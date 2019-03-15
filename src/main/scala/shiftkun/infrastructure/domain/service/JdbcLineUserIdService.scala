package shiftkun.infrastructure.domain.service

import scalikejdbc.DB
import shiftkun.domain.service.interpreter.LineUserIdServiceInterpreter
import tables.UserRecord
import scalikejdbc._
import shiftkun.domain.model.LineUserId
import cats.syntax.either._
import shiftkun.domain.DomainError.LineUserIdDuplicated
import shiftkun.lib.TypeConversions._

import scala.concurrent.ExecutionContext
import shiftkun.infrastructure.Codes._
import shiftkun.lib.TypeAlias.MaybeErrors

class JdbcLineUserIdService(implicit ec: ExecutionContext) extends LineUserIdServiceInterpreter{
  override def checkDuplicateById(id: String): MaybeErrors[LineUserId] =

    DB readOnly { implicit s =>

      val u = UserRecord.syntax("u")

      withSQL {
        select
          .from(UserRecord as u)
          .where
          .eq(u.line_id, id)
      }.map(UserRecord(u.resultName)).single.apply().map{u =>
        u.status match {
          case UserStatusActive => err(LineUserIdDuplicated).asLeft
          case UserStatusInactive => LineUserId(id).asRight
        }
      }.getOrElse(LineUserId(id).asRight)
    }
}
