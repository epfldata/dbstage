val paradiseVersion = "2.1.0"

lazy val commonSettings = Seq(
  version := "0.1",
  scalaVersion := "2.12.10",
  organization := "ch.epfl.data",
  libraryDependencies += "ch.epfl.data" %% "squid" % "0.4.1-SNAPSHOT",
  libraryDependencies += "org.scalactic" %% "scalactic" % "3.1.1",
  libraryDependencies += "com.lihaoyi" %% "os-lib" % "0.6.2",
  libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.1" % "test",
  autoCompilerPlugins := true,
  addCompilerPlugin("org.scalamacros" % "paradise" % paradiseVersion cross CrossVersion.full),
  scalacOptions ++= Seq("-feature", "-language:postfixOps", "-unchecked")
)

lazy val dbstage = (project in file("dbstage"))
  .settings(commonSettings: _*)
  .settings(
    name := "dbstage",
  )
  .dependsOn(macros)

lazy val macros = (project in file("macros"))
  .settings(commonSettings: _*)
  .settings(
    name := "dbstage-macros"
  )

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .aggregate(macros)
  .aggregate(dbstage)
