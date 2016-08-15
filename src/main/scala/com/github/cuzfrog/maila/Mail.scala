package com.github.cuzfrog.maila

import java.time.format.{DateTimeFormatter, DateTimeParseException}
import java.time.{Instant, LocalDate}
import javax.mail.{Message, MessagingException, Multipart, Part}

import scala.util.matching.Regex

//todo:redesign Mail to wrap Message properly.

trait Mail {
  def receiveDate: LocalDate

  def subject: String

  def contentMime: AnyRef

  def contentText: String

  def contentType: String

  def recipients: Seq[String]
}

object Mail {
  /**
    * Create a wrapper. Note that fields are not really fetched from the server.
    */
  def wrap(message: Message): Mail = new JmMail(message)

  /**
    * Fetch data from the server, and create an entity wrapper.
    */
  def fetch(mail: Mail): Mail = new EntityMail(mail)

  def apply(recipients: Seq[String], subject: String, text: String): Mail =
    new MailForSending(recipients: Seq[String], subject: String, text: String)

  private lazy val config = Configuration.config

  import collection.JavaConversions._

  private lazy val dateFormats = {
    val pairs = config.getConfig("reader.pop3-received-date-parse.formatter").entrySet().toList
    pairs.sortBy(_.getKey).map(_.getValue.unwrapped().toString)
  }

  private def getExtractorFromDateFormat(expression: String): Regex = {
    ???
  }

  //private final val RECEIVED_HEADER_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss Z"
  private final val ReceivedDateRex =
    """(?s).*([\w]{3},\s[\d]{2}\s[\w]{3}\s[\d]{4}\s[\d:]{8}\s\+[\d]{4}).*""".r

  private class JmMail(message: Message) extends Mail {

    lazy val receiveDate = message.getReceivedDate match {
      case null =>
        val header = message.getHeader("Received")
        if (header.isEmpty) throw new MessagingException("Cannot read email header. Mail:" + subject)
        //DateTimeFormatter.ofPattern(RECEIVED_HEADER_DATE_FORMAT)
        header.head match {
          case ReceivedDateRex(d) => LocalDate.parse(d, DateTimeFormatter.RFC_1123_DATE_TIME)
          case h => throw new MessagingException("Bad email header. Header:" + h)
        }
      case d => LocalDate.from(Instant.ofEpochMilli(d.getTime))
    }

    lazy val subject = message.getSubject
    lazy val contentMime = message.getContent
    lazy val contentType = message.getContentType
    lazy val contentText = parseMime(message)
    lazy val recipients = message.getAllRecipients.toSeq.map(_.toString)

    private def parseMime(part: Part): String = part.getContentType.toLowerCase match {
      case s if s.contains("text") => part.getContent.toString
      case s if s.contains("multipart") =>
        val multipart = part.getContent.asInstanceOf[Multipart]
        val range = 1 to multipart.getCount
        range.map(n => parseMime(multipart.getBodyPart(n - 1))).mkString(System.lineSeparator)
    }

    //try every formatter to parse the date expression until success or fail completely.
    private def parseDate(header: String, formats: Seq[String]): LocalDate = {
      if (formats.isEmpty) {
        val formatter = DateTimeFormatter.RFC_1123_DATE_TIME
        val expr = extractDateFromHeader(header, formatter.toString)
        LocalDate.parse(header,formatter)
      }
      else try {
        val format = formats.head
        val expr = extractDateFromHeader(header, format)
        LocalDate.parse(expr, DateTimeFormatter.ofPattern(format))
      } catch {
        case e: DateTimeParseException => parseDate(header, formats.tail)
      }
    }

    private def extractDateFromHeader(header: String, format: String): String = {
      val DateExtractor = getExtractorFromDateFormat(format)
      header match {
        case DateExtractor(d) => d
        case h => throw new MessagingException("Bad email header. Header:" + h)
      }
    }
  }

  private class EntityMail(mail: Mail) extends Mail {
    override val receiveDate: LocalDate = mail.receiveDate
    override val contentText: String = mail.contentText
    override val contentType: String = mail.contentType
    override val subject: String = mail.subject
    override val recipients: Seq[String] = mail.recipients
    override val contentMime: AnyRef = mail.contentMime
  }


  private class MailForSending(val recipients: Seq[String], val subject: String, text: String) extends Mail {
    def receiveDate = throw new UnsupportedOperationException

    def contentText = text

    def contentType = "plain/TEXT"

    def contentMime = throw new UnsupportedOperationException
  }

}