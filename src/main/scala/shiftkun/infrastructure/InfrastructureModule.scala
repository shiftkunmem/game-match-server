package shiftkun.infrastructure

import shiftkun.ConcurrencyContexts
import akka.actor.ActorSystem
import shiftkun.lib.auth.impl.{DummyAuthenticator, JwtAuthenticator}
import shiftkun.application.{SampleAppQuery, UserAppService}
import shiftkun.AppConfig
import shiftkun.domain.model.UserRepository
import shiftkun.domain.service.{LineUserIdService, UserService}
import shiftkun.infrastructure.domain.service.JdbcLineUserIdService
import shiftkun.infrastructure.repository.JdbcUserRepository
import shiftkun.lib.db.{DBContext, ScalikeJdbcContext}

import scala.concurrent.ExecutionContext

trait InfrastructureModule { self: ConcurrencyContexts =>

  import com.softwaremill.macwire._

  implicit private val ec: ExecutionContext = infrastructure
  implicit private val system: ActorSystem = infrastructureSystem

  lazy val sampleAppQuery: SampleAppQuery = wire[JdbcSampleAppQuery]
  lazy val userRepository: UserRepository = wire[JdbcUserRepository]
  lazy val lineUserIdService: LineUserIdService = wire[JdbcLineUserIdService]

  lazy val dbCtx: DBContext = {
    implicit val ec = infrastructure
    new ScalikeJdbcContext()
  }

  //起動時に初期化する必要があるので、lazyはつけない
  //val authenticator =new JwtAuthenticator()
  val authenticator = DummyAuthenticator
    //(AppConfig.paasAuthUri, AppConfig.newAuthUri) match {
//      case (Some(uri1), Some(uri2)) => new JwtOrPaasAuthenticator(uri1, uri2)
//      case (None, None) => DummyAuthenticator
//      case _ => throw new IllegalArgumentException
//    }

}
