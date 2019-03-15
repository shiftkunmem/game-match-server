package tables

import scalikejdbc._
case class GroupRecord(
  group_id: Long,
  name: String,
  status: Int
)

object GroupRecord extends SQLSyntaxSupport[GroupRecord] {
  override val tableName = "Group"
  override val columns = Seq("group_id", "name", "status")

  def apply(rn: ResultName[GroupRecord])(rs: WrappedResultSet): GroupRecord =
    autoConstruct(rs, rn)
}