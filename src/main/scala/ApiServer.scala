import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.host
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.stream.ActorMaterializer
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import scalikejdbc.config.DBs
import shiftkun.lib.auth.AuthContext
import shiftkun.{ConcurrencyContexts, DomainServiceModule}
import shiftkun.api.{HealthCheckApi, SampleApi, SwaggerDocApi, UserApi}
import shiftkun.api.common.{ErrorInfo, ErrorOutput}
import shiftkun.application.AppServiceModule
import shiftkun.infrastructure.InfrastructureModule
import shiftkun.lib.LoggingSupport

import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.io.StdIn

object ApiServer {
    def main(args: Array[String]) {

        DBs.setupAll()

        val server = new ApiServer()
        Await.result(
            server.start("0.0.0.0", 8080),
            Duration.Inf
        )

        scala.sys.addShutdownHook {
            Await.result(server.stop(), 30.seconds)
            DBs.closeAll()
        }

    }
}


class ApiServer extends LoggingSupport {

    import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
    import akka.http.scaladsl.model.HttpMethods._

    implicit val system = ActorSystem()

    def start(host: String, port: Int): Future[_] = {
        implicit val system = ActorSystem("my-system")
        implicit val materializer = ActorMaterializer()
        implicit val executionContext = system.dispatcher

        lazy val module =
            new AppServiceModule
              with InfrastructureModule
              with DomainServiceModule
              with ConcurrencyContexts {
                override def app = system.dispatchers.lookup("app.non-blocking-dispatcher")

                override def infrastructure: ExecutionContext = system.dispatchers.lookup("app.infrastructure-dispatcher")

                override def domain = system.dispatchers.lookup("app.non-blocking-dispatcher")

                override def infrastructureSystem: ActorSystem = system
            }


        val routes = {
            import akka.http.scaladsl.model.headers._
            import akka.http.scaladsl.server.Directives._
            import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
            import io.circe.generic.auto._

            val mainRoutes: AuthContext => Route = { implicit auth =>
                new SampleApi(module).routes ~
                  new UserApi(module).routes ~
                  new HealthCheckApi().routes ~
                  cors() {
                      SwaggerDocApi.routes
                  }
            }

            import akka.http.scaladsl.server.Directives._
            import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
            import io.circe.generic.auto._
            val exceptionHandler = ExceptionHandler {
                case e: Exception =>
                    logger.error(s"Exception occurred in ${this.getClass.getSimpleName}", e)
                    complete((
                      StatusCodes.InternalServerError,
                      ErrorOutput(Seq(ErrorInfo("UnexpectedError", e.getMessage)))
                    ))
            }

            val corsSettings = CorsSettings.defaultSettings.copy(
                allowedMethods = scala.collection.immutable.Seq(GET, POST, PUT, DELETE, HEAD, OPTIONS)
            )

            handleExceptions(exceptionHandler) {
                authenticateOAuth2Async[AuthContext](realm = "ASaaS", module.authenticator.authenticate) { implicit auth: AuthContext =>
                    cors(corsSettings) {
                        extractRequestContext { ctx =>
                            logger.debug("request={},user={}", ctx.request, "2")
                            mainRoutes(auth).andThen {
                                _.map { result =>
                                    logger.debug("response={},user={}", result, "2")
                                    result
                                }
                            }
                        }
                    }
                }
            }
        }

        for {
            sb <- Http().bindAndHandle(routes, host, port)
        } yield {
            logger.info("AccountingZ api server is running...")
            sb
        }
    }
        def stop(): Future[_] =
            system.terminate()
}
