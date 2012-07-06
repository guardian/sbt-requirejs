sbtPlugin := true

name := "sbt-requirejs"

organization := "com.gu"

libraryDependencies ++= Seq(
    "rhino" % "js" % "1.7R2",
    "com.codahale" %% "jerkson" % "0.5.0"
)