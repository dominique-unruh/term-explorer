name := "term-explorer"

lazy val root = project in file(".")

Global / onChangedBuildSource := ReloadOnSourceChanges

scalaVersion := "2.13.7"

libraryDependencies += "de.unruh" %% "scala-isabelle" % "master-SNAPSHOT"
