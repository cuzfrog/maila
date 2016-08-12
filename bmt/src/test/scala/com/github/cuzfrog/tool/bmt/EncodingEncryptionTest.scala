package com.github.cuzfrog.tool.bmt

import java.util.Base64

import com.github.cuzfrog.utils.EncryptTool

/**
  * Created by cuz on 2016-08-12.
  */
object EncodingEncryptionTest extends App {
  val k = "sx9LCcEY2WmS6Rnl"
  val pw = "testpw123"
  val charset = "utf8"
  val encrypted = EncryptTool.encrypt(pw.getBytes(charset), k.getBytes(charset))
  val encoded = Base64.getEncoder.encodeToString(encrypted)
  println(s"encoded pw:$encoded")

  val decoded = Base64.getDecoder.decode(encoded)
  val decrypted = new String(EncryptTool.decrypt(decoded, k.getBytes(charset)), charset)
  println(s"decrypted:$decrypted")

}
