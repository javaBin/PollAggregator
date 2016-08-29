package no.javazone.poll

import doobie.contrib.hikari.hikaritransactor.HikariTransactor
import doobie.imports.Transactor
import no.javazone.poll.storage.{Migration, StorageService}

import scalaz.concurrent.Task

object AggregatorMain extends App {

  println("Starting app")
  private val config: ServerConfiguration = DefaultConfig()

  Migration.runMigration(config.db)

  val xa = for {
    xa <- HikariTransactor[Task](
      config.db.driver, config.db.connectionUrl, config.db.username, config.db.password)
    _ <- xa.configure(hxa =>
      Task.delay {
        hxa.setMaximumPoolSize(10)
      })
  } yield xa

  private val run: Transactor[Task] = xa.run
  private val fetcher: MqttFetcher = new MqttFetcher(
    config.mqtt,
    new StorageService(xa.run))

  while (true) {
    if (!fetcher.connected) {
      fetcher.connectToServer()
    }
    Thread.sleep(10)
  }
  println("Stopped App")
}
