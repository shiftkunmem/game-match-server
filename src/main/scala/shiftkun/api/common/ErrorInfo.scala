package shiftkun.api.common

case class ErrorOutput(
  errors: Seq[ErrorInfo]
)

case class ErrorInfo(
  key: String,
  message: String,
  fields: Seq[String] = Seq.empty
)