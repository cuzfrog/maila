package com.github.cuzfrog.tool

import java.io.File

/**
  * Created by cuz on 2016-08-09.
  */
object CsvParseTest extends App{
  private val bufferedSource = io.Source
    .fromFile(new File("""D:\workspace\scala\Maila\bmt\target\scala-2.11\account_with_new_pw.csv"""))("GBK")
  private val allRaw = try {
    bufferedSource.getLines.toList
  } finally {
    bufferedSource.close
  }
  private val RegexString = """"(.*)"""".r
  private val all = allRaw.map(_.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1).map {
    case RegexString(s) =>
      println(s)
      s
    case os => os.trim
  })

  private val heads = all.head
  println(s"headers: ${heads.mkString("|")}")
}
