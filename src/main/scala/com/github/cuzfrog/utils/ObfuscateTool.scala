package com.github.cuzfrog.utils

import java.io.{BufferedInputStream, BufferedOutputStream, FileInputStream, FileOutputStream}
import java.security.InvalidKeyException
import java.util.Random
import javax.crypto.{BadPaddingException, IllegalBlockSizeException}

/**
  * Created by cuz on 2016-08-11.
  */
private[cuzfrog] object ObfuscateTool {
  /**
    * Obfuscate a file by given keys list and return an unencrypted byte array standing for that file.
    * When trying to obfuscate an already blobbed file, the method will try to decrypt it with given list
    * of keys and return decrypted bytes data, while remaining the file a blob.
    * @param path absolute file path of the file.
    * @param keys AES keys list to encrypt or decrypt the file. One of the keys will be randomly chosen
    *             to do the encryption work, while decrypting everyone of them is to be tried until success.
    * @return an unencrypted byte array standing for the file.
    */
  @deprecated("Drop this functionality", "0.2.0")
  def obfuscate(path: String, keys: List[Array[Byte]]): Array[Byte] = {

    val bis = new BufferedInputStream(new FileInputStream(path))
    val byteArray = Stream.continually(bis.read).takeWhile(_ != -1).map(_.toByte).toArray
    bis.close() //read the file
    val (didSucceed, decryptedString) = try {
        (true, decrypt(byteArray, keys))
      } catch {
        case e@(_: InvalidKeyException | _: IllegalBlockSizeException) => (false, null)
      }
    val unencrypted = if (didSucceed) decryptedString //return the unencrypted data
    else {
      val encrypted = encrypt(byteArray, keys(new Random(System.currentTimeMillis()).nextInt(keys.size)))
      val bos = new BufferedOutputStream(new FileOutputStream(path))
      Stream.continually(bos.write(encrypted))
      bos.close() //obfuscate the file/path
      byteArray //return the unencrypted data
    }
    unencrypted
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
}
