name := "term-explorer"

lazy val root = project in file(".")

Global / onChangedBuildSource := ReloadOnSourceChanges

scalaVersion := "2.13.5"

libraryDependencies += "de.unruh" %% "scala-isabelle" % "master-SNAPSHOT"

Compile / packageBin / artifactPath := baseDirectory.value / "term-explorer-component.jar"

val isabelleHome = file("/opt/Isabelle2021-1-RC5/")
Compile / unmanagedJars ++= ((isabelleHome / "lib" / "classes" +++ isabelleHome / "contrib") ** "*.jar").classpath

Compile / unmanagedJars ++= (file("../qrhl-tool/scala-isabelle/component") * "*.jar").classpath
