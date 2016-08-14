package com.github.cuzfrog.maila

import com.github.cuzfrog.maila.server.Server
import com.typesafe.config.Config

trait Maila {
  /**
    * Read mails using POP3.
    *
    * @param mailFilter filter mails to be read. Default filter fetches latest 30 mails.
    * @return mails conform the filter.
    */
  def read(mailFilter: MailFilter = MailFilter.default): List[Mail]

  /**
    * Send a sequence of mails.
    *
    * @param mails a sequence of mails to be sent.
    * @return sending results and messages. like: (true, ".. success..")
    */
  def send(mails: Seq[Mail]): Seq[(Boolean, String)]

  /**
    * Get current config.
    *
    * @param path config relative path inside root maila.
    * @return current config
    */
  def getConfig(path: String): Config
}

object Maila {

  /**
    * Create a new instance and assume password is stored as plain text in config file
    * with "allow-none-encryption-password" set to true.
    * When assumption is not met, this creation fails immediately.
    *
    * @return a new instance ready to access mail.
    */
  def newInstance: Maila = {
    val config = Configuration.withNormal
    new SimpleMaila(config)
  }

  /**
    * Create a new instance loaded with configuration and a key for encrypted password.
    *
    * @param key used to decrypt password in config file.<br>
    *            If not specified and "allow-none-encryption-password" in config is set to true,
    *            application tries to use password as plain text.
    * @return a new instance ready to access mail.
    */
  def newInstance(key: Array[Byte]): Maila = {
    require(key != null, "key must not be null.")
    val config = Configuration.withKey(key)
    new SimpleMaila(config)
  }

  /**
    * Create a new instance loaded with configuration and a lazy password.
    *
    * @param askPassword a call-by-name lazy password, which can reference to a custom providing logic.
    * @return a new instance ready to access mail.
    */
  def newInstance(askPassword: => String): Maila = {
    val config = Configuration.withPassword(askPassword)
    new SimpleMaila(config)
  }

  private class SimpleMaila(config: Configuration) extends Maila {
    override def getConfig(path: String = ""): Config =
      if (path.isEmpty) config.config else config.config.getConfig(path)

    lazy val server = Server(config)
    lazy val senderLogging = config.config.getBoolean("sender.logging")

    def read(mailFilter: MailFilter): List[Mail] = {
      val reader = server.reader
      val mails = reader.mails(mailFilter)
      reader.shutdown()
      mails
    }

    def send(mails: Seq[Mail]): Seq[(Boolean, String)] = {
      val sender = server.sender
      val result = mails.map { m =>
        val res = sender.send(m.recipients, m.subject, m.contentText)
        if (senderLogging) println(s"[maila]${res._2}")
        res
      }
      sender.close()
      result
    }
  }

}