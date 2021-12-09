import sbt.Compile

name := "term-explorer"

// File .isabelle-home must contain the path to the Isabelle-installation
lazy val isabelleHome = file(IO.read(file(".isabelle-home")).trim)

Global / onChangedBuildSource := ReloadOnSourceChanges

def assertExists(file: File) = { assert(file.exists(), file); file }

scalaVersion := "2.13.5"

Compile / packageBin / artifactPath := baseDirectory.value / "term-explorer-component.jar"

libraryDependencies += "de.unruh" %% "scala-isabelle" % "master-SNAPSHOT"
Compile / unmanagedJars += assertExists(file("../qrhl-tool/scala-isabelle/component/scala-isabelle-component.jar"))
Compile / unmanagedJars += assertExists(isabelleHome / "lib" / "classes" / "isabelle.jar")


/*
lazy val component = project
  .dependsOn(root)
  .settings(
    scalaVersion := "2.13.5",
    Compile / packageBin / artifactPath := baseDirectory.value / "classes" / "term-explorer-component.jar",
    Compile / unmanagedJars ++= ((isabelleHome / "lib" / "classes" +++ isabelleHome / "contrib") ** "*.jar").classpath,
    Compile / unmanagedJars ++= (file("../qrhl-tool/scala-isabelle/component") * "*.jar").classpath,
    Compile / packageBin := {
      IO.copyFile((Compile/(root/packageBin)).value, baseDirectory.value / "classes" / "term-exporer.jar")
      (Compile/packageBin).value
    }
  )
*/


