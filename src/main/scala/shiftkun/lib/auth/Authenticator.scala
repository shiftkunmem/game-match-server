package shiftkun.lib.auth

import akka.http.scaladsl.server.directives.Credentials

import scala.concurrent.Future

trait Authenticator {
  def authenticate(credentials: Credentials): Future[Option[AuthContext]]
}

object Authenticator {
  val DummyAuth = AuthContext("サンプルマン", "sample1020", None)
}


