package com.github.cuzfrog.maila

import java.io.{BufferedInputStream, BufferedOutputStream, FileInputStream, FileOutputStream}
import java.security.InvalidKeyException
import java.util.{Properties, Random}
import javax.crypto.{BadPaddingException, IllegalBlockSizeException}

import com.github.cuzfrog.utils.EncryptTool
import com.typesafe.config.{Config, ConfigFactory}

private[maila] trait Configuration {

  def user: String

  def password: String

  def serverProps: Properties
}

private[maila] object Configuration {

  def propsFromConfig(config: Config): Properties = {
    import scala.collection.JavaConversions._

    val props = new Properties()

    val map: Map[String, Object] = config.entrySet().map({ entry =>
      entry.getKey -> entry.getValue.unwrapped()
    })(collection.breakOut)

    props.putAll(map)
    props
  }

  def fromFile(path: String, willObfuscate: Boolean = false, keys: List[Array[Byte]] = null): Configuration = {
    //todo: change obfuscation against typesafe config
    new TypesafeConfiguration(path)
  }

  private def obfuscate(path: String, keys: List[Array[Byte]]): Array[Byte] = {
    require(keys.nonEmpty && !keys.exists(_.length < 16),
      "Key lenth:" + keys.size + "|too-short key lenth:" + keys.find(_.length < 16).map(_.length))

    val bis = new BufferedInputStream(new FileInputStream(path))
    val byteArray = Stream.continually(bis.read).takeWhile(_ != -1).map(_.toByte).toArray
    bis.close() //read the file
    val (didSucceed, decryptedString) = try {
        (true, decrypt(byteArray, keys))
      } catch {
        case e@(_: InvalidKeyException | _: IllegalBlockSizeException) => (false, null)
      }
    val uncrypted = if (didSucceed) decryptedString //return the uncrypted data
    else {
      val encrypted = encrypt(byteArray, keys(new Random(System.currentTimeMillis()).nextInt(keys.size)))
      val bos = new BufferedOutputStream(new FileOutputStream(path))
      Stream.continually(bos.write(encrypted))
      bos.close() //obfuscate the file/path
      byteArray //return the uncrypted data
    }
    uncrypted
  }

  private def decrypt(encrypted: Array[Byte], keys: List[Array[Byte]]): Array[Byte] = {
    keys.foreach {
      key =>
        try {
          return EncryptTool.decrypt(encrypted, key)
        } catch {
          case _: InvalidKeyException | _: BadPaddingException => //try every key.
        }
    }
    throw new InvalidKeyException("All keys have been tried, decrypt failed.")
  }

  private def encrypt(input: Array[Byte], key: Array[Byte]): Array[Byte] = try {
    EncryptTool.encrypt(input, key)
  } catch {
    case e: Throwable => throw new AssertionError("Encrypt failed, cause:" + e.toString + "|" + e.getMessage)
  }

  private class TypesafeConfiguration(configPath: String) extends Configuration {
    System.setProperty("config.file", configPath)
    private val config = ConfigFactory.load().withFallback(ConfigFactory.load("reference.conf"))
    override val serverProps = propsFromConfig(config.getConfig("maila.server"))
    override val user: String = config.getString("maila.authentication.user")
    override val password: String = config.getString("maila.authentication.password")
  }

}
