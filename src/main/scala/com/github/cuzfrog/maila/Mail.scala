package com.github.cuzfrog.maila

import java.util.Date

import javax.mail.Message
import javax.mail.Multipart
import javax.mail.Part

trait Mail {
  def receiveDate: Date
  def subject: String
  def contentMime: Object
  def contentText: String
  def contentType: String
}

object Mail {
  def apply(message: Message): Mail = {
    new JmMail(message)
  }

  private class JmMail(message: Message) extends Mail {
    val receiveDate = message.getReceivedDate
    val subject = message.getSubject
    val contentMime = message.getContent
    val contentType = message.getContentType
    val contentText = parseMime(message)

    def parseMime(part: Part): String = part.getContentType.toLowerCase match {
      case s if (s.contains("text")) => part.getContent.toString
      case s if (s.contains("multipart")) =>
        val multipart = part.getContent.asInstanceOf[Multipart]
        val range = 1 to multipart.getCount
        range.map(n => parseMime(multipart.getBodyPart(n-1))).mkString(System.lineSeparator)
    }
  }
}