package shiftkun.domain.model

case class Group(
  id: GroupId = GroupId(0L),//DBで採番される値
  name: String,
  status: GroupStatus
)

case class GroupId(value: Long) extends AnyVal

sealed trait GroupStatus
object GroupStatus {
  case object Active extends GroupStatus
  case object Inactive extends GroupStatus
}