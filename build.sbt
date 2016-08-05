organization := "com.github.cuzfrog"
name := "maila"
version := "0.0.4"
scalaVersion := "2.11.8"


lazy val root = (project in file("."))
resolvers ++= Seq(
  "Local Maven Repository" at """file:///"""+Path.userHome.absolutePath+"""\.m2\repository""",
  "bintray-cuzfrog-maven" at "http://dl.bintray.com/cuzfrog/maven",
  "Artima Maven Repository" at "http://repo.artima.com/releases"
)

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % "1.0.5",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.4.0",
  "ch.qos.logback" %  "logback-classic" % "1.1.7",
  "com.sun.mail" % "javax.mail" % "1.5.5",
  "junit" % "junit" % "4.11"
)

reColors := Seq("magenta")

publishTo := Some("My Bintray" at "https://api.bintray.com/maven/cuzfrog/maven/maila/;publish=1")
credentials += Credentials("Bintray API Realm", "api.bintray.com", "BINTRAY_USER", "BINTRAY_PASS")