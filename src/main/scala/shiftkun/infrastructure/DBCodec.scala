package shiftkun.infrastructure

import shiftkun.domain.model.UserStatus

object DBCodec {

  def userStatus(value: Int): UserStatus =
    value match {
      case 0 => UserStatus.Active
      case 1 => UserStatus.Inactive
      case x => throw new IllegalArgumentException(s"unexpected userStatus: $x")
    }

}