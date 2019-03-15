package shiftkun.lib.auth

case class AuthContext (
  lineUserId: String,
  name: String,
  picture: Option[String]
)
