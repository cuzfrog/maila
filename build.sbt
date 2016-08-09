
name := "maila"

lazy val commonSettings = Seq(
  organization := "com.github.cuzfrog",
  version := "0.1.3",
  scalaVersion := "2.11.8"
)


resolvers ++= Seq(
  "Local Maven Repository" at """file:///""" + Path.userHome.absolutePath +"""\.m2\repository""",
  "bintray-cuzfrog-maven" at "http://dl.bintray.com/cuzfrog/maven",
  "Artima Maven Repository" at "http://repo.artima.com/releases"
)

lazy val root = (project in file(".")).disablePlugins(AssemblyPlugin)
   .settings(commonSettings:_*)
libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % "1.0.5",
  "com.sun.mail" % "javax.mail" % "1.5.5"
)


lazy val batchMailTool = (project in file("./bmt"))
  .settings(commonSettings:_*)
  .settings(
    name := "batch-mail-tool",
    libraryDependencies ++= Seq(
    ),
    mainClass in assembly := Some("com.github.cuzfrog.tool.bmt.BatchMailTool")
  ).dependsOn(root)

reColors := Seq("magenta")
publishTo := Some("My Bintray" at "https://api.bintray.com/maven/cuzfrog/maven/maila/;publish=1")
credentials += Credentials("Bintray API Realm", "api.bintray.com", "BINTRAY_USER", "BINTRAY_PASS")