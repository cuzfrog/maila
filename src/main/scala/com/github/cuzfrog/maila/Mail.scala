package com.github.cuzfrog.maila

import java.time.{Instant, LocalDate}
import javax.mail.Message
import javax.mail.Multipart
import javax.mail.Part

trait Mail {
  def receiveDate: LocalDate
  def subject: String
  def contentMime: Object
  def contentText: String
  def contentType: String
  def recipients: Seq[String]
}

object Mail {
  def apply(message: Message): Mail = {
    new JmMail(message)
  }
  def apply(recipients: Seq[String], subject: String, text: String): Mail =
    new MailForSending(recipients: Seq[String], subject: String, text: String)

  private class JmMail(message: Message) extends Mail {
    lazy val receiveDate = LocalDate.from(Instant.ofEpochMilli(message.getReceivedDate.getTime))
    lazy val subject = message.getSubject
    lazy val contentMime = message.getContent
    lazy val contentType = message.getContentType
    lazy val contentText = parseMime(message)
    lazy val recipients = message.getAllRecipients.toSeq.map(_.toString)

    def parseMime(part: Part): String = part.getContentType.toLowerCase match {
      case s if s.contains("text") => part.getContent.toString
      case s if s.contains("multipart") =>
        val multipart = part.getContent.asInstanceOf[Multipart]
        val range = 1 to multipart.getCount
        range.map(n => parseMime(multipart.getBodyPart(n - 1))).mkString(System.lineSeparator)
    }
  }

  private class MailForSending(val recipients: Seq[String], val subject: String, text: String) extends Mail {
    def receiveDate = throw new UnsupportedOperationException
    def contentText = text
    def contentType = "plain/TEXT"
    def contentMime = throw new UnsupportedOperationException
  }
}