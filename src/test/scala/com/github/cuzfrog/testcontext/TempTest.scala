package com.github.cuzfrog.testcontext

import java.time.format.DateTimeFormatter

import com.typesafe.config.ConfigFactory

/**
  * Created by cuz on 2016-08-16.
  */
object TempTest extends App {
  val pattern = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z z")
}
