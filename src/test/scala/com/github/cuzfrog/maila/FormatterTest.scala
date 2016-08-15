package com.github.cuzfrog.maila

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
  * Created by cuz on 2016-08-15.
  */
object FormatterTest extends App {
  val dateS = "Mon Aug 15 16:24:14 CST 2016"
  val formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
  println(DateTimeFormatter.RFC_1123_DATE_TIME.toFormat.)
}
