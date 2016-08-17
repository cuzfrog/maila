package com.github.cuzfrog.utils

import com.github.cuzfrog.maila.Configuration
import com.typesafe.config.ConfigFactory

/**
  * Created by cuz on 2016-08-16.
  */
object DateParseToolTest extends App {
  val context1 =
    """Context:from Foxmail (unknown[10.185.129.8]) Wed, 17 Aug 2016 08:41:20 +0800 (CST) DDEESAS"""
  val config = ConfigFactory.load().getConfig("maila")
  val pattern = config.getString("reader.pop3-received-date-parse.formatter.foxmail")
  println(DateParseTool.getExtractorFromDateFormat(pattern).regex)
  val localDate = DateParseTool.extractDate(context = context1, formats = Seq(pattern))
  println(localDate)

  val context2 = "from 127.0.0.1 (HELO 127.0.0.1); Tue Aug 16 11:58:43 CST 2016 IDl,,es"
  val pattern2 = config.getString("reader.pop3-received-date-parse.formatter.green-mails")
  val localDate2 = DateParseTool.extractDate(context = context2, formats = Seq(pattern2))
  println(localDate2)
}
