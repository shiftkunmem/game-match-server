package shiftkun.domain

import shiftkun.lib.ErrorType

object DomainError {


  case object LineUserIdDuplicated extends ErrorType
  case object InvalidAuth extends ErrorType
  case object ClientNotFound extends ErrorType

}
