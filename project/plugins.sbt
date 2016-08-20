resolvers ++= Seq(
  "Local Maven Repository" at """file:///""" + Path.userHome.absolutePath +"""\.m2\repository""",
  "bintray-cuzfrog-maven" at "http://dl.bintray.com/cuzfrog/maven",
  "Artima Maven Repository" at "http://repo.artima.com/releases",
  "spray repo" at "http://repo.spray.io",
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"
)
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.2")
addSbtPlugin("io.spray" % "sbt-revolver" % "0.8.0")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.3")
//addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.8.5")