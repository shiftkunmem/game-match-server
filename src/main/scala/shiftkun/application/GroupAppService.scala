package shiftkun.application

import shiftkun.domain.model._
import shiftkun.domain.service.{LineUserIdService, UserService}
import shiftkun.lib.ProcessResult
import shiftkun.lib.TypeAlias.ProcessResult
import shiftkun.lib.auth.AuthContext
import shiftkun.lib.db.DBContext
import cats.instances.future._

import scala.concurrent.ExecutionContext

class GroupAppService(
  userRepository: UserRepository,
  lineUserIdService: LineUserIdService,
  userService: UserService,
  dbCtx: DBContext,
)(implicit ec: ExecutionContext) {

  def registerGroup(userId: UserId)(implicit auth: AuthContext): ProcessResult[Group] = dbCtx.tx { implicit op =>
    for {
      user <- userRepository.get(userId)
    } yield {
      Group(name = "tetete", status = GroupStatus.Active)
    }
  }
}

