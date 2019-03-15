package tables

import scalikejdbc._
case class MemberRecord(
  member_id: Long,
  user_id: Long,
  group_id: Long,
  admin_flag: Int,
  status: Int
)

object MemberRecord extends SQLSyntaxSupport[MemberRecord] {
  override val tableName = "Member"
  override val columns = Seq("member_id", "user_id", "group_id", "admin_flag", "status")

  def apply(rn: ResultName[MemberRecord])(rs: WrappedResultSet): MemberRecord =
    autoConstruct(rs, rn)
}