package shiftkun.domain.service

import shiftkun.domain.model.{LineUserId, User}
import shiftkun.lib.TypeAlias.MaybeErrors
import shiftkun.lib.auth.AuthContext

trait UserService {
  def create(lid: LineUserId)(implicit auth: AuthContext): MaybeErrors[User]
}
