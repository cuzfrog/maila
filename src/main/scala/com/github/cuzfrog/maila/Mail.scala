package com.github.cuzfrog.maila

import java.time.{Instant, LocalDate}
import java.util.Locale
import javax.mail.{Message, MessagingException, Multipart, Part}

import com.github.cuzfrog.utils.{DateParseTool, SimpleLogger}


trait Mail {
  def receiveDate: LocalDate

  def subject: String

  def contentMime: AnyRef

  def contentText: String

  def contentType: String

  def recipients: Seq[String]

  def sender: String
}

object Mail extends SimpleLogger {
  def apply(recipients: Seq[String], subject: String, text: String): Mail = {
    require(recipients.nonEmpty, "There is no recipients.")
    val _subject = if (subject == null || subject.isEmpty) "NoSubject" else subject
    val _text = if (text == null) "" else text
    new MailForSending(recipients: Seq[String], _subject: String, _text: String)
  }

  /**
    * Create a wrapper. Note that fields are not really fetched from the server.
    */
  private[maila] def wrap(message: Message): Mail = new JmMail(message)

  /**
    * Fetch data from the server, and create an entity wrapper.
    */
  private[maila] def fetch(mail: Mail): Mail = new EntityMail(mail)

  private lazy val config = Configuration.config

  import collection.JavaConversions._

  private lazy val dateFormats: List[String] = {
    val pairs = config.getConfig("reader.pop3-received-date-parse.formatter").entrySet().toList
    val configFormats = pairs.sortBy(_.getKey).map(_.getValue.unwrapped().toString)
    configFormats ++ DateParseTool.defaultFormats
  }
  private lazy val locale = Locale.forLanguageTag(config.getString("reader.pop3-received-date-parse.locale"))

  private class JmMail(message: Message) extends Mail {

    lazy val receiveDate = message.getReceivedDate match {
      case null =>
        val header = message.getHeader("Received")
        if (header.isEmpty) throw new MessagingException("Cannot read email header. Mail:" + subject)
        //try every formatter to parse the date expression until a success or a complete failure.
        DateParseTool.extractDate(context = header.head, formats = dateFormats, locale = locale)
      case d => LocalDate.from(Instant.ofEpochMilli(d.getTime))
    }

    lazy val subject = message.getSubject
    lazy val contentMime = message.getContent
    lazy val contentType = message.getContentType
    lazy val contentText = parseMime(message)
    lazy val recipients = message.getAllRecipients.toSeq.map(_.toString)
    lazy val sender: String = message.getFrom.head.toString

    private def parseMime(part: Part): String = part.getContentType.toLowerCase match {
      case s if s.contains("text") => part.getContent.toString
      case s if s.contains("multipart") =>
        val multipart = part.getContent.asInstanceOf[Multipart]
        val range = 1 to multipart.getCount
        range.map(n => parseMime(multipart.getBodyPart(n - 1))).mkString(System.lineSeparator)
      case o => s"[Content:$o]"

    }
  }

  private class EntityMail(mail: Mail) extends Mail {
    override val receiveDate: LocalDate = mail.receiveDate
    override val contentText: String = mail.contentText
    override val contentType: String = mail.contentType
    override val subject: String = mail.subject
    override val recipients: Seq[String] = mail.recipients
    override val contentMime: AnyRef = mail.contentMime
    override val sender: String = mail.sender
  }

  private class MailForSending(val recipients: Seq[String], val subject: String, text: String) extends Mail {
    def receiveDate = throw new UnsupportedOperationException

    def contentText = text

    def contentType = "plain/TEXT"

    def contentMime = throw new UnsupportedOperationException

    lazy val sender: String = config.getString("authentication.user")
  }

}