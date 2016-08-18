package com.github.cuzfrog

import java.time.format.DateTimeFormatter

/**
  * Created by cuz on 2016-08-16.
  */
object TempTest extends App {
  val r="""user18@localhost.com,subject18,"This is only one line with this double quote: \" and special sign: \\n.""""
    .split(""",(?=(([^"]|(\\"))*"([^"]|(\\"))*")*([^"]|(\\"))*$)""", -1)

  println(r.mkString(System.lineSeparator()))

  println("""sdfga\"sdfas""".matches("""([^\d]|(\\"))*"""))
}
