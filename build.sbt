ThisBuild / scalaVersion     := "2.13.4"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "rs-faas-demo",
  )

libraryDependencies ++= Seq(
    "com.amazonaws" % "aws-lambda-java-core" % "1.2.1",
    "com.amazonaws" % "aws-lambda-java-events" % "3.8.0",
    "org.scalatest" %% "scalatest" % "3.2.2",
    "com.softwaremill.sttp.client3" %% "core" % "3.3.0-RC2",
    "com.amazonaws" % "aws-java-sdk-dynamodb" % "1.11.999"
  )

val circeVersion = "0.12.3"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

assemblyJarName in assembly := "rs-faas-demo.jar"

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
