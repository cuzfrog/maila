package com.github.cuzfrog.utils

object RexDev extends App {



  def pattern(expr: String) = {
    val reg = expr.replaceAll("""('[^']+'|[:\-\s])""","""|$1|""")
      .split("""\|""").map(formatToRegex).mkString
    """(?s).*(?<=[\s])(""" + reg + """).*"""
  }.r

  private def formatToRegex(piece: String): String = {
    val EscapeR = """('[^']')""".r
    val AlphanumericR = """(M+)""".r
    val NumericR = """(y+|d+|H+|m+|s+)""".r
    val AlphabeticR = """(z+|Z+|S+|E+|a+|A+)""".r
    piece match {
      case EscapeR(s) => s
      case " " => "\\s"
      case AlphanumericR(s) => s"""[\\d\\w]+"""
      case NumericR(s) => s"""[\\d]{${s.length}}"""
      case AlphabeticR(s) => s"""[\\w]+"""
      case o if o.sliding(2).exists(p => p.head != p.last) =>
        val pp = o.scanLeft("")((l, r) => if (!(l contains r)) l + " " + r else l + r).last
        pp.split("""\s""").map(formatToRegex).mkString
      case o => o
    }
  }

  val expr = "yyyy-MM-dd HH:mm:ss"
  val context1 = "from 127.0.0.1 (HELO 127.0.0.1); Tue Aug 16 11:58:43 CST 2016 IDl,,es"
  val context = "from 127.0.0.1 (HELO 127.0.0.1); 2016-06-30 11:58:43 CST IDl,,es"
  println(expr)
  println(pattern(expr).regex)
  println(pattern(expr).unapplySeq(context))

  println("Default format validity test: ")
  DateParseTool.defaultFormats.foreach { f => println(pattern(f).regex) }


}