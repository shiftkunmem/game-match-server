package tables

import scalikejdbc._
case class UserRecord(
  user_id: Long,
  name: String,
  line_id: String,
  picture: Option[String] = None,
  status: Int
)

object UserRecord extends SQLSyntaxSupport[UserRecord] {
  override val tableName = "User"
  override val columns = Seq("user_id", "name", "line_id", "picture", "status")

  def apply(rn: ResultName[UserRecord])(rs: WrappedResultSet): UserRecord =
    autoConstruct(rs, rn)
}