package com.github.cuzfrog.utils

import java.time.LocalDate
import java.time.format.{DateTimeFormatter, DateTimeParseException}
import java.util.Locale
import javax.mail.MessagingException

import scala.util.matching.Regex

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


  /**
    * Try to parse a date String expression using given formats recursively.
    *
    * @param context date String expression to parse.
    * @param transformFunc accepts context and format and yields a date expression to parse.
    *                      default is defined within this object, which extracts date String from the context
    *                      String with respect to different parsing format.
    * @param formats a sequence of formats, everyone of which would be used to parse the date expression.
    * @param locale  date expression locale.
    * @return LocalDate parsed.
    */
  def extractDate(context: String,
                  transformFunc: (String, String) => String = defaultExtractDateFromContext,
                  formats: Seq[String] = defaultFormats,
                  locale: Locale = Locale.ENGLISH): LocalDate = {
    if (formats.isEmpty) throw new DateTimeParseException("Cannot parse date with all formats.", context, 0)
    else try {
      val format = formats.head
      val expr = transformFunc(context, format)
      LocalDate.parse(expr, DateTimeFormatter.ofPattern(format, locale))
    } catch {
      case e: DateTimeParseException => extractDate(context, transformFunc, formats.tail)
    }
  }

  /**
    * Use context as date expression itself, ignore format.
    */
  val NO_EXTRACTION_FROM_CONTEXT = (context: String, format: String) => context

  /**
    * Provide a default algorithm to map a format to a regex, which is used to extract
    * date expression from its context string.
    * @param context in which date expression resides.
    * @param format date parser pattern.
    * @return extracted date expression.
    */
  def defaultExtractDateFromContext(context: String, format: String): String = {
    def getExtractorFromDateFormat(expr: String): Regex = {
      expr.replaceAll("""('[^']*')""",""" '.*' """).split("""[\s:-]""")
      println(context)
      ???
    }

    val DateExtractor = getExtractorFromDateFormat(format)
    context match {
      case DateExtractor(d) => d
      case h => throw new MessagingException("Bad email header. Header:" + h)
    }
  }


}
