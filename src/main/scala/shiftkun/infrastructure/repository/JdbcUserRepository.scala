package shiftkun.infrastructure.repository
import cats.syntax.all._
import shiftkun.domain.model._
import shiftkun.lib.db.{DBOperation, UseScalikeJdbc}
import shiftkun.lib.ProcessResult
import shiftkun.lib.TypeAlias.ProcessResult
import shiftkun.lib.TypeConversions._

import scala.concurrent.ExecutionContext
import scalikejdbc.{DB, _}
import shiftkun.domain.DomainError.{ClientNotFound, UserNotFound}
import shiftkun.infrastructure.Codes
import shiftkun.lib.auth.AuthContext
import tables._

import scala.util.Try



class JdbcUserRepository(
  implicit ec: ExecutionContext
) extends UserRepository
  with UseScalikeJdbc {

  @SuppressWarnings(Array("OptionGet"))
  override def add(user: User)(implicit op: DBOperation,auth: AuthContext): ProcessResult[User] = ProcessResult {
    Try {
      exec { implicit s =>
        lazy val ucols = UserRecord.column
        val newUserId: Long = applyUpdateAndReturnGeneratedKey {
          insert.into(UserRecord).namedValues(
            ucols.name -> user.name,
            ucols.line_id -> user.lineUserId.value,
            ucols.picture -> user.picture,
            ucols.status -> 0
          )
        }
        user.copy(id = UserId(newUserId))
      }
    }.toEither.leftMap { e => err(ClientNotFound) }
  }

  override def get(id: UserId)(implicit op: DBOperation, auth: AuthContext): ProcessResult[User] = ProcessResult{
    DB readOnly { implicit s =>

      val u = UserRecord.syntax("u")

      val result = withSQL {
        select
          .from(UserRecord as u)
          .where
          .eq(u.user_id, id.value)
          .and.eq(u.status, Codes.UserStatusActive)
      }.map(UserRecord(u.resultName)).single.apply().map{u =>
        User(
          UserId(u.user_id),
          LineUserId("1"),
          "temp",
          None,
          UserStatus.Active
        )
      }

      Either.fromOption(
        result,
        err(UserNotFound)
      )
    }
  }
}