import scala.util.Properties

val commonSettings = Seq(
  organization := "no.javazone.poll",
  scalaVersion := "2.11.8",
  name := "PollAggregator",
  crossScalaVersions := Seq("2.11.8"),
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
  appmgrLauncher in Appmgr := (appmgrLauncher in Appmgr).value.map(_.copy(command = "aggregatormain", name = "pollaggregator")),
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

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

initialize := {
  val _ = initialize.value
  if (sys.props("java.specification.version") != "1.8")
    sys.error("Java 8 is required for this project.")
}

lazy val doobieVersion = "0.2.4"

val database = Seq(
  "org.flywaydb"   %  "flyway-core"               % "4.0.3",
  "org.tpolecat"   %% "doobie-core"               % doobieVersion  withSources(),
  "org.tpolecat"   %% "doobie-contrib-postgresql" % doobieVersion  withSources()  exclude("postgresql", "postgresql"),
  "org.tpolecat"   %% "doobie-contrib-hikari"     % doobieVersion  withSources(),
  "com.zaxxer"     %  "HikariCP"                  % "2.4.7"
)

lazy val http4sVersion = "0.14.4"

val http4s = Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion
)

lazy val app = (project in file(".")).
    settings(commonSettings).
    settings(Revolver.settings).
    settings(libraryDependencies ++= Seq(
      "org.eclipse.paho" % "org.eclipse.paho.client.mqttv3" % "1.0.2",
      "org.scalatest" %% "scalatest" % "2.2.6" % "test"
    ) ++ database ++ http4s)


enablePlugins(BuildInfoPlugin)

buildInfoPackage := "pollaggregator"


buildInfoKeys := Seq[BuildInfoKey](
  scalaVersion,
  BuildInfoKey.action("version") { (version in ThisBuild ).value },
  BuildInfoKey.action("buildTime") { System.currentTimeMillis },
  BuildInfoKey.action("branch"){ Git.branch },
  BuildInfoKey.action("sha"){ Git.sha }
)