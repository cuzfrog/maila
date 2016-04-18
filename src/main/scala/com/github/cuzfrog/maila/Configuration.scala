package com.github.cuzfrog.maila

import scala.xml.XML
import scala.xml.Elem
import javax.crypto.spec.SecretKeySpec
import javax.crypto.Cipher
import com.github.cuzfrog.utils.EncryptTool
import java.security.InvalidKeyException
import scala.io.Source
import java.util.Random
import java.io.FileOutputStream
import java.io.BufferedOutputStream

private[maila] trait Configuration {
  def hostPop3: String
  def user: String
  def password: String
}

private[maila] object Configuration {
  def fromFile(path: String, willObfuscate: Boolean = false, keys: List[Array[Byte]] = null): Configuration = {
    val xml = if (willObfuscate) XML.loadString(new String(obfuscate(path, keys), "utf8"))
    else XML.loadFile(path)
    new XmlConfiguration(xml)
  }

  private def obfuscate(path: String, keys: List[Array[Byte]]): Array[Byte] = {
    require(keys.nonEmpty && !keys.exists(_.length != 128))
    val source = scala.io.Source.fromFile(path, "utf8")
    val byteArray = source.map(_.toByte).toArray
    source.close()
    val (didSucceed, decryptedString) = try {
      (true, decrypt(byteArray, keys))
    } catch {
      case e: InvalidKeyException => (false, null)
    }
    val uncrypted = if (didSucceed) decryptedString //return the uncrypted data
    else {
      val encrypted = encrypt(byteArray, keys(new Random(System.currentTimeMillis()).nextInt(keys.size)))
      val bos = new BufferedOutputStream(new FileOutputStream(path))
      Stream.continually(bos.write(byteArray))
      bos.close() //obfuscate the file/path
      byteArray //return the uncrypted data
    }
    return uncrypted
  }

  private def decrypt(encrypted: Array[Byte], keys: List[Array[Byte]]): Array[Byte] = {
    val out = keys.foreach {
      key =>
        try {
          return EncryptTool.decrypt(encrypted, key)
        } catch {
          case e: InvalidKeyException => //try every key.
        }
    }
    throw new InvalidKeyException("All keys have been tried, decrypt failed.")
  }
  private def encrypt(input: Array[Byte], key: Array[Byte]): Array[Byte] = try {
    EncryptTool.encrypt(input, key)
  } catch {
    case e: Throwable => throw new AssertionError("Encrypt failed, cause:" + e.toString() + "|" + e.getMessage)
  }

  private class XmlConfiguration(xml: Elem) extends Configuration {
    val hostPop3 = (xml \ "host").head.attribute("pop3").get.head.text
    val user = (xml \ "user").head.text
    val password = (xml \ "password").head.text
  }

  private class SpecifiedConfiguration(val hostPop3: String, val user: String, val password: String) extends Configuration
}
