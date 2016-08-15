package com.github.cuzfrog.utils

import java.time.LocalDate
import java.time.format.{DateTimeFormatter, DateTimeParseException}
import java.util.Locale

/**
  * Created by cuz on 2016-08-15.
  */
private[cuzfrog] object DateParseTool {
  val defaultFormats = List(
    "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm:ssZ",
    "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
    "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd HH:mm:ss",
    "MM/dd/yyyy HH:mm:ss", "MM/dd/yyyy'T'HH:mm:ss.SSS'Z'",
    "MM/dd/yyyy'T'HH:mm:ss.SSSZ", "MM/dd/yyyy'T'HH:mm:ss.SSS",
    "MM/dd/yyyy'T'HH:mm:ssZ", "MM/dd/yyyy'T'HH:mm:ss",
    "yyyy:MM:dd HH:mm:ss", "yyyyMMdd"
  )

  def parseDate(dateExpr: String, formats: Seq[String], locale: Locale = Locale.ENGLISH): LocalDate = {
    if (formats.isEmpty) throw new DateTimeParseException("Cannot parse date with all formats.", dateExpr, 0)
    else try {
      val format = formats.head
      LocalDate.parse(dateExpr, DateTimeFormatter.ofPattern(format, locale))
    } catch {
      case e: DateTimeParseException => parseDate(dateExpr, formats.tail)
    }
  }
}
