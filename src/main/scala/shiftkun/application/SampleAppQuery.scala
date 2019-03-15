package shiftkun.application

import shiftkun.lib.TypeAlias.ProcessResult
import scala.concurrent.ExecutionContext

trait SampleAppQuery {
  def temp: ProcessResult[String]
}