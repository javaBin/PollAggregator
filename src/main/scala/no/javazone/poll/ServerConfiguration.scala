package no.javazone.poll

import scala.util.Properties._

case class MqttConfig(
    url: String,
    port: Int)

case class DatabaseConfig(
    driver: String = "org.postgresql.Driver",
    host: String = "localhost",
    port: Int = 5432,
    database: String = "devnull",
    username: String = "devnull",
    password: String = "devnull") {
  val connectionUrl = s"jdbc:postgresql://$host:$port/$database"
}

case class ServerConfiguration(
    mqtt: MqttConfig,
    db: DatabaseConfig )

object DefaultConfig {

  def apply(): ServerConfiguration = {
    val mqttUrl: String = propsOrEnv("mqtt.url").get
    val mqttPort: Int = propsOrEnv("mqtt.port").map(_.toInt).getOrElse(1883)


    ServerConfiguration(
      MqttConfig(mqttUrl, mqttPort),
      DatabaseConfigEnv())
  }

  private def propsOrEnv(key: String): Option[String] =
    propOrNone(key).orElse(envOrNone(key))

}

object DatabaseConfigEnv {

  def apply(): DatabaseConfig = {
    DatabaseConfig(
      database = propOrElse("dbName", envOrElse("DB_NAME", "pollzone")),
      username = propOrElse("dbUsername", envOrElse("DB_USERNAME", "pollzone")),
      password = propOrElse("dbPassword", envOrElse("DB_PASSWORD", "pollzone"))
    )
  }
}
