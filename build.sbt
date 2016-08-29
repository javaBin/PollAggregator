name := "PollAggregator"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.8"

lazy val doobieVersion = "0.2.4"

val database = Seq(
  "org.flywaydb"   %  "flyway-core"               % "4.0.3",
  "org.tpolecat"   %% "doobie-core"               % doobieVersion  withSources(),
  "org.tpolecat"   %% "doobie-contrib-postgresql" % doobieVersion  withSources()  exclude("postgresql", "postgresql"),
  "org.tpolecat"   %% "doobie-contrib-hikari"     % doobieVersion  withSources(),
  "com.zaxxer"     %  "HikariCP"                  % "2.4.7"
)

libraryDependencies ++= Seq(
  "org.eclipse.paho" % "org.eclipse.paho.client.mqttv3" % "1.0.2",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test"
) ++ database

