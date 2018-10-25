import akka.actor. {Actor, ActorLogging, Props, ActorSystem}
import scala.io.StdIn

object IotSuperVisor {
  def props(): Props = Props(new IotSuperVisor)
}

class IotSuperVisor extends Actor with ActorLogging {
  override def preStart(): Unit = log.info("IoT App Started")
  override def postStop(): Unit = log.info("IoT Application stopped")

  override def receive = Actor.emptyBehavior
}

object IotApp extends App {
  override def main(args: Array[String]): Unit = {
    val system = ActorSystem("iot-system")

    try {
      val supervisor = system.actorOf(IotSuperVisor.props(), "iot-supervisor")
      StdIn.readLine()
    } finally {
      system.terminate()
    }
  }
}


