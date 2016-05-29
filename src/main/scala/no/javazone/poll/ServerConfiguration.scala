package no.javazone.poll

import scala.util.Properties

case class ServerConfiguration(mqttUrl: String, mqttPort: Int)

object DefaultConfig {

  def apply(): ServerConfiguration = {
    val mqttUrl: String = propsOrEnv("mqtt.url").get
    val mqttPort: Int = propsOrEnv("mqtt.port").map(_.toInt).getOrElse(1883)

    ServerConfiguration(mqttUrl, mqttPort)
  }

  private def propsOrEnv(key: String): Option[String] = {
    Properties.propOrNone(key).orElse(
      Properties.envOrNone(key)
    )
  }

}
