package shiftkun.lib.auth.impl

import java.nio.charset.StandardCharsets
import java.util.Base64

import akka.http.scaladsl.server.{Directive0, Directive1, ValidationRejection}
import akka.http.scaladsl.server.Directives._
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object LineAuthenticator {
  def verifyLINESignature: Directive1[String]  = (headerValueByName("X-Line-Signature") & entity(as[String])).tflatMap {
    case (signature, body) if LineSignatureVerifier.isValid("ab830f3e315bf62ed9b7672acd75b3f2", body, signature) => provide("test")
    case _ => reject(ValidationRejection("Invalid signature"))
  }
}

object LineSignatureVerifier {
  def isValid(channelSecret: String, bodyString: String, signature: String): Boolean = {
    val key = new SecretKeySpec(channelSecret.getBytes, "HmacSHA256")
    val mac = Mac.getInstance("HmacSHA256")
    val source = bodyString.getBytes(StandardCharsets.UTF_8)
    mac.init(key)
    Base64.getEncoder.encodeToString(mac.doFinal(source)) == signature
  }
}
