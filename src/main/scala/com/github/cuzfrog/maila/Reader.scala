package com.github.cuzfrog.maila

import javax.mail.MessagingException
import javax.mail.Session
import javax.mail.NoSuchProviderException
import java.util.Properties
import javax.mail.Folder
import javax.mail.Store
import java.util.Date

private[maila] trait Reader {
  def mails(filter: MailFilter): List[Mail]
  def shutdown: Unit
}

private[maila] object Reader {
  private[maila] def apply(store: Store, host: String, user: String, password: String): Reader = {
    store.connect(host, user, password)
    new JmReader(store)
  }

  

  private class JmReader(store: Store) extends Reader {
    val emailFolder = store.getFolder("INBOX");

    override def mails(mailFilter: MailFilter): List[Mail] = {
      emailFolder.open(Folder.READ_ONLY);
      val range = (emailFolder.getMessageCount - mailFilter.maxSearchAmount) to emailFolder.getMessageCount
      val messages = emailFolder.getMessages(range.toArray).filter {
        m => 
          mailFilter.receiveDateFilter(m.getReceivedDate) && mailFilter.subjectFilter(m.getSubject)
      }.toList.map(Mail(_))
      emailFolder.close(false)
      messages
    }
    override def shutdown = {
      store.close();
    }

  }
}