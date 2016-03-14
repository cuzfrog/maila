package com.github.cuzfrog.maila

trait Maila {
  def read(mailFilter: MailFilter): List[Mail]
}

object Maila {
  def newInstance(configFilePath: String): Maila = {
    val config = Configuration.fromFile(configFilePath)
    new SimpleMaila(config)
  }

  private class SimpleMaila(config: Configuration) extends Maila {
    val server = Server(config)
    def read(mailFilter: MailFilter): List[Mail] = {
      val reader = server.reader(Account(config))

      val mails = reader.mails(mailFilter)
      reader.shutdown
      mails
    }
  }
}