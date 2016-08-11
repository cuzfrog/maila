package com.github.cuzfrog.maila

import java.util.Properties

import com.github.cuzfrog.utils.EncryptTool
import com.typesafe.config.{Config, ConfigFactory}


private[maila] trait Configuration {

  def user: String

  def password: String

  def serverProps: Properties

  def storeType: String

  def transportType: String

  def config: Config
}

private[maila] object Configuration {

  def fromFile(path: String): Configuration = {
    new TypesafeConfiguration(path)
  }

  def fromFileWithKey(path: String, aesKey: Array[Byte]): Configuration = {
    new TypesafeConfigurationWithEncryption(path, aesKey: Array[Byte])
  }

  def fromFileWithPassword(path: String, askPassword: => String): Configuration = {
    new TypesafeConfigurationWithPassword(path, askPassword)
  }

  private class TypesafeConfiguration(configPath: String) extends Configuration {
    System.setProperty("config.file", configPath)
    override val config = ConfigFactory.load().withFallback(ConfigFactory.load("reference.conf")).getConfig("maila")
    override val serverProps = propsFromConfig(config.getConfig("server"))
    override val user: String = config.getString("authentication.user")
    override val password: String = config.getString("authentication.password")
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

  private class TypesafeConfigurationWithPassword(configPath: String, askPassword: => String)
    extends TypesafeConfiguration(configPath) {
    override lazy val password = askPassword
  }

  private class TypesafeConfigurationWithEncryption(configPath: String, aesKey: Array[Byte])
    extends TypesafeConfiguration(configPath) {
    override lazy val password: String =
      if (aesKey.isEmpty && config.getBoolean("authentication.allow-none-encryption-password")) {
        config.getString("authentication.password")
      }
      else config.getEncryptedString("authentication.password", aesKey)

    implicit private class RichConfig(in: Config) {
      def getEncryptedString(path: String, aesKey: Array[Byte]): String = {
        require(aesKey.length == 16 || aesKey.length == 24 || aesKey.length == 32, s"Bad key length:${aesKey.length}")
        new String(EncryptTool.decrypt(in.getString(path).getBytes("utf8"), aesKey), "utf8")
      }
    }

  }

}
