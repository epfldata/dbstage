val paradiseVersion = "2.1.0"

name := "dbstage"

version := "0.2"

lazy val commonSettings = Seq(
  scalaVersion := "2.11.11",
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  ),
  libraryDependencies ++= Seq(
    "ch.epfl.data" %% "squid" % "0.3.0-SNAPSHOT",
    //"com.chuusai" %% "shapeless" % "2.3.2",
    "org.typelevel" %% "cats-core" % "1.0.1",
    "junit" % "junit-dep" % "4.10" % "test",
    "org.scalatest" % "scalatest_2.11" % "2.2.0" % "test"
  ),
  autoCompilerPlugins := true,
  addCompilerPlugin("org.scalamacros" % "paradise" % paradiseVersion cross CrossVersion.full),
  addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.4"),
  scalacOptions ++= Seq("-feature", "-language:postfixOps", "-unchecked", "-deprecation"
    //, "-Xprint:typer", "-Xlog-implicits"
    //, "-Yliteral-types" // yields error:  bad option: '-Yliteral-types'
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

