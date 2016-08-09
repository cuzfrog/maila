package com.github.cuzfrog.maila.server

import java.util.Date
import javax.mail.Message.RecipientType
import javax.mail.internet.{InternetAddress, MimeMessage}
import javax.mail.{Address, MessagingException, Session, Transport}

private[server] trait Sender {
  def send(recipients: Seq[String], subject: String, text: String): Unit

  def close(): Unit
}


private[server] object Sender {
  def apply(session: Session, transport: Transport, user: String): Sender = new JmSender(session, transport, user)

  private class JmSender(session: Session, transport: Transport, user: String) extends Sender {

    def send(recipients: Seq[String], subject: String, text: String) = try {
      val addresses: Array[Address] = recipients.map(new InternetAddress(_)).toArray
      lazy val message = new MimeMessage(session)
      message.setFrom(new InternetAddress(user))

      message.addRecipients(RecipientType.TO, addresses)
      message.setSubject(subject)
      message.setText(text)
      message.setSentDate(new Date())

      transport.sendMessage(message, message.getAllRecipients)

      println(s"Maila:Sent message[${message.getSubject}] to[${message.getAllRecipients.mkString(System.lineSeparator)}] successfully....");
    } catch {
      case e: MessagingException => e.printStackTrace()
    }

    def close(): Unit = transport.close()
  }

}