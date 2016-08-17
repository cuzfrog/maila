package com.github.cuzfrog.utils

object RexDev extends App {

  val expr = "yyyy-MM-dd HH:mm:ss"
  val context1 = "from 127.0.0.1 (HELO 127.0.0.1); Tue Aug 16 11:58:43 CST 2016 IDl,,es"
  val context = "from 127.0.0.1 (HELO 127.0.0.1); 2016-06-30 11:58:43 CST IDl,,es"
  println(expr)
  println(DateParseTool.getExtractorFromDateFormat(expr).regex)
  println(DateParseTool.getExtractorFromDateFormat(expr).unapplySeq(context))


  println("Default format validity test: ")
  DateParseTool.defaultFormats.foreach { f => println(DateParseTool.getExtractorFromDateFormat(f).regex) }


}