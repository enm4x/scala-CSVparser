name := "CSVreader"

version := "0.1"

scalaVersion := "2.13.1"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.8.1",

  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.11"
)
