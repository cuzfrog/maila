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
    * @param context       date String expression to parse.
    * @param transformFunc accepts context and format and yields a date expression to parse.
    *                      default is defined within this object, which extracts date String from the context
    *                      String with respect to different parsing format.
    * @param formats       a sequence of formats, everyone of which would be used to parse the date expression.
    * @param locale        date expression locale.
    * @return LocalDate parsed.
    */
  def extractDate(context: String,
                  transformFunc: (String, String) => Option[String] = defaultExtractDateFromContext,
                  formats: Seq[String] = defaultFormats,
                  locale: Locale = Locale.ENGLISH): LocalDate = {
    if (formats.isEmpty) throw new DateTimeParseException(s"Cannot parse date with all formats. Context:$context", context, 0)
    else try {
      val format = formats.head
      val expr = transformFunc(context, format).get
      LocalDate.parse(expr, DateTimeFormatter.ofPattern(format, locale))
    } catch {
      case e: Exception =>
        println(s"Debug:${e.getMessage}")
        extractDate(context, transformFunc, formats.tail)
    }
  }

  /**
    * Use context as date expression itself, ignore format.
    */
  val NO_EXTRACTION_FROM_CONTEXT = (context: String, format: String) => context

  /**
    * Provide a default algorithm to map a format to a regex, which is used to extract
    * date expression from its context string.
    *
    * @param context in which date expression resides.
    * @param format  date parser pattern.
    * @return extracted date expression.
    */
  def defaultExtractDateFromContext(context: String, format: String): Option[String] = {
    def getExtractorFromDateFormat(format: String): Regex = {
      val reg = format.replaceAll("""('[^']+'|[:\-\s])""","""|$1|""")
        .split("""\|""").map(formatToRegex).mkString
      """(?s).*(?<=[\s])(""" + reg + """).*"""
    }.r

    val DateExtractor = getExtractorFromDateFormat(format)
    context match {
      case DateExtractor(d) => Some(d)
      case _ => None
    }
  }

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
}
