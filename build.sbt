organization := "com.github.cuzfrog"
name := "maila"
version := "0.1.2"
scalaVersion := "2.11.8"

resolvers ++= Seq(
  "Local Maven Repository" at """file:///""" + Path.userHome.absolutePath +"""\.m2\repository""",
  "bintray-cuzfrog-maven" at "http://dl.bintray.com/cuzfrog/maven",
  "Artima Maven Repository" at "http://repo.artima.com/releases"
)

lazy val root = (project in file(".")).disablePlugins(AssemblyPlugin)
libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % "1.0.5",
  "com.sun.mail" % "javax.mail" % "1.5.5"
)


lazy val batchMailTool = (project in file("./bmt"))
  .settings(
    organization := "com.github.cuzfrog",
    name := "batch-mail-tool",
    version := "0.1.0",
    scalaVersion := "2.11.8",
    libraryDependencies ++= Seq(
      "com.github.cuzfrog" %% "maila" % "0.1.2"
    ),
    mainClass in assembly := Some("com.github.cuzfrog.tool.BatchMailTool")
  )

reColors := Seq("magenta")
publishTo := Some("My Bintray" at "https://api.bintray.com/maven/cuzfrog/maven/maila/;publish=1")
credentials += Credentials("Bintray API Realm", "api.bintray.com", "BINTRAY_USER", "BINTRAY_PASS")