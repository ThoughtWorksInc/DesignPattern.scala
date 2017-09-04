lazy val `benchmark-compiletime-scalaz` = project.dependsOn(`benchmark-asyncio-scalaz`)

lazy val `benchmark-compiletime-designpattern` = project.dependsOn(`benchmark-asyncio-designpattern`)

lazy val `benchmark-asyncio-scalaz` = project

lazy val `benchmark-Main` = project.dependsOn(`benchmark-asyncio-scalaz`, `benchmark-asyncio-designpattern`, lite)

lazy val `benchmark-asyncio-designpattern` = project.dependsOn(designpattern)

lazy val designpattern = project

lazy val lite = project

lazy val unidoc = project
  .enablePlugins(StandaloneUnidoc, TravisUnidocTitle)
  .settings(
    UnidocKeys.unidocProjectFilter in ScalaUnidoc in UnidocKeys.unidoc := {
      inAggregates(LocalRootProject)
    },
    addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.3"),
    scalacOptions += "-Xexperimental"
  )
