organization := "com.example"
name := "project-a"
version := "0.1.0-SNAPSHOT"
scalaVersion := "3.8.4"

libraryDependencies ++= Seq(
  ("io.netty" % "netty-bom" % "4.2.20.Final").pomOnly(),
  // version provided by netty-bom
  "io.netty" % "netty-handler" % "*"
)
