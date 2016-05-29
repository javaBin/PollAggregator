package no.javazone.poll

object AggregatorMain extends App {

  println("Starting app")
  private val fetcher: MqttFetcher = new MqttFetcher("trygvis.io", 1883)

  while (true) {
    if (!fetcher.connected) {
      fetcher.connectToServer()
    }
    Thread.sleep(10)
  }
  println("Stopped App")
}
