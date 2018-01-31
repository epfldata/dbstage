val paradiseVersion = "2.1.0"

name := "dbstage"

version := "0.2"

lazy val commonSettings = Seq(
  scalaVersion := "2.11.11",
  libraryDependencies += "ch.epfl.data" %% "squid" % "0.3.0-SNAPSHOT",
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  ),
  libraryDependencies ++= Seq(
    "junit" % "junit-dep" % "4.10" % "test",
    "org.scalatest" % "scalatest_2.11" % "2.2.0" % "test",
    "com.chuusai" %% "shapeless" % "2.3.2"
  ),
  libraryDependencies += "org.typelevel" %% "cats-core" % "1.0.1",
  autoCompilerPlugins := true,
  addCompilerPlugin("org.scalamacros" % "paradise" % paradiseVersion cross CrossVersion.full),
  addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.4"),
  scalacOptions ++= Seq("-feature", "-language:postfixOps", "-unchecked", "-deprecation"
    //, "-Xprint:typer", "-Xlog-implicits"
  ),
  scalacOptions += "-Ypartial-unification" // for Cats (https://github.com/typelevel/cats#getting-started)
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

