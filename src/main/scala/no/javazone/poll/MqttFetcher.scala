package no.javazone.poll

import no.javazone.poll.storage.StorageService
import org.eclipse.paho.client.mqttv3.{IMqttDeliveryToken, MqttCallback, MqttClient, MqttMessage}

import scala.concurrent.duration.{DurationLong, FiniteDuration}
import scala.language.postfixOps
import scalaz.\/

class MqttFetcher(mqtt: MqttConfig, storage: StorageService) {
  private val retryDur: Seq[FiniteDuration] = Seq(100 millis, 200 millis, 500 millis)

  private val client: MqttClient = new MqttClient(s"tcp://${mqtt.url}:${mqtt.port}", "PollAgg")

  client.setCallback(new MqttCallback {


    override def deliveryComplete(token: IMqttDeliveryToken): Unit = {}
    override def messageArrived(topic: String, message: MqttMessage): Unit = {
      val msg = Message(topic, message)

      def logErrors: (\/[Throwable, Any]) => Unit = {
        r => r.swap.toOption.foreach(_.printStackTrace())
      }
      msg match {
        case o @ OnlineMessage(_,_,_) => storage.onlineEvent(o).retry(retryDur).runAsync(logErrors)
        case v @ VoteMessage(_, _, _) => storage.voteEvent(v).retry(retryDur).runAsync(logErrors)
        case s @ SerialLabelMessage(_, _, _) => storage.setBoxLabel(s).runAsync(logErrors)
        case u => println(s"unhandled msg: $u")
      }
    }

    override def connectionLost(cause: Throwable): Unit = {
      println(s"connection lost ${cause.getMessage}")
      cause.printStackTrace()
    }
  })

  def connectToServer(): Unit = {
    println(s"Connecting to server ${client.getServerURI}")
    client.connect()
    client.subscribe("pollerbox/#")
  }

  def connected: Boolean = {
    client.isConnected
  }
}

