scalaVersion := "2.11.12"

// Set to false or remove if you want to show stubs as linking errors
nativeLinkStubs := true

enablePlugins(ScalaNativePlugin)

nativeLinkingOptions ++= Seq("-L" +
  "/usr/local/Cellar/lmdb/0.9.24/lib/")
