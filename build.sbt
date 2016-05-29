name := "PollAggregator"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.eclipse.paho" % "org.eclipse.paho.client.mqttv3" % "1.0.2",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test"
)
