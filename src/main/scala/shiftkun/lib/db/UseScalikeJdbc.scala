package shiftkun.lib.db

import scalikejdbc.DBSession

trait UseScalikeJdbc {

  def exec[T](f: DBSession => T)(implicit op: DBOperation): T =
    op match {
      case ScalikeJdbcOperation(s) => f(s)
      case _ => throw new UnsupportedOperationException
    }

}