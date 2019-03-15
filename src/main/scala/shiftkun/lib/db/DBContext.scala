package shiftkun.lib.db

import shiftkun.lib.TypeAlias.ProcessResult

import scala.concurrent.Future

trait DBContext {
  def tx[T](f: DBOperation => Future[T]): Future[T]
  def tx[T](f: DBOperation => ProcessResult[T]): ProcessResult[T]
  def read[T](f: DBOperation => ProcessResult[T]): ProcessResult[T]
}

trait DBOperation
