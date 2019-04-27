package shiftkun.lib

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

import cats.{Functor, Id}
import cats.syntax.functor._
import com.typesafe.scalalogging.Logger

trait LoggingSupport {

  implicit val logger: Logger = Logger(this.getClass)

  def withPerfLog[T](msg: => String)(f: => T): T =
    withPerfLogF[T, Id](msg)(f)

  def withPerfLogF[T, F[_]: Functor](msg: => String)(f: => F[T]): F[T] = {
    val start = LocalDateTime.now
    logger.debug(s"start $msg")

    for {
      r <- f
    } yield {
      val time = ChronoUnit.MILLIS.between(start, LocalDateTime.now)
      logger.debug(s"end $msg in ($time ms)")
      r
    }
  }

}
