package no.javazone.poll

import no.javazone.poll.storage.Migration

object AggregatorMain extends App {

  println("Starting app")
  private val config: ServerConfiguration = DefaultConfig()

  Migration.runMigration(config.db)

  private val fetcher: MqttFetcher = new MqttFetcher(
    config.mqtt.url,
    config.mqtt.port)

  while (true) {
    if (!fetcher.connected) {
      fetcher.connectToServer()
    }
    Thread.sleep(10)
  }
  println("Stopped App")
}
