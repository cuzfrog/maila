package com.github.cuzfrog.maila.server

import java.util.Date
import javax.mail.Message.RecipientType
import javax.mail.internet.{InternetAddress, MimeMessage}
import javax.mail.{Address, MessagingException, Session, Transport}

private[server] trait Sender {
  def send(recipients: Seq[String], subject: String, text: String): (Boolean, String)

  def close(): Unit
}

private[server] object Sender {
  def apply(session: Session, transport: Transport, user: String): Sender = new JmSender(session, transport, user)

  private class JmSender(session: Session, transport: Transport, user: String) extends Sender {

    def send(recipients: Seq[String], subject: String, text: String): (Boolean, String) = {
      val addresses: Array[Address] = recipients.map(new InternetAddress(_)).toArray
      lazy val message = new MimeMessage(session)
      message.setFrom(new InternetAddress(user))

      message.addRecipients(RecipientType.TO, addresses)
      message.setSubject(subject)
      message.setText(text)
      message.setSentDate(new Date())
      val msg = s"Sent message[${message.getSubject}] to[${message.getAllRecipients.mkString(",")}]"
      try {
        transport.sendMessage(message, message.getAllRecipients)
        (true, s"$msg successfully.")
      } catch {
        case e: MessagingException =>
          (false, s"$msg Failed with msg:${e.getMessage}")
      }
    }

    def close(): Unit = transport.close()
  }

}