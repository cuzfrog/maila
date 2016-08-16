package com.github.cuzfrog.utils

import com.github.cuzfrog.maila.Configuration
import com.typesafe.config.ConfigFactory

/**
  * Created by cuz on 2016-08-16.
  */
object DateParseToolTest extends App {
  val context1 = "from 127.0.0.1 (HELO 127.0.0.1); Tue Aug 16 11:58:43 CST 2016 IDl,,es"
  val config = ConfigFactory.load().getConfig("maila")
  val pattern = config.getString("reader.pop3-received-date-parse.formatter.green-mails")
  val localDate = DateParseTool.extractDate(context = context1, formats = Seq(pattern))
  println(localDate)
}
