package com.github.cuzfrog

import java.time.format.DateTimeFormatter

/**
  * Created by cuz on 2016-08-16.
  */
object TempTest extends App {
  def st: String = {
    println("st invoked")
    null
  }
  st match {
    case s: String => println(s)
    case null =>
  }
}
