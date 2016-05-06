package com.github.cuzfrog.utils

import javax.crypto.spec.SecretKeySpec
import javax.crypto.Cipher

private[cuzfrog] object EncryptTool {
  def decrypt(encrypted: Array[Byte], key: Array[Byte]): Array[Byte] = {
    // Create key and cipher
    val aesKey = new SecretKeySpec(key, "AES");
    val cipher = Cipher.getInstance("AES");

    // decrypt the text
    cipher.init(Cipher.DECRYPT_MODE, aesKey);
    return cipher.doFinal(encrypted)
  }

  def encrypt(input: Array[Byte], key: Array[Byte]): Array[Byte] = {
    // Create key and cipher
    val aesKey = new SecretKeySpec(key, "AES");

    val cipher = Cipher.getInstance("AES");
    // encrypt the text
    cipher.init(Cipher.ENCRYPT_MODE, aesKey);
    return cipher.doFinal(input);

  }
}