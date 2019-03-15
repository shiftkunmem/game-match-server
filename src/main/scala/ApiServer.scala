import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.stream.ActorMaterializer
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import scalikejdbc.config.DBs
import shiftkun.lib.auth.AuthContext
import shiftkun.{ConcurrencyContexts, DomainServiceModule}
import shiftkun.api.{SampleApi, SwaggerDocApi, UserApi}
import shiftkun.api.common.{ErrorInfo, ErrorOutput}
import shiftkun.application.AppServiceModule
import shiftkun.infrastructure.InfrastructureModule

import scala.concurrent.ExecutionContext
import scala.io.StdIn

object ApiServer {
    def main(args: Array[String]) {

        DBs.setupAll()

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
                      cors() { SwaggerDocApi.routes }
                }

            import akka.http.scaladsl.server.Directives._
            import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
            import io.circe.generic.auto._
            val exceptionHandler = ExceptionHandler {
                case e: Exception =>
                    complete((
                      StatusCodes.InternalServerError,
                      ErrorOutput(Seq(ErrorInfo("UnexpectedError", e.getMessage)))
                    ))
            }


            handleExceptions(exceptionHandler) {
                authenticateOAuth2Async[AuthContext](realm = "ASaaS", module.authenticator.authenticate) { implicit auth: AuthContext =>
                    mainRoutes(auth)
                }
            }
        }

        val bindingFuture = Http().bindAndHandle(routes, "localhost", 8080)

        println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
        StdIn.readLine() // let it run until user presses return
        bindingFuture
            .flatMap(_.unbind()) // trigger unbinding from the port
            .onComplete(_ => system.terminate()) // and shutdown when done
    }
}
