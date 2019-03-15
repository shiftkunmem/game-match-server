package shiftkun.lib

import cats.data.{EitherT, NonEmptyList}
import cats.syntax.either._
import cats.syntax.option._
import cats.instances.future._
import scala.concurrent.{ExecutionContext, Future}

object TypeAlias {

  type Errors = NonEmptyList[ErrorType]

  type ProcessResult[T] = EitherT[Future, Errors, T]

  type MaybeErrors[T] = Either[Errors, T]

}

object ProcessResult {
  import TypeAlias._

  def apply[T](f: => MaybeErrors[T])(implicit ec: ExecutionContext): ProcessResult[T] =
    EitherT {
      Future {
        f
      }
    }

  def success[T](f: => T)(implicit ec: ExecutionContext): ProcessResult[T] =
    EitherT {
      Future {
        f.asRight
      }
    }


  def wrap[T](f: => Either[ErrorType, T]): ProcessResult[T] =
    EitherT {
      Future.successful {
        f match {
          case Right(r) => r.asRight
          case Left(e) => NonEmptyList.of(e).asLeft
        }
      }
    }

  def wrapE[T](f: => MaybeErrors[T]): ProcessResult[T] =
    EitherT {
      Future.successful {
        f match {
          case Right(r) => r.asRight
          case Left(es) => es.asLeft
        }
      }
    }

  def optWrapE[T](f: => Option[MaybeErrors[T]]): ProcessResult[Option[T]] =
    EitherT {
      Future.successful {
        f match {
          case Some(Right(r)) => r.some.asRight
          case Some(Left(es)) => es.asLeft
          case None => none.asRight
        }
      }
    }

  def wrapSuccess[T](f: => T): ProcessResult[T] =
    EitherT {
      Future.successful{
        f.asRight
      }
    }

  def opt[T](value: => Option[ProcessResult[T]])(implicit ec: ExecutionContext): ProcessResult[Option[T]] =
    value.map { _.map(_.some) }.getOrElse(wrapSuccess(None))


  def join[A, B](
    aF: ProcessResult[A],
    bF: ProcessResult[B]
  )(implicit ec: ExecutionContext): ProcessResult[(A, B)] =
    for {
      a <- aF
      b <- bF
    } yield (a, b)

  def join3[A, B, C](
    aF: ProcessResult[A],
    bF: ProcessResult[B],
    cF: ProcessResult[C]
  )(implicit ec: ExecutionContext): ProcessResult[(A, B, C)] =
    for {
      a <- aF
      b <- bF
      c <- cF
    } yield (a, b, c)

  def join4[A, B, C, D](
    aF: ProcessResult[A],
    bF: ProcessResult[B],
    cF: ProcessResult[C],
    dF: ProcessResult[D]
  )(implicit ec: ExecutionContext): ProcessResult[(A, B, C, D)] =
    for {
      a <- aF
      b <- bF
      c <- cF
      d <- dF
    } yield (a, b, c, d)

  def join5[A, B, C, D, E](
    aF: ProcessResult[A],
    bF: ProcessResult[B],
    cF: ProcessResult[C],
    dF: ProcessResult[D],
    eF: ProcessResult[E]
  )(implicit ec: ExecutionContext): ProcessResult[(A, B, C, D, E)] =
    for {
      a <- aF
      b <- bF
      c <- cF
      d <- dF
      e <- eF
    } yield (a, b, c, d, e)

  def join6[A, B, C, D, E, F](
    aF: ProcessResult[A],
    bF: ProcessResult[B],
    cF: ProcessResult[C],
    dF: ProcessResult[D],
    eF: ProcessResult[E],
    fF: ProcessResult[F]
  )(implicit ec: ExecutionContext): ProcessResult[(A, B, C, D, E, F)] =
    for {
      a <- aF
      b <- bF
      c <- cF
      d <- dF
      e <- eF
      f <- fF
    } yield (a, b, c, d, e, f)

  def join7[A, B, C, D, E, F, G](
    aF: ProcessResult[A],
    bF: ProcessResult[B],
    cF: ProcessResult[C],
    dF: ProcessResult[D],
    eF: ProcessResult[E],
    fF: ProcessResult[F],
    gF: ProcessResult[G],
  )(implicit ec: ExecutionContext): ProcessResult[(A, B, C, D, E, F, G)] =
    for {
      a <- aF
      b <- bF
      c <- cF
      d <- dF
      e <- eF
      f <- fF
      g <- gF
    } yield (a, b, c, d, e, f, g)

  def future[T](f: => Future[T])(implicit ec: ExecutionContext): ProcessResult[T] =
    EitherT { f.map(_.asRight) }

}
