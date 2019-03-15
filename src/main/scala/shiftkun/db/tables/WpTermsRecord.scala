package tables

import scalikejdbc._
case class WpTermsRecord(
  term_id: Long,
  name: String,
  slug: String,
  term_group: Long
)

object WpTermsRecord extends SQLSyntaxSupport[WpTermsRecord] {
  override val tableName = "wp_terms"
  override val columns = Seq("term_id", "name", "slug", "term_group")

  def apply(rn: ResultName[WpTermsRecord])(rs: WrappedResultSet): WpTermsRecord =
    autoConstruct(rs, rn)
}