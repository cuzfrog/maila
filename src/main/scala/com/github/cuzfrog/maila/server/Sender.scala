package com.github.cuzfrog.maila.server

import java.util.Date
import javax.mail.Message.RecipientType
import javax.mail.internet.{InternetAddress, MimeMessage}
import javax.mail.{Address, MessagingException, Transport}

import com.typesafe.scalalogging.LazyLogging

private[server] trait Sender {
  def send(recipients: Seq[String], subject: String, text: String): Unit
}


private[server] object Sender extends LazyLogging {
  def apply(mimeMessage: MimeMessage, transport: Transport): Sender = new JmSender(mimeMessage,transport)

  private class JmSender(message: MimeMessage, transport: Transport) extends Sender {

    def send(recipients: Seq[String], subject: String, text: String) = try {
      val addresses: Array[Address] = recipients.map(new InternetAddress(_)).toArray
      message.addRecipients(RecipientType.TO, addresses)
      message.setSubject(subject)
      message.setText(text)
      message.setSentDate(new Date())


      transport.sendMessage(message, message.getAllRecipients)
      transport.close()
      logger.info(s"Sent message[${message.getSubject}] successfully....");
    } catch {
      case e: MessagingException => e.printStackTrace()
    }
  }
}