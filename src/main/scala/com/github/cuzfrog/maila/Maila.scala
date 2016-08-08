package com.github.cuzfrog.maila

import com.github.cuzfrog.maila.server.Server

trait Maila {
  /**
    * Read mails using POP3.
    *
    * @param mailFilter filter mails to be read.
    * @return mails conform the filter.
    */
  def read(mailFilter: MailFilter): List[Mail]

  def send(mails: Seq[Mail]): Unit

  /**
    * @return current user with this maila instance.
    */
  def user: String
}

object Maila {
  def newInstance(configFilePath: String,
                  willObfuscate: Boolean = false,
                  keys: List[Array[Byte]] = Nil): Maila = {
    val config = Configuration.fromFile(configFilePath, willObfuscate, keys)
    new SimpleMaila(config)
  }

  private class SimpleMaila(config: Configuration) extends Maila {
    val user = config.user
    val server = Server(config)

    def read(mailFilter: MailFilter): List[Mail] = {
      val reader = server.reader(Account(config))
      val mails = reader.mails(mailFilter)
      reader.shutdown
      mails
    }

    def send(mails: Seq[Mail]) = {
      val sender = server.sender(Account(config))
      mails.foreach { m =>
        sender.send(m.recipients: Seq[String], m.subject: String, m.contentText: String): Unit
      }
      sender.close()
    }
  }
}