package com.github.cuzfrog.maila.server

import java.util.Date
import javax.mail.Message.RecipientType
import javax.mail.{Address, MessagingException, Session, Transport}
import javax.mail.internet.{InternetAddress, MimeMessage}

import com.sun.mail.smtp.SMTPTransport
import com.typesafe.scalalogging.LazyLogging

private[server] trait Sender {
  def send(recipients: Seq[String], subject: String, text: String): Unit
}


private[server] object Sender extends LazyLogging {
  def apply(mimeMessage: MimeMessage): Sender = new JmSender(mimeMessage)

  private class JmSender(message: MimeMessage) extends Sender {

    def send(recipients: Seq[String], subject: String, text: String) = try {
      val addresses: Array[Address] = recipients.map(new InternetAddress(_)).toArray
      message.addRecipients(RecipientType.TO, addresses)
      message.setSubject(subject)
      message.setText(text)
      message.setSentDate(new Date())


//      t.sendMessage(msg, msg.getAllRecipients());
//      t.close();
      logger.info(s"Sent message[${message.getSubject}] successfully....");
    } catch {
      case e: MessagingException => e.printStackTrace()
    }
  }
}