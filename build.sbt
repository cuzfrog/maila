import sbt.Keys._

shellPrompt in ThisBuild := { state => Project.extract(state).currentRef.project + "> " }
resolvers ++= Seq(
  "Local Maven Repository" at """file:///""" + Path.userHome.absolutePath +"""\.m2\repository""",
  "bintray-cuzfrog-maven" at "http://dl.bintray.com/cuzfrog/maven",
  "Artima Maven Repository" at "http://repo.artima.com/releases"
)


lazy val commonSettings = Seq(
  organization := "com.github.cuzfrog",
  scalaVersion := "2.11.8",
  logBuffered in Test := false,
  scalacOptions ++= Seq("-unchecked", "-feature"),
  libraryDependencies ++= Seq(
    "junit" % "junit" % "4.12" % "test",
    "com.novocode" % "junit-interface" % "0.11" % "test->default",
    "org.scalacheck" %% "scalacheck" % "1.13.2" % "test",
    "com.icegreen" % "greenmail" % "1.5.1" % "test",
    "ch.qos.logback" % "logback-classic" % "1.1.7" % "test"
  )
)

lazy val root = (project in file(".")).disablePlugins(AssemblyPlugin)
  .settings(commonSettings: _*)
  .settings(
    name := "maila",
    version := "0.2.1",
    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % "1.3.0",
      "com.sun.mail" % "javax.mail" % "1.5.5",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.4.0" % "test"
    ),
    reColors := Seq("magenta"),
    publishTo := Some("My Bintray" at "https://api.bintray.com/maven/cuzfrog/maven/maila/;publish=1"),
    credentials += Credentials("Bintray API Realm", "api.bintray.com", "BINTRAY_USER", "BINTRAY_PASS")
  )



lazy val batchMailTool = (project in file("./bmt"))
  .settings(commonSettings: _*)
  .settings(
    name := "batch-mail-tool",
    version := "0.2.1",
    libraryDependencies ++= Seq(
    ),
    mainClass in assembly := Some("com.github.cuzfrog.tool.bmt.BatchMailTool"),
    mainClass in (Compile, packageBin) := (mainClass in assembly).value,
    assembly <<= assembly dependsOn generateBat,
    generateBat <<= generateBat dependsOn copyApp,
    generateBat := {
      val file = baseDirectory.value / "target/scala-2.11" / "bmt.bat"
      val contents = s"@echo off${System.lineSeparator}java -jar %CD%\\batch-mail-tool-assembly-${version.value}.jar %*"
      IO.write(file, contents)
    },
    copyApp := {

    }
  ).dependsOn(root)

lazy val generateBat = TaskKey[Unit]("generate-bat", "Generate a bat file for window shell.")
lazy val copyApp = TaskKey[Unit]("copy-app","Copy app files to target.")
addCommandAlias("bmt", "batchMailTool/run")