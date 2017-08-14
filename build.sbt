val paradiseVersion = "2.1.0"

name := "stagedb"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies += "ch.epfl.data" %% "squid" % "0.1.1-SNAPSHOT"

autoCompilerPlugins := true

addCompilerPlugin("org.scalamacros" % "paradise" % paradiseVersion cross CrossVersion.full)

scalacOptions ++= Seq("-feature", "-language:postfixOps")



