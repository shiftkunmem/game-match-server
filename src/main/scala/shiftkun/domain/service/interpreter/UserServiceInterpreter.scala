package shiftkun.domain.service.interpreter

import shiftkun.domain.model.{LineUserId, User, UserStatus}
import shiftkun.domain.service.UserService
import shiftkun.lib.TypeAlias.MaybeErrors
import shiftkun.lib.auth.AuthContext
import cats.syntax.either._


class UserServiceInterpreter extends UserService {
  override def create(lid: LineUserId)(implicit auth: AuthContext): MaybeErrors[User] = {
    //validationが増えるかもしれないので
    User(
      name = auth.name,
      lineUserId = lid,
      picture = auth.picture,
      status = UserStatus.Active
    ).asRight
  }
}