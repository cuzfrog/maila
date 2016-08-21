package com.github.cuzfrog.maila

import java.security.KeyException
import java.util.concurrent.atomic.AtomicReference
import java.util.{Locale, Properties}
import javax.crypto.{BadPaddingException, IllegalBlockSizeException}

import com.github.cuzfrog.utils.{DateParseTool, EncryptTool}
import com.typesafe.config.{Config, ConfigFactory}
import collection.JavaConversions._

private[maila] trait Configuration {

  def user: String

  def password: String

  def serverProps: Properties

  def storeType: String

  def transportType: String

  def senderLogging: Boolean

  def javaMailDebug: Boolean

  def folderName: String

  def isDeleteAfterFetch: Boolean

  def dateFormats: List[String]

  def locale: Locale
}

private[maila] object Configuration {

  def withAuth(askUser: => String, askPassword: => String): Configuration = {
    new TypesafeConfiguration with AskAuth {
      override def _askUser: Option[String] = {
        val user = askUser
        if (user == null || user.isEmpty) None else Some(user)
      }
      override def _askPassword: Option[String] = {
        val pw = askPassword
        if (pw == null || pw.isEmpty) None else Some(pw)
      }
    }
  }

  def withKey(aesKey: Array[Byte]): Configuration = {
    require(aesKey.length == 16 || aesKey.length == 24 || aesKey.length == 32, s"Bad key length:${aesKey.length}")
    new TypesafeConfiguration with EncryptedPw {
      override def key = aesKey: Array[Byte]
    }
  }

  /**
    * Reload config and set to current reference.
    *
    * @return new loaded config.
    */
  def reload: Config = synchronized {
    val newConfig = load
    currentConfig.set(newConfig)
    newConfig
  }

  /**
    * Get current loaded config.
    *
    * @return current config.
    */
  def get: Config = currentConfig.get() match {
    case null => throw new NoSuchElementException("Config not loaded.")
    case c => c
  }

  /**
    * Provide a custom config by client.
    *
    * @param config custom config by client.
    */
  def provide(config: Config): Config = synchronized {
    val configMerged = config.withFallback(ConfigFactory.defaultReference()).getConfig("maila")
    currentConfig.set(configMerged)
    configMerged
  }

  private def load: Config = {
    ConfigFactory.invalidateCaches()
    ConfigFactory.load().getConfig("maila")
  }

  private val currentConfig = new AtomicReference[Config]

  private abstract class TypesafeConfiguration extends Configuration {
    val config = {
      currentConfig.compareAndSet(null, load)
      currentConfig.get()
    }

    override val serverProps = propsFromConfig(config.getConfig("server"))
    override val storeType: String = config.getString("reader.store.protocol")
    override val transportType: String = config.getString("sender.transport.protocol")
    override val senderLogging: Boolean = config.getBoolean("sender.logging")
    override val javaMailDebug = config.getBoolean("server.javax.mail.debug")
    override val folderName = config.getString("reader.folder")
    override val isDeleteAfterFetch = config.getBoolean("reader.delete-after-fetch")

    override val dateFormats: List[String] = {
      val pairs = config.getConfig("reader.pop3-received-date-parse.formatter").entrySet().toList
      val configFormats = pairs.sortBy(_.getKey).map(_.getValue.unwrapped().toString)
      configFormats ++ DateParseTool.defaultFormats
    }
    override val locale = Locale.forLanguageTag(config.getString("reader.pop3-received-date-parse.locale"))

    private def propsFromConfig(config: Config): Properties = {
      import scala.collection.JavaConversions._
      val props = new Properties()
      val map: Map[String, Object] = config.entrySet().map({ entry =>
        entry.getKey -> entry.getValue.unwrapped()
      })(collection.breakOut)
      props.putAll(map)
      props
    }
  }

  private sealed trait PasswordStrategy {
    def config: Config

    def user: String

    def password: String
  }

  private trait AskAuth extends PasswordStrategy {
    override lazy val user: String = _askUser match {
      case None => config.getString("authentication.user")
      case Some(s) => s
    }
    override lazy val password: String = _askPassword match {
      case None =>
        require(config.getBoolean("authentication.allow-none-encryption-password")
          , "allow-none-encryption-password is set to false, must provide password.")
        config.getString("authentication.password")
      case Some(s) => s
    }

    def _askUser: Option[String]

    def _askPassword: Option[String]
  }

  private trait EncryptedPw extends PasswordStrategy {

    def key: Array[Byte]

    override val user: String = config.getString("authentication.user")
    override lazy val password: String = config.getEncryptedString("authentication.password", key)

    implicit private class RichConfig(in: Config) {
      def getEncryptedString(path: String, aesKey: Array[Byte]): String = {
        val decrypted = try {
          EncryptTool.decryptFromBase64(in.getString(path), aesKey)
        } catch {
          case e@(_: IllegalBlockSizeException | _: BadPaddingException) =>
            throw new KeyException("Bad key.")
        }
        new String(decrypted, config.getString("authentication.password-encoding"))
      }
    }

  }

}
