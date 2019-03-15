package shiftkun.domain.service

import shiftkun.domain.model.LineUserId
import shiftkun.lib.TypeAlias.MaybeErrors
import shiftkun.lib.auth.AuthContext

trait LineUserIdService {
  def create(implicit auth: AuthContext): MaybeErrors[LineUserId]
}
