package no.javazone.poll

import org.eclipse.paho.client.mqttv3.{IMqttDeliveryToken, MqttCallback, MqttClient, MqttMessage}

class MqttFetcher(url: String, port: Int) {
  private val client: MqttClient = new MqttClient(s"tcp://$url:$port", "PollAgg")

  client.setCallback(new MqttCallback {

    override def deliveryComplete(token: IMqttDeliveryToken): Unit = {}

    override def messageArrived(topic: String, message: MqttMessage): Unit = {
      val msg = Message(topic, message)
      println(s"msg: $msg")
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

