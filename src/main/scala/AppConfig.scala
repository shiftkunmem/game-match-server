package shiftkun

import com.typesafe.config.ConfigFactory

import scala.util.Try

trait AppConfig {

  private val config = ConfigFactory.load()

  import config._

  lazy val exposeApiSpec: Boolean =
    opt(getBoolean("app.exposeApiSpec")).getOrElse(false)

  lazy val paasAuthUri: Option[String] =
    opt(getString("app.paasAuthUri"))

  lazy val newAuthUri: Option[String] =
    opt(getString("app.newAuthUri"))

  lazy val compatibility: Option[String] =
    opt(getString("app.compatibility"))


  private def opt[T](value: => T): Option[T] =
    Try(value).toOption

}

object AppConfig extends AppConfig
