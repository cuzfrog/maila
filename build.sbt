resolvers ++= Seq(
  "Local Maven Repository" at """file:///""" + Path.userHome.absolutePath +"""\.m2\repository""",
  "bintray-cuzfrog-maven" at "http://dl.bintray.com/cuzfrog/maven",
  "Artima Maven Repository" at "http://repo.artima.com/releases"
)


lazy val commonSettings = Seq(
  organization := "com.github.cuzfrog",
  scalaVersion := "2.11.8",
  logBuffered in Test := false
)

lazy val root = (project in file(".")).disablePlugins(AssemblyPlugin)
  .settings(commonSettings: _*)
  .settings(
    name := "maila",
    version := "0.2.0",
    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % "1.3.0",
      "com.sun.mail" % "javax.mail" % "1.5.5",
      "com.lihaoyi" %% "utest" % "0.4.3" % "test"
    ),
    testFrameworks += new TestFramework("utest.runner.Framework"),
    reColors := Seq("magenta"),
    publishTo := Some("My Bintray" at "https://api.bintray.com/maven/cuzfrog/maven/maila/;publish=1"),
    credentials += Credentials("Bintray API Realm", "api.bintray.com", "BINTRAY_USER", "BINTRAY_PASS")
  )



lazy val batchMailTool = (project in file("./bmt"))
  .settings(commonSettings: _*)
  .settings(
    name := "batch-mail-tool",
    version := "0.2.0",
    libraryDependencies ++= Seq(
    ),
    mainClass in assembly := Some("com.github.cuzfrog.tool.bmt.BatchMailTool"),
    assembly <<= assembly dependsOn generateBat,
    generateBat := {
      val file = baseDirectory.value / "target/scala-2.11" / "bmt.bat"
      val contents = s"@echo off${System.lineSeparator}java -jar %CD%\\batch-mail-tool-assembly-${version.value}.jar %*"
      IO.write(file, contents)
    }
  ).dependsOn(root)

lazy val generateBat = TaskKey[Unit]("generate-bat", "Generate a bat file for window shell.")
