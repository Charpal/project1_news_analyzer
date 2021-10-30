import Dependencies._

ThisBuild / scalaVersion     := "2.13.0"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

resolvers += Resolver.sonatypeRepo("releases")



libraryDependencies ++= Seq(
  "com.danielasfregola" %% "twitter4s"      % "7.0",
  "ch.qos.logback"      % "logback-classic" % "1.2.3"
)



lazy val root = (project in file("."))
  .settings(
    name := "newsanalyzercli",
    libraryDependencies += scalaTest % Test,
    // resolvers += Resolver.sonatypeRepo("releases"),
    // libraryDependencies += "com.danielasfregola" %% "twitter4s" % "7.0",
)


// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.

// libraryDependencies += "net.liftweb" %% "lift-json" % "2.5.1"
// libraryDependencies += "net.liftweb" % "lift-webkit_2.11" % "3.1.0"
