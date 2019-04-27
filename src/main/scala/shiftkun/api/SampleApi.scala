package shiftkun.api

import shiftkun.application.AppServiceModule
import cats.instances.future._
import shiftkun.api.codec.ApiErrorMapper
import shiftkun.api.common.ApiSupport
import akka.http.scaladsl.server.Route

class SampleApi(val module: AppServiceModule)
  extends ApiSupport
    with ApiErrorMapper {
  def routes: Route = postSample

  import module._

  def postSample: Route = {
    path("sample") {
      options {
        //complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
//        val result =
//          for {
//            temp <- sampleAppQuery.temp
//          } yield temp
        val r = "成功です！"
//        response(result) { r =>
          complete(r)
//        }
      }
    }
  }
}
