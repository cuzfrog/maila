package com.github.cuzfrog.maila

import com.github.cuzfrog.maila.server.Server
import com.typesafe.config.Config

import scala.concurrent.{Await, Future}
import scala.language.postfixOps

trait Maila {
  /**
    * Read mails using POP3.
    *
    * @param mailFilter filter mails to be read. Default filter fetches latest 30 mails.
    * @return mails conform the filter.
    */
  def read(mailFilter: MailFilter = MailFilter.default): Seq[Mail]

  /**
    * Send a sequence of mails.
    *
    * @param mails          a sequence of mails to be sent.
    * @param isParallel     when true, every Mail will be sent by a future.
    * @param maxWaitSeconds the max time it takes to wait for every future.
    * @return sending results and messages. like: (true, ".. success..")
    */
  def send(mails: Seq[Mail], isParallel: Boolean = false, maxWaitSeconds: Int = 5): Seq[(Boolean, String)]

  /**
    * Get current config.
    *
    * @param path config relative path inside root maila. If not provided, return root config of maila.
    * @return current config
    */
  def getConfig(path: String = ""): Config
}

object Maila {

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
    * Create a new instance with lazy user and password.
    * If one of above is not provided, it changes its scenarios:<br>
    * <ul>
    * 1.Password missing - assume password is stored as plain text in config file
    * with "allow-none-encryption-password" set to true.
    * When assumption is not met, it fails later.<br>
    * 2.User missing - look for user in config when needed, fail if not found.<br>
    * </ul>
    *
    * @param askUser lazy user, when missing, later it will fallback to config file.
    * @param askPassword lazy password, when missing, later it will fallback to config file.
    * @return a new instance ready to access mail.
    */
  def newInstance(askUser: => String = null, askPassword: => String = null): Maila = {
    val config = Configuration.withAuth(askUser, askPassword)
    new SimpleMaila(config)
  }

  private class SimpleMaila(config: Configuration) extends Maila {
    override def getConfig(path: String = ""): Config =
      if (path.isEmpty) Configuration.config else Configuration.config.getConfig(path)

    lazy val server = Server(config)
    lazy val senderLogging = Configuration.config.getBoolean("sender.logging")

    def read(mailFilter: MailFilter): Seq[Mail] = {
      val reader = server.reader
      val mails = reader.mails(mailFilter)
      reader.shutdown()
      mails
    }

    def send(mails: Seq[Mail], isParallel: Boolean, maxWaitSeconds: Int = 5): Seq[(Boolean, String)] = {
      val sender = server.sender
      val result = if (isParallel) mails.map { m =>
        val res = sender.send(m.recipients, m.subject, m.contentText)
        if (senderLogging) println(s"[maila]${res._2}")
        res
      }
      else mails.map { m =>
        import scala.concurrent.ExecutionContext.Implicits.global
        val res = Future {
          sender.send(m.recipients, m.subject, m.contentText)
        }
        if (senderLogging)
          res.onComplete(f => println(s"[maila]${f.getOrElse((false, "Future failed. This should not happen. Please issue about this."))._2}"))
        import scala.concurrent.duration._
        Await.result(res, maxWaitSeconds seconds)
      }
      sender.close()
      result
    }
  }

}