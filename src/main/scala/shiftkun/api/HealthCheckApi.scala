package shiftkun.api

import akka.http.scaladsl.server.Route
import shiftkun.api.common.ApiSupport


class HealthCheckApi extends ApiSupport {

  lazy val routes: Route =

    pathSingleSlash {
      get {
        complete("shiftkun is running !")
      }
    } ~
      path("healthcheck") {
        get {
          complete("service is running")
        }
      }
}
