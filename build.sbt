val paradiseVersion = "2.1.0"

name := "dbstage"

version := "0.2"

lazy val commonSettings = Seq(
  scalaVersion := "2.12.6",
  resolvers += Resolver.sonatypeRepo("snapshots"),
  resolvers += Resolver.sonatypeRepo("releases"),
  autoCompilerPlugins := true,
  addCompilerPlugin("org.scalamacros" % "paradise" % paradiseVersion cross CrossVersion.full),
  addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.4"),
  scalacOptions ++= Seq("-feature", "-language:postfixOps", "-unchecked", "-deprecation"
    //, "-language:higherKinds" // not yet supported by Squid, so better warn against it
    //, "-Xprint:typer", "-Xlog-implicits"
    //, "-Yliteral-types" // yields error:  bad option: '-Yliteral-types'
  ),
  scalacOptions += "-Ypartial-unification", // for Cats (https://github.com/typelevel/cats#getting-started)
  
  libraryDependencies += "ch.epfl.data" %% "squid" % "0.3.1-SNAPSHOT",
  libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.5",
  libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  //libraryDependencies += "com.chuusai" %% "shapeless" % "2.3.2",
  libraryDependencies += "org.typelevel" %% "cats-core" % "1.0.1",
  // libraryDependencies += "com.lihaoyi" %% "sourcecode" % "0.1.4",
  
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

