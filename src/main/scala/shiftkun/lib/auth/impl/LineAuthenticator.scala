package shiftkun.lib.auth.impl

import java.nio.charset.StandardCharsets
import java.util.Base64

import akka.http.scaladsl.server.{Directive0, Directive1, ValidationRejection}
import akka.http.scaladsl.server.Directives._
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import spray.json._
import DefaultJsonProtocol._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

object LineAuthenticator {
  def verifyLINESignature: Directive0  = (headerValueByName("X-Line-Signature") & entity(as[String])).tflatMap {
    case (signature, body) if LineSignatureVerifier.isValid("ab830f3e315bf62ed9b7672acd75b3f2", body, signature) => pass
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



sealed trait Event {
  val `type`: String
  val timestamp: Long
  val source: Source
}
case class Events(events: List[Event])
sealed trait Message {
  val `type`: String
}
case class MessageEvent(replyToken: String, timestamp: Long, source: Source, message: Message) extends Event {
  override val `type`: String = "message"
}
sealed trait Source {
  val `type`: String
}
case class TextMessage(id: String, text: String) extends Message {
  override val `type`: String = "text"
}
case class UserSource(userId: String) extends Source {
  override val `type`: String = "user"
}



trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit object SourceJsonFormat extends RootJsonFormat[Source] {
    def write(obj: Source): JsValue = obj match {
      case user: UserSource => JsObject(
        "type" -> JsString(user.`type`),
        "userId" -> JsString(user.userId)
      )
    }
    def read(json: JsValue): Source = json.asJsObject.getFields("type", "userId") match {
      case Seq(JsString("user"), JsString(userId)) => UserSource(userId)
      case _ => deserializationError("Source expected")
    }
  }
  implicit object MessageJsonFormat extends RootJsonFormat[Message] {
    def write(obj: Message): JsValue = obj match {
      case textMessage: TextMessage => JsObject(
        "type" -> JsString(textMessage.`type`),
        "id" -> JsString(textMessage.id),
        "text" -> JsString(textMessage.text)
      )
    }
    def read(json: JsValue): Message = json.asJsObject.getFields("type", "id", "text") match {
      case Seq(JsString("text"), JsString(id), JsString(text)) => TextMessage(id, text)
      case _ => deserializationError("Message expected")
    }
  }
  implicit object EventJsonFormat extends RootJsonFormat[Event] {
    def write(obj: Event): JsValue = obj match {
      case messageEvent: MessageEvent => JsObject(
        "type" -> JsString(messageEvent.`type`),
        "timestamp" -> JsNumber(messageEvent.timestamp),
        "source" -> SourceJsonFormat.write(messageEvent.source),
        "replyToken" -> JsString(messageEvent.replyToken),
        "message" -> MessageJsonFormat.write(messageEvent.message)
      )
    }
    def read(json: JsValue): Event = {
      json.asJsObject.getFields("type", "timestamp", "source", "replyToken", "message") match {
        case Seq(JsString("message"), JsNumber(timestamp), source, JsString(replyToken), message) =>
          MessageEvent(replyToken, timestamp.toLong, SourceJsonFormat.read(source), MessageJsonFormat.read(message))
        case _ => deserializationError("Event expected")
      }
    }
  }
  implicit val EventsJsonFormat = jsonFormat1(Events)
}