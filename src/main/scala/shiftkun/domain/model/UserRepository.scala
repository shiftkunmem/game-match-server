package shiftkun.domain.model

import java.time.LocalDateTime

import shiftkun.lib.db.DBOperation
import shiftkun.lib.TypeAlias.ProcessResult
import shiftkun.lib.auth.AuthContext

trait UserRepository {

  def add(
    user: User
  )(implicit op: DBOperation, auth: AuthContext): ProcessResult[User]

  def get(
    id: UserId
  )(implicit op: DBOperation, auth: AuthContext): ProcessResult[User]

}
