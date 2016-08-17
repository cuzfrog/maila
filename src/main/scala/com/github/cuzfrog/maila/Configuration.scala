package com.github.cuzfrog.maila

import java.security.KeyException
import java.util.Properties
import javax.crypto.{BadPaddingException, IllegalBlockSizeException}

import com.github.cuzfrog.utils.EncryptTool
import com.typesafe.config.{Config, ConfigFactory}

private[maila] trait Configuration {

  def user: String

  def password: String

  def serverProps: Properties

  def storeType: String

  def transportType: String
}

private[maila] object Configuration {

  def withAuth(user: String, password: String): Configuration = {
    val _user = user
    val _password = password
    if (_user == null || _password == null) new TypesafeConfiguration with PlaintextPw
    else new TypesafeConfiguration with SpecifyAuth {
      override val user: String = _user
      override val password: String = _password
    }
  }

  def withKey(aesKey: Array[Byte]): Configuration = {
    require(aesKey.length == 16 || aesKey.length == 24 || aesKey.length == 32, s"Bad key length:${aesKey.length}")

    new TypesafeConfiguration with EncryptedPw {
      override def key = aesKey: Array[Byte]
    }
  }

  def withPassword(askPassword: => String): Configuration = {
    new TypesafeConfiguration with AskPw {
      override def ask: String = askPassword
    }
  }

  lazy val config = {
    ConfigFactory.invalidateCaches()
    ConfigFactory.load().withFallback(ConfigFactory.load("reference.conf")).getConfig("maila")
  }

  private abstract class TypesafeConfiguration extends Configuration {
    override val serverProps = propsFromConfig(config.getConfig("server"))
    override val user: String = config.getString("authentication.user")
    override val storeType: String = config.getString("reader.store.type")
    override val transportType: String = config.getString("sender.transport.type")

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
    def user: String

    def password: String
  }

  private trait PlaintextPw extends PasswordStrategy {
    override val password: String = {
      require(config.getBoolean("authentication.allow-none-encryption-password")
        , "allow-none-encryption-password is set to false, must provide password.")
      config.getString("authentication.password")
    }
  }

  private trait SpecifyAuth extends PasswordStrategy {
    override val user: String
    override val password: String
  }

  private trait AskPw extends PasswordStrategy {
    def ask: String

    override lazy val password = ask
  }

  private trait EncryptedPw extends PasswordStrategy {
    def key: Array[Byte]

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
