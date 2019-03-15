package shiftkun.lib.db


import cats.data.{EitherT, NonEmptyList}
import shiftkun.lib.ErrorType
import shiftkun.lib.TypeAlias.ProcessResult
import scalikejdbc._

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success, Try}

class ScalikeJdbcContext(implicit val ec: ExecutionContext) extends DBContext {
  import ScalikeJdbcContext._

  override def tx[T](f: DBOperation => Future[T]): Future[T] = {
    DB futureLocalTx { session =>
      f(ScalikeJdbcOperation(session))
    }
  }

  override def tx[T](f: DBOperation => ProcessResult[T]): ProcessResult[T] = {
    implicit val txBoundary: TxBoundary[ProcessResult[T]] = processResultTxBoundary[T]
    DB localTx { session =>
      f(ScalikeJdbcOperation(session))
    }
  }

  override def read[T](f: DBOperation => ProcessResult[T]): ProcessResult[T] = {
    f(ScalikeJdbcOperation(ReadOnlyAutoSession))
  }
}

object ScalikeJdbcContext {

  def processResultTxBoundary[T](implicit ec: ExecutionContext): TxBoundary[ProcessResult[T]] = new TxBoundary[ProcessResult[T]] {
    def finishTx(result: ProcessResult[T], tx: Tx): ProcessResult[T] = {
      val p = Promise[Either[NonEmptyList[ErrorType], T]]
      result.value.onComplete { r =>
        p.complete(Try {
          r match {
            case Success(Right(_)) => tx.commit()
            case Success(Left(_)) => tx.rollback()
            case Failure(e) => tx.rollback()
          }
        }.transform (
          _ => r,
          finishError =>
            Failure(r match {
              case Success(_) => finishError
              case Failure(resultError) =>
                resultError.addSuppressed(finishError)
                resultError
            })
        ))
      }

      EitherT(p.future)
    }

    override def closeConnection(result: ProcessResult[T], doClose: () => Unit): ProcessResult[T] = {
      val p = Promise[Either[NonEmptyList[ErrorType], T]]
      result.value.onComplete { r =>
        p.complete(Try {
          doClose()
        }.transform (
          _ => r,
          finishError =>
            Failure(r match {
              case Success(_) => finishError
              case Failure(resultError) =>
                resultError.addSuppressed(finishError)
                resultError
            })
        ))
      }

      EitherT(p.future)
    }

  }

}

case class ScalikeJdbcOperation(
  session: DBSession
) extends DBOperation
