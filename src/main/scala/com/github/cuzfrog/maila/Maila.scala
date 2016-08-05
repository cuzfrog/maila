package com.github.cuzfrog.maila

import com.github.cuzfrog.maila.server.Server

trait Maila {
  def read(mailFilter: MailFilter): List[Mail]
  def send(recipients: Seq[String], subject: String, text: String): Unit
  def user: String
}

object Maila {
  def newInstance(configFilePath: String,
                  willObfuscate: Boolean = false,
                  keys: List[Array[Byte]] = null): Maila = {
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

    def send(recipients: Seq[String], subject: String, text: String) = {
      val sender = server.sender(Account(config))
      sender.send(recipients: Seq[String], subject: String, text: String): Unit
    }
  }
}