val paradiseVersion = "2.1.0"

name := "stagedb"

version := "1.0"

lazy val commonSettings = Seq(
  scalaVersion := "2.11.8",
  libraryDependencies += "ch.epfl.data" %% "squid" % "0.1.1-SNAPSHOT",
  libraryDependencies ++= Seq(
    "junit" % "junit-dep" % "4.10" % "test",
    "org.scalatest" % "scalatest_2.11" % "2.2.0" % "test"
  ),
  autoCompilerPlugins := true,
  addCompilerPlugin("org.scalamacros" % "paradise" % paradiseVersion cross CrossVersion.full),
  scalacOptions ++= Seq("-feature", "-language:postfixOps", "-unchecked")
)

lazy val main = (project in file("."))
    .settings(commonSettings: _*)
    .aggregate(macros)
    .dependsOn(macros)

lazy val macros = (project in file("macros"))
    .settings(commonSettings: _*)
    .settings(
      name := "macros"
    )

