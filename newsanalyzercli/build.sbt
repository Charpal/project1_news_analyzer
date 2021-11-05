import Dependencies._

ThisBuild / scalaVersion     := "2.11.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"


lazy val root = (project in file("."))
  .settings(
    name := "newsanalyzercli",
    libraryDependencies += scalaTest % Test,
    libraryDependencies += "org.apache.hadoop" % "hadoop-client" % "2.7.0",
    libraryDependencies += "net.liftweb" %% "lift-json" % "2.6",
    //libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.2.0",
    // resolvers += Resolver.sonatypeRepo("releases"),
    // libraryDependencies += "com.danielasfregola" %% "twitter4s" % "7.0",
)

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.

// libraryDependencies += "net.liftweb" %% "lift-json" % "2.5.1"
// libraryDependencies += "net.liftweb" % "lift-webkit_2.11" % "3.1.0"
