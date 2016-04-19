package com.github.cuzfrog.maila

trait Maila {
  def read(mailFilter: MailFilter): List[Mail]
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
    val user=config.user
    val server = Server(config)
    def read(mailFilter: MailFilter): List[Mail] = {
      val reader = server.reader(Account(config))

      val mails = reader.mails(mailFilter)
      reader.shutdown
      mails
    }
  }
}