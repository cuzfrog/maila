package com.github.cuzfrog.maila

import com.github.cuzfrog.maila.server.Server
import com.typesafe.config.Config

trait Maila {
  /**
    * Read mails using POP3.
    *
    * @param mailFilter filter mails to be read.
    * @return mails conform the filter.
    */
  def read(mailFilter: MailFilter): List[Mail]

  /**
    * Send a sequence of mails.
    *
    * @param mails a sequence of mails to be sent.
    */
  def send(mails: Seq[Mail]): Unit

  /**
    * Get current config.
    *
    * @return current config
    */
  def getConfig(path: String): Config
}

object Maila {


  def newInstance(configFilePath: String): Maila = {
    val config = Configuration.fromFile(configFilePath)
    new SimpleMaila(config)
  }

  /**
    * Create a new instance loaded with configuration and a key for encrypted password.
    *
    * @param configFilePath self-explanatory.
    * @param key            used to decrypt password in config file.<br>
    *                       If not specified and "allow-none-encryption-password" in config is set to true,
    *                       application tries to use password as plain text.
    * @return a new instance ready to access mail.
    */
  def newInstance(configFilePath: String, key: Array[Byte] = Array.empty[Byte]): Maila = {
    require(key != null, "if there is no key, leave it to default value, which is an empty array.")
    val config = Configuration.fromFileWithKey(configFilePath, key)
    new SimpleMaila(config)
  }

  /**
    * Create a new instance loaded with configuration and a lazy password.
    *
    * @param configFilePath self-explanatory.
    * @param askPassword    a call-by-name lazy password, which can reference to a custom providing logic.
    * @return a new instance ready to access mail.
    */
  def newInstance(configFilePath: String, askPassword: => String): Maila = {
    val config = Configuration.fromFileWithPassword(configFilePath, askPassword)
    new SimpleMaila(config)
  }

  private class SimpleMaila(config: Configuration) extends Maila {
    override def getConfig(path: String): Config = config.config.getConfig(path)

    val server = Server(config)

    def read(mailFilter: MailFilter): List[Mail] = {
      val reader = server.reader
      val mails = reader.mails(mailFilter)
      reader.shutdown()
      mails
    }

    def send(mails: Seq[Mail]) = {
      val sender = server.sender
      mails.foreach { m =>
        sender.send(m.recipients: Seq[String], m.subject: String, m.contentText: String): Unit
      }
      sender.close()
    }
  }

}