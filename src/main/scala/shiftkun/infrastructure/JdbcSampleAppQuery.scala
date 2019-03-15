package shiftkun.infrastructure
import tables._
import shiftkun.application.SampleAppQuery
import shiftkun.lib.ProcessResult
import shiftkun.lib.TypeAlias.ProcessResult
import scalikejdbc.{DB, _}

import scala.concurrent.ExecutionContext.Implicits.global

class JdbcSampleAppQuery extends SampleAppQuery {
  override def temp: ProcessResult[String] =  ProcessResult.success {
    DB readOnly { implicit s =>



//      val sa = wpTermsRecord.syntax("sa")
      val tr = WpTermsRecord.syntax("tr")

      val temp = withSQL {select.from(WpTermsRecord as tr).where.eq(tr.term_id, 1L)}.map(WpTermsRecord(tr.resultName)).single.apply().map { tr => tr.name }.getOrElse("ないよ")
      temp
    }



//    ProcessResult.success("temp")
  }
}
