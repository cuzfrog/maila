import java.nio.file.Files

import sbt.Keys._

shellPrompt in ThisBuild := { state => Project.extract(state).currentRef.project + "> " }

lazy val commonSettings = Seq(
  organization := "com.github.cuzfrog",
  scalaVersion := "2.11.8",
  logBuffered in Test := false,
  scalacOptions ++= Seq("-unchecked", "-feature"),
  libraryDependencies ++= Seq(
    "ch.qos.logback" % "logback-classic" % "1.1.7" % "test",
    "junit" % "junit" % "4.12" % "test",
    "com.novocode" % "junit-interface" % "0.11" % "test->default",
    "org.scalacheck" %% "scalacheck" % "1.13.2" % "test",
    "com.icegreen" % "greenmail" % "1.5.1" % "test",
    "org.mockito" % "mockito-core" % "1.10.19" % "test"
  )
)

lazy val root = (project in file(".")).disablePlugins(AssemblyPlugin)
  .settings(commonSettings: _*)
  .settings(
    name := "maila",
    version := "0.2.3",
    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % "1.3.0",
      "com.sun.mail" % "javax.mail" % "1.5.5",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.4.0" % "test"
    ),
    reColors := Seq("magenta"),
    publishTo := Some("My Bintray" at "https://api.bintray.com/maven/cuzfrog/maven/maila/;publish=1"),
    credentials += Credentials("Bintray API Realm", "api.bintray.com", "BINTRAY_USER", "BINTRAY_PASS"),
    compile in Compile <<= (compile in Compile) dependsOn versionReadme,
    versionReadme := {
      val contents = IO.read(file("README.md"))
      val regex ="""(?<=libraryDependencies \+= "com\.github\.cuzfrog" %% "maila" % ")[\d\w\-\.]+(?=")"""
      val newContents = contents.replaceAll(regex, version.value)
      IO.write(file("README.md"), newContents)
    }
  )



lazy val batchMailTool = (project in file("./bmt"))
  .settings(commonSettings: _*)
  .settings(
    name := "batch-mail-tool",
    version := "0.2.3",
    libraryDependencies ++= Seq(
    ),
    mainClass in assembly := Some("com.github.cuzfrog.tool.bmt.CmdUi"),
    mainClass in(Compile, packageBin) := (mainClass in assembly).value,
    assembly <<= assembly dependsOn generateBat,
    assembly <<= assembly dependsOn copyApp,
    generateBat := {
      val file = crossTarget.value / "bmt.bat"
      val contents = s"@echo off${System.lineSeparator}java -jar %CD%\\batch-mail-tool-assembly-${version.value}.jar %*"
      IO.write(file, contents)
    },
    copyApp := {
      crossTarget.value.listFiles().foreach(f => if (f.isFile) Files.delete(f.toPath))
      new File(baseDirectory.value, "app").listFiles().foreach(
        file => Files.copy(file.toPath, new File(crossTarget.value, file.name).toPath)
      )
    },
    cleanAll := {
    }
  ).dependsOn(root)

lazy val generateBat = TaskKey[Unit]("generate-bat", "Generate a bat file for window shell.")
lazy val copyApp = TaskKey[Unit]("copy-app", "Copy app files to target.")
lazy val cleanAll = TaskKey[Unit]("clean-all", "Clean all files in target folders.")
lazy val versionReadme = TaskKey[Unit]("version-readme", "Update version in README.MD")
addCommandAlias("bmt", "batchMailTool/run")

