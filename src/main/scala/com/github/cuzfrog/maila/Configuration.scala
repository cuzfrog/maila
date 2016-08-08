package com.github.cuzfrog.maila

import java.io.{BufferedInputStream, BufferedOutputStream, FileInputStream, FileOutputStream}
import java.security.InvalidKeyException
import java.util.Random
import javax.crypto.{BadPaddingException, IllegalBlockSizeException}

import com.github.cuzfrog.utils.EncryptTool

import scala.xml.{Elem, XML}

private[maila] trait Configuration {
  def hostPop3: String
  def hostSmtp: String
  def user: String
  def password: String
}

private[maila] object Configuration  {
  def fromFile(path: String, willObfuscate: Boolean = false, keys: List[Array[Byte]] = null): Configuration = {
    val xml = if (willObfuscate) {
      XML.loadString(new String(obfuscate(path, keys), "utf8"))
    } else XML.loadFile(path)
    new XmlConfiguration(xml)
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
      case e @ (_: InvalidKeyException | _: IllegalBlockSizeException) => (false, null)
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

  private class XmlConfiguration(xml: Elem) extends Configuration {
    lazy val hostPop3 = (xml \ "host").head.attribute("pop3").get.head.text
    lazy val hostSmtp = (xml \ "host").head.attribute("smtp").get.head.text
    lazy val user = (xml \ "user").head.text
    lazy val password = (xml \ "password").head.text
  }

  private class SpecifiedConfiguration(val hostPop3: String,val hostSmtp: String, val user: String, val password: String) extends Configuration
}
