package shiftkun.api.common

import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server._
import com.typesafe.scalalogging.Logger
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import shiftkun.lib.ErrorType
import shiftkun.lib.TypeAlias.ProcessResult

import scala.util._

trait ApiSupport
  extends Directives
    with FailFastCirceSupport { self =>

  lazy val logger: Logger = Logger(this.getClass)

  @SuppressWarnings(Array("TraversableHead"))
  def response[T](
    f: => ProcessResult[T],
    statusMapping: PartialFunction[ErrorType, StatusCode] = PartialFunction.empty
  )(g: T => Route)(
    implicit errorMapping: ErrorType => (StatusCode, ErrorInfo)
  ): Route =
    onComplete(f.value) {
      case Success(Right(r)) =>
        g(r)

      case Success(Left(errors)) =>
        val apiErrors = errors.map(errorMapping).toList
        val status = statusMapping.orElse[ErrorType, StatusCode] {
          case _ => apiErrors.head._1
        }(errors.head)

        if (apiErrors.exists(_._1 == StatusCodes.InternalServerError))
          logger.error(s"System Error occurred by $errors")
        else
          logger.debug(s"App Error occurred by $errors")

        complete((status, ErrorOutput(apiErrors.map(_._2))))

      case Failure(e) =>
        logger.error(s"Exception occurred in ${this.getClass.getSimpleName}", e)
        complete((
          StatusCodes.InternalServerError,
          ErrorOutput(Seq(ErrorInfo("UnexpectedError", e.getMessage)))
        ))
    }
}
