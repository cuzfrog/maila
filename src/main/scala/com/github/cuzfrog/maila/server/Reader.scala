package com.github.cuzfrog.maila.server

import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDate}
import java.util.Locale
import javax.mail.{Folder, MessagingException, Store}

import com.github.cuzfrog.maila.{Mail, MailFilter}

private[maila] trait Reader {
  def mails(filter: MailFilter): List[Mail]
  def shutdown(): Unit
}

private[maila] object Reader {
  private[maila] def apply(store: Store): Reader = {
    new JmReader(store)
  }

  private class JmReader(store: Store) extends Reader {
    val emailFolder = store.getFolder("INBOX")
    private final val RECEIVED_HEADER_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss Z"
    private final val ReceivedDateRex = """(?s).*([\w]{3},\s[\d]{2}\s[\w]{3}\s[\d]{4}\s[\d:]{8}\s\+[\d]{4}).*""".r

    override def mails(mailFilter: MailFilter): List[Mail] = {
      emailFolder.open(Folder.READ_ONLY)
      val range = (emailFolder.getMessageCount - mailFilter.maxSearchAmount) to emailFolder.getMessageCount
      val messages = range.flatMap { i =>
        try {
          Some(emailFolder.getMessage(i))
        } catch {
          case e: IndexOutOfBoundsException => None
        }
      }.filter { m =>
        //println(m.getSubject+m.getHeader("Received").mkString(System.lineSeparator()))
        //todo: handle date more reasonably
        lazy val receivedDate = m.getReceivedDate match {
          case null =>
            val header = m.getHeader("Received")
            if (header.isEmpty) throw new MessagingException("Cannot read email header. Mail:" + m.getSubject)
            val sdf = new SimpleDateFormat(RECEIVED_HEADER_DATE_FORMAT, Locale.ENGLISH)
            header(0) match {
              case ReceivedDateRex(d) => sdf.parse(d)
              case h => throw new MessagingException("Bad email header. Header:" + h)
            }
          case d => d
        }
        mailFilter.subjectFilter(m.getSubject) && mailFilter.receiveDateFilter(LocalDate.from(Instant.ofEpochMilli(receivedDate.getTime)))
      }.toList.map(Mail(_))
      emailFolder.close(false)
      messages
    }
    override def shutdown() = {
      store.close()
    }

  }
}