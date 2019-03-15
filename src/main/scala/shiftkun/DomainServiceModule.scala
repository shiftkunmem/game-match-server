package shiftkun


import shiftkun.domain.service.UserService
import shiftkun.domain.service.interpreter.UserServiceInterpreter

import scala.concurrent.ExecutionContext


trait DomainServiceModule { self: ConcurrencyContexts =>
  import com.softwaremill.macwire._
  implicit val ec: ExecutionContext = domain

  lazy val userService: UserService = wire[UserServiceInterpreter]
}
