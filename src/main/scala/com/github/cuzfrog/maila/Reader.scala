package com.github.cuzfrog.maila

import javax.mail.MessagingException
import javax.mail.Session
import javax.mail.NoSuchProviderException
import java.util.Properties
import javax.mail.Folder
import javax.mail.Store
import java.util.Date
import java.text.SimpleDateFormat

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
    private final val RECEIVED_HEADER_DATE_FORMAT = "EEE, d MMM yyyy HH:mm:ss Z";
    private final val ReceivedDateRex="""(?s).*([\w]{3},\s[\d]{2}\s[\w]{3}\s[\d]{4}\s[.]{8}\s+[\d]{4}).*""".r
     new SimpleDateFormat
    override def mails(mailFilter: MailFilter): List[Mail] = {
      emailFolder.open(Folder.READ_ONLY);
      val range = (emailFolder.getMessageCount - mailFilter.maxSearchAmount) to emailFolder.getMessageCount
      val messages = emailFolder.getMessages(range.toArray).filter {
        m => 
          println(m.getSubject+m.getHeader("Received").mkString(System.lineSeparator()))
          val receivedDate= m.getReceivedDate match{
            case null=> 
              val header=m.getHeader("Received")
              if(header.size==0) throw new IllegalArgumentException("Cannot read email header. Mail:"+m.getSubject)
              val sdf = new SimpleDateFormat(RECEIVED_HEADER_DATE_FORMAT);
              header(0) match{
                case ReceivedDateRex(d) =>sdf.parse(d)
                case h => throw new IllegalArgumentException("Bad email header. Header:"+h)
              }
            case d=>d
          }
          mailFilter.receiveDateFilter(receivedDate) && mailFilter.subjectFilter(m.getSubject)
      }.toList.map(Mail(_))
      emailFolder.close(false)
      messages
    }
    override def shutdown = {
      store.close();
    }

  }
}