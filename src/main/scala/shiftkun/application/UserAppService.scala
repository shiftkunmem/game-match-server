package shiftkun.application

import shiftkun.domain.model.{LineUserId, User, UserId, UserRepository}
import shiftkun.domain.service.{LineUserIdService, UserService}
import shiftkun.lib.ProcessResult
import shiftkun.lib.TypeAlias.ProcessResult
import shiftkun.lib.auth.AuthContext
import shiftkun.lib.db.DBContext
import cats.instances.future._

import scala.concurrent.ExecutionContext

class UserAppService(
  userRepository: UserRepository,
  lineUserIdService: LineUserIdService,
  userService: UserService,
  dbCtx: DBContext,
)(implicit ec: ExecutionContext) {

  def registerUser(implicit auth: AuthContext): ProcessResult[User] = dbCtx.tx { implicit op =>
    for {
      lineUserId <- ProcessResult.wrapE(lineUserIdService.create)
      user <- ProcessResult.wrapE(userService.create(lineUserId))
      result <- userRepository.add(user)
    } yield {
      result
    }
  }
}

