package shiftkun.api.codec

import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import shiftkun.api.common.ErrorInfo
import shiftkun.domain.DomainError
import shiftkun.lib.ErrorType

trait ApiErrorMapper {

  implicit val errorMapping: ErrorType => (StatusCode, ErrorInfo) = { e =>
    val errorKey = key(e)
    e match {
      case DomainError.LineUserIdDuplicated =>
        (StatusCodes.BadRequest, ErrorInfo(errorKey, "既に登録済みのユーザーです", Seq("line_user_id")))
      case DomainError.InvalidAuth =>
        (StatusCodes.BadRequest, ErrorInfo(errorKey, "不正な認証情報です"))
      case _ =>
        (StatusCodes.InternalServerError, ErrorInfo("UnexpectedError", "予期せぬエラーが発生しました"))
    }
  }

  private def key(e: ErrorType): String =
    e.getClass.getSimpleName.replaceAll("\\$", "")

}

object ApiErrorMapper extends ApiErrorMapper
