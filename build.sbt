lazy val `benchmark-scalazasyncio` = project

lazy val `benchmark-Main` = project.dependsOn(`benchmark-scalazasyncio`, `benchmark-designpatternasyncio`, lite)

lazy val `benchmark-designpatternasyncio` = project.dependsOn(designpattern)

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
