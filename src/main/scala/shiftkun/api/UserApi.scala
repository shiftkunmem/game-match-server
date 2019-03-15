package shiftkun.api

import shiftkun.application.AppServiceModule
import shiftkun.api.codec.ApiErrorMapper
import shiftkun.api.common.ApiSupport
import shiftkun.lib.auth.AuthContext
import akka.http.scaladsl.server.Route
import cats.instances.future._

import scala.concurrent.ExecutionContext

class UserApi(val module: AppServiceModule)(implicit ec: ExecutionContext)
  extends ApiSupport
    with ApiErrorMapper {
  def routes(implicit auth: AuthContext): Route = registerUser

  import module._

  def registerUser(implicit auth:AuthContext): Route = {
    path("user") {
      post {
        val result =
          for {
            user <- userAppService.registerUser
          } yield user
        response(result) { r =>
          complete(r.id.value)
        }
      }
    }
  }
}
