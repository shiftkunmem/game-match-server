package shiftkun.application

import shiftkun.ConcurrencyContexts
import shiftkun.domain.model.UserRepository
import shiftkun.domain.service.interpreter.UserServiceInterpreter
import shiftkun.domain.service.{LineUserIdService, UserService}
import shiftkun.lib.db.DBContext

trait AppServiceModule { self: ConcurrencyContexts =>
  import com.softwaremill.macwire._


  implicit private val ec = app

  def sampleAppQuery: SampleAppQuery
  def lineUserIdService: LineUserIdService

  lazy val userAppService: UserAppService = wire[UserAppService]

  protected def userRepository: UserRepository
  protected def userService: UserService
  protected def dbCtx: DBContext
}
