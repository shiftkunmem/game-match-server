package shiftkun

import scala.concurrent.ExecutionContext
import akka.actor.ActorSystem

trait ConcurrencyContexts {
  def app: ExecutionContext
  def infrastructure: ExecutionContext
  def domain: ExecutionContext

  def infrastructureSystem: ActorSystem
}

