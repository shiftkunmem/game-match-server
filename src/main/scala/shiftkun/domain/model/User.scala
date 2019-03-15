package shiftkun.domain.model

case class User(
  id: UserId = UserId(0L),//DBで採番される値
  lineUserId: LineUserId,
  name: String,
  picture: Option[String],
  status: UserStatus
)

case class UserId(value: Long) extends AnyVal
case class LineUserId(value: String) extends AnyVal

sealed trait UserStatus
object UserStatus {
  case object Active extends UserStatus
  case object Inactive extends UserStatus
}