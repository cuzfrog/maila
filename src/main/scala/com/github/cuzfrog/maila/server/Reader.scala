package com.github.cuzfrog.maila.server

import javax.mail.Flags.Flag
import javax.mail.{Folder, Message, Store}

import com.github.cuzfrog.maila.{Configuration, Mail, MailFilter}

private[maila] trait Reader {
  def mails(filter: MailFilter): Seq[Mail]

  def shutdown(): Unit
}

private[maila] object Reader {
  private[maila] def apply(store: Store, config: Configuration): Reader = {
    new JmReader(store, config)
  }


  private class JmReader(store: Store, config: Configuration) extends Reader {

    private val folder = store.getFolder(config.folderName)
    folder.open(Folder.READ_WRITE)

    override def mails(mailFilter: MailFilter): Seq[Mail] = synchronized {
      if (folder.getMessageCount == 0) return Nil
      val range = Math.max(folder.getMessageCount - mailFilter.maxSearchAmount, 1) to folder.getMessageCount
      val mails: Seq[Message] = range.flatMap { i =>
        try {
          val m = folder.getMessage(i)
          if (config.isDeleteAfterFetch) m.setFlag(Flag.DELETED, true)
          Some(m)
        } catch {
          case e: IndexOutOfBoundsException => None
        }
      }
      mails.filter(mailFilter.javaMessageFilter)
        .map(Mail.wrap(_, config)).filter(mailFilter.filter).map(Mail.fetch)
    }

    override def shutdown() = {
      folder.close(true)
      store.close()
    }

  }

}