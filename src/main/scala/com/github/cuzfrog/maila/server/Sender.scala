package com.github.cuzfrog.maila.server

import java.util.Date
import javax.mail.Message.RecipientType
import javax.mail.internet.{InternetAddress, MimeMessage}
import javax.mail.{Address, MessagingException, Transport}

private[server] trait Sender {
  def send(recipients: Seq[String], subject: String, text: String): Unit
  def close(): Unit
}


private[server] object Sender {
  def apply(mimeMessage: MimeMessage, transport: Transport): Sender = new JmSender(mimeMessage, transport)

  private class JmSender(message: MimeMessage, transport: Transport) extends Sender {

    def send(recipients: Seq[String], subject: String, text: String) = try {
      val addresses: Array[Address] = recipients.map(new InternetAddress(_)).toArray
      message.addRecipients(RecipientType.TO, addresses)
      message.setSubject(subject)
      message.setText(text)
      message.setSentDate(new Date())

      transport.sendMessage(message, message.getAllRecipients)

      println(s"Maila:Sent message[${message.getSubject}] successfully....");
    } catch {
      case e: MessagingException => e.printStackTrace()
    }

    def close(): Unit = transport.close()
  }
}