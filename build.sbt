import sbt.Tests.Setup

name := "samza-approximation-store"

def appVersion() = sys.env.getOrElse("GO_PIPELINE_LABEL", "0.1.0-SNAPSHOT")

val samzaVersion = "0.10.0.11-indix"
val samzaCore = "org.apache.samza" % "samza-core_2.10" % samzaVersion
val samzaKafka = "org.apache.samza" % "samza-kafka_2.10" % samzaVersion
val samzaKV = "org.apache.samza" % "samza-kv_2.10" % samzaVersion
val samzaLog4j = "org.apache.samza" % "samza-log4j" % samzaVersion
val slf4jLog4j12 = "org.slf4j" % "slf4j-log4j12" % "1.6.2" % "runtime"
val samzaTest = "org.apache.samza" % "samza-test_2.10" % samzaVersion
val guava = "com.google.guava" % "guava" % "18.0"

val scalaTest = "org.scalatest" %% "scalatest" % "2.2.4" % "test"
val mockito = "org.mockito" % "mockito-all" % "1.9.5"

val excludeDepsForLive = Seq("junit.*", "jets3t.*")

lazy val commonSettings = Seq(
  organization := "samza.contrib.store",
  version := appVersion(),
  scalaVersion := "2.10.4",
  fork in run := false,
  resolvers ++= Seq(
    Resolver.sonatypeRepo("snapshots"),
    "Indix Maven" at "http://artifacts.indix.tv:8081/artifactory/libs-release",
    "morphia.googlecode.com" at "http://morphia.googlecode.com/svn/mavenrepo/"
  ),
  testOptions += Setup( cl =>
      cl.loadClass("org.slf4j.LoggerFactory").
        getMethod("getLogger",cl.loadClass("java.lang.String")).
        invoke(null,"ROOT")
  ),
  parallelExecution in This := false,
  publishMavenStyle := true,
  crossPaths := true,
  publishArtifact in Test := false,
  publishArtifact in(Compile, packageDoc) := false,
  publishArtifact in(Compile, packageSrc) := true,

  publishTo <<= version { v =>
    if (appVersion().endsWith("-SNAPSHOT"))
      Some("Indix Snapshot Artifactory" at "http://artifacts.indix.tv:8081/artifactory/libs-snapshot-local")
    else
      Some("Indix Release Artifactory" at "http://artifacts.indix.tv:8081/artifactory/libs-release-local")
  },
  credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "samza-approximation-store",
    libraryDependencies ++= Seq(
      samzaCore, samzaKV, samzaLog4j, slf4jLog4j12, guava, scalaTest, mockito
    )
  )  
