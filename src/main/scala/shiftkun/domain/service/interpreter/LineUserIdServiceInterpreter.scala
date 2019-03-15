package shiftkun.domain.service.interpreter

import shiftkun.domain.model.LineUserId
import shiftkun.domain.service.LineUserIdService
import shiftkun.lib.TypeAlias.MaybeErrors
import shiftkun.lib.auth.AuthContext


trait LineUserIdServiceInterpreter extends LineUserIdService {
  override def create(implicit auth: AuthContext): MaybeErrors[LineUserId] = {
    //現状これだけだが、今後細かいvalidationとかが入ってきそうなので
    checkDuplicateById(auth.lineUserId)
  }

  def checkDuplicateById(id: String): MaybeErrors[LineUserId]
}