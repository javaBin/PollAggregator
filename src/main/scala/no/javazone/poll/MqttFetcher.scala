package no.javazone.poll

import no.javazone.poll.storage.StorageService
import org.eclipse.paho.client.mqttv3.{IMqttDeliveryToken, MqttCallback, MqttClient, MqttMessage}

import scalaz.\/

class MqttFetcher(mqtt: MqttConfig, storage: StorageService) {
  private val client: MqttClient = new MqttClient(s"tcp://${mqtt.url}:${mqtt.port}", "PollAgg")

  client.setCallback(new MqttCallback {

    override def deliveryComplete(token: IMqttDeliveryToken): Unit = {}

    override def messageArrived(topic: String, message: MqttMessage): Unit = {
      val msg = Message(topic, message)
      println(s"received msg: $msg")

      def logErrors: (\/[Throwable, Unit]) => Unit = {
        r => r.swap.toOption.foreach(_.printStackTrace())
      }
      msg match {
        case o @ OnlineMessage(_,_,_) => storage.onlineEvent(o).runAsync(logErrors)
        case v @ VoteMessage(_, _, _) => storage.voteEvent(v).run
        case u => println("unhandled msg: $u")
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

