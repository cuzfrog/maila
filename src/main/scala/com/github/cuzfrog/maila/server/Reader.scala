package com.github.cuzfrog.maila.server

import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDate}
import java.util.Locale
import javax.mail.Flags.Flag
import javax.mail.{Folder, MessagingException, Store}

import com.github.cuzfrog.maila.{Configuration, Mail, MailFilter}

private[maila] trait Reader {
  def mails(filter: MailFilter): Seq[Mail]

  def shutdown(): Unit
}

private[maila] object Reader {
  private[maila] def apply(store: Store): Reader = {
    new JmReader(store)
  }

  private lazy val config = Configuration.config.getConfig("reader")
  private lazy val folderName = config.getString("folder")
  private lazy val deleteAfterFetch = config.getBoolean("delete-after-fetch")

  private class JmReader(store: Store) extends Reader {
    val folder = store.getFolder(folderName)
    folder.open(Folder.READ_WRITE)

    override def mails(mailFilter: MailFilter): Seq[Mail] = synchronized {
      if (folder.getMessageCount == 0) return Nil
      val range = Math.max(folder.getMessageCount - mailFilter.maxSearchAmount, 1) to folder.getMessageCount
      val mails = range.flatMap { i =>
        try {
          val m = folder.getMessage(i)
          if (deleteAfterFetch) m.setFlag(Flag.DELETED, true)
          Some(m)
        } catch {
          case e: IndexOutOfBoundsException => None
        }
      }.filter(mailFilter.javaMessageFilter).map(Mail.wrap).filter(mailFilter.filter).map(Mail.fetch)
      mails
    }

    override def shutdown() = {
      folder.close(true)
      store.close()
    }

  }

}