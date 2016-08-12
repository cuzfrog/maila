package com.github.cuzfrog.utils

import java.util.Base64
import javax.crypto.spec.SecretKeySpec
import javax.crypto.Cipher

import jdk.internal.util.xml.impl.Input

private[cuzfrog] object EncryptTool {


  def decrypt(encrypted: Array[Byte], key: Array[Byte]): Array[Byte] = {
    // Create key and cipher
    val aesKey = new SecretKeySpec(key, "AES")
    val cipher = Cipher.getInstance("AES")

    // decrypt the text
    cipher.init(Cipher.DECRYPT_MODE, aesKey)
    cipher.doFinal(encrypted)
  }

  def encrypt(input: Array[Byte], key: Array[Byte]): Array[Byte] = {
    // Create key and cipher
    val aesKey = new SecretKeySpec(key, "AES")

    val cipher = Cipher.getInstance("AES")
    // encrypt the text
    cipher.init(Cipher.ENCRYPT_MODE, aesKey)
    cipher.doFinal(input)
  }

  def encryptToBase64(input: String, key: String, charset: String = "utf8"): String = {
    val in = input.getBytes(charset)
    val k = key.getBytes(charset)
    encryptToBase64(in, k)
  }

  def encryptToBase64(input: Array[Byte], key: Array[Byte]): String = {
    val encrypted = encrypt(input, key)
    val encoded = Base64.getEncoder.encodeToString(encrypted)
    encoded
  }

  def decryptFromBase64(input: String, key: String, charset: String = "utf8"): String = {
    val k = key.getBytes(charset)
    new String(decryptFromBase64(input, k), charset)
  }

  def decryptFromBase64(input: String, key: Array[Byte]): Array[Byte] = {
    val decoded = Base64.getDecoder.decode(input)
    val decrypted = decrypt(decoded, key)
    decrypted
  }

}