package shiftkun.api

import shiftkun.application.AppServiceModule
import shiftkun.api.codec.ApiErrorMapper
import shiftkun.api.common.ApiSupport
import shiftkun.lib.auth.AuthContext
import akka.http.scaladsl.server.Route
import cats.instances.future._
import shiftkun.api.UserApi.{UserGetOutput, UserQueryResult}
import shiftkun.domain.model.{LineUserId, User, UserId, UserStatus}
import shiftkun.lib.ProcessResult
import io.circe.generic.auto._
import shiftkun.lib.auth.impl.LineAuthenticator

import scala.concurrent.ExecutionContext

class UserApi(val module: AppServiceModule)(implicit ec: ExecutionContext)
  extends ApiSupport
    with ApiErrorMapper {
  def routes(implicit auth: AuthContext): Route = registerUser ~ getUser

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

  def sampleUser(implicit auth:AuthContext): Route = {
    path("line" / "callback") {
      (post & LineAuthenticator.verifyLINESignature) {a =>
//        val result =
//          for {
//            user <- userAppService.registerUser
//          } yield user
//        response(result) { r =>
          complete(a)
//        }
      }
    }
  }

  def getUser(implicit auth: AuthContext): Route = {
    path("user") {
      get {
        val result = ProcessResult.success {
          UserGetOutput(
            UserQueryResult(
              id = 0L,
              lineUserId = "tempId",
              name = "sampleくん",
              picture = "http:sssssssssssssssssiconicon",
              status = "active"
            )
          )
        }
        response(result) { r =>
          complete(r)
        }
      }
    }
  }
}

object UserApi {

  case class UserGetOutput(
    user: UserQueryResult
  )

  case class UserQueryResult(
    id: Long,
    lineUserId: String,
    name: String,
    picture: String,
    status: String,
  )
}