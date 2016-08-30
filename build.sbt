import scala.util.Properties

val commonSettings = Seq(
  organization := "no.javazone.poll",
  scalaVersion := "2.11.8",
  name := "PollAggregator",
  crossScalaVersions := Seq("2.11.7"),
  scalacOptions := Seq("-deprecation", "-feature"),
  pomIncludeRepository := {
    x => false
  },
  crossPaths := false,
  publishTo <<= (version) apply {
    (v: String) => if (v.trim().endsWith("SNAPSHOT")) {
      Some("JavaBin Nexus repo" at "http://nye.java.no/nexus/content/repositories/snapshots")
    }
    else {
      Some("JavaBin Nexus repo" at "http://nye.java.no/nexus/content/repositories/releases")
    }
  },
  credentials ++= {
    val cred = Path.userHome / ".sbt" / "javabin.credentials"
    if (cred.exists) Seq(Credentials(cred)) else Nil
  },
  target in App := target.value / "appmgr" / "root",
  packageBin in Appmgr <<= (packageBin in Appmgr).dependsOn(packageBin in App),
  appmgrLauncher in Appmgr := (appmgrLauncher in Appmgr).value.map(_.copy(command = "jetty", name = "poll-aggregator")),
  aether.AetherKeys.aetherArtifact <<= (aether.AetherKeys.aetherArtifact, (packageBin in Appmgr)) map { (art, build) =>
    art.attach(build, "appmgr", "zip")
  },
  credentials ++= {
    (for {
      username <- Properties.envOrNone("NEXUS_USERNAME")
      password <- Properties.envOrNone("NEXUS_PASSWORD")
    } yield
      Credentials(
        "Sonatype Nexus Repository Manager",
        "nye.java.no",
        username,
        password
      )).toSeq
  }

) ++ overridePublishBothSettings

lazy val doobieVersion = "0.2.4"

val database = Seq(
  "org.flywaydb"   %  "flyway-core"               % "4.0.3",
  "org.tpolecat"   %% "doobie-core"               % doobieVersion  withSources(),
  "org.tpolecat"   %% "doobie-contrib-postgresql" % doobieVersion  withSources()  exclude("postgresql", "postgresql"),
  "org.tpolecat"   %% "doobie-contrib-hikari"     % doobieVersion  withSources(),
  "com.zaxxer"     %  "HikariCP"                  % "2.4.7"
)

lazy val app = (project in file(".")).
    settings(commonSettings).
    settings(Revolver.settings).
    settings(libraryDependencies ++= Seq(
      "org.eclipse.paho" % "org.eclipse.paho.client.mqttv3" % "1.0.2",
      "org.scalatest" %% "scalatest" % "2.2.6" % "test"
    ) ++ database)


enablePlugins(BuildInfoPlugin)

buildInfoPackage := "pollaggregator"


buildInfoKeys := Seq[BuildInfoKey](
  scalaVersion,
  BuildInfoKey.action("version") { (version in ThisBuild ).value },
  BuildInfoKey.action("buildTime") { System.currentTimeMillis },
  BuildInfoKey.action("branch"){ Git.branch },
  BuildInfoKey.action("sha"){ Git.sha }
)