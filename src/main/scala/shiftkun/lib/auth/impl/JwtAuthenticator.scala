package shiftkun.lib.auth.impl

import akka.actor.{Actor, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.directives.Credentials
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.pattern._
import akka.stream.ActorMaterializer
import akka.util.{ByteString, Timeout}
import cats.syntax.either._
import cats.syntax.option._
import io.circe.parser.parse
import shiftkun.lib.TypeConversions._

import scala.concurrent.{Await, ExecutionContext, Future}
import io.circe.generic.auto._

import scala.concurrent.duration._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import shiftkun.lib.auth.{AuthContext, Authenticator}
import shiftkun.lib.{ErrorType, ProcessResult}
import shiftkun.lib.TypeAlias.MaybeErrors
import sun.util.logging.LoggingSupport

import scala.concurrent.duration._
import pdi.jwt.{Jwt, JwtAlgorithm}
import pdi.jwt.JwtAlgorithm._
import pdi.jwt.algorithms.{JwtECDSAAlgorithm, JwtHmacAlgorithm, JwtRSAAlgorithm}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import cats.instances.future._
import cats.syntax.either._
import cats.syntax.option._

class JwtAuthenticator(implicit
  ec: ExecutionContext,
  system: ActorSystem
) extends Authenticator {
  import JwtAuthenticator._

  implicit val materializer: ActorMaterializer = ActorMaterializer()

  override def authenticate(credentials: Credentials): Future[Option[AuthContext]] = {
    credentials match {
      case Credentials.Provided(token) if isJwtFormat(token) => validateJwt(token)
      case _ => Future { None }
    }
  }

  private def validateJwt(jwt: String): Future[Option[AuthContext]] = {
//    val temp3 = Jwt.decodeAll(token = jwt, key = "3ff520c7f57e643a606676a2ff4beaad",algorithms = Seq(JwtAlgorithm.HS256))
    (for {
      decodedJwt <- ProcessResult.wrapE(Jwt.decode(token = jwt, key = "3ff520c7f57e643a606676a2ff4beaad",algorithms = Seq(JwtAlgorithm.HS256)).toEither.leftMap(_ => err(InvalidJsonWebToken)))
      input <- ProcessResult.wrap {parse(decodedJwt).right.flatMap(_.as[JwtBody]).leftMap(_ => AuthError)}
      auth <- ProcessResult.wrapE {
        AuthContext(lineUserId = input.sub, name = input.name, picture = input.picture.some).asRight
      }
    } yield auth).value.map(_.toOption)
  }
}


object JwtAuthenticator {

  def isJwtFormat(str: String): Boolean =
    str.split("\\.").length == 3

  case object AuthError extends ErrorType
  case object InvalidJsonWebToken extends ErrorType
  case object InvalidRequest extends ErrorType
  case object InvalidJsonSessionId extends ErrorType

  case class JwtBody(
    iss: String,//IDトークンが生成されたURL 	https://access.line.me
    sub: String,//ユーザーID
    aud: String,//チャンネルID
    exp: Long,//UNIX時間でのトークンの有効期限
    iat: Long,//IDトークンがUNIX時刻で生成された時刻,
    name: String,//ユーザー表示名
    picture: String //写真のアドレス
  )
}


object DummyAuthenticator extends Authenticator {
  override def authenticate(credentials: Credentials): Future[Option[AuthContext]] =
    Future.successful { Some(Authenticator.DummyAuth) }
}


