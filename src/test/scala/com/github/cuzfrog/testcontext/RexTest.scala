package com.github.cuzfrog.testcontext

import com.github.cuzfrog.utils.DateParseTool

object RexTest extends App {
  val expr = DateParseTool.defaultFormats.head

  val result= {
    expr.replaceAll("""('[^']*')""",""" $1 """)
  }
  println(expr)
  println(result)
}