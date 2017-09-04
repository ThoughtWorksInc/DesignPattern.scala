lazy val `benchmark-scalazasyncio` = project

lazy val `benchmark-Main` = project.dependsOn(`benchmark-scalazasyncio`, `benchmark-designpatternasyncio`, lite)

lazy val `benchmark-designpatternasyncio` = project.dependsOn(designpattern)

lazy val designpattern = project

lazy val lite = project
