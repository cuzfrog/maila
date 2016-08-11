package com.github.cuzfrog.tool.bmt

import java.security.{MessageDigest, SecureRandom}

import com.github.cuzfrog.utils.EncryptTool

/**
  * Created by cuz on 2016-08-09.
  */
private[bmt] object Keys {
  def DEFAULT_KEYS = {
    BatchMailTool.p("[Warning]: you are using default keys, which don't provide any real security.")
    List("w0j9j3pc1lht5c6b",
      "pelila8h8xyduk8u",
      "pqzlv3646t5czf43",
      "rlea96gwkutwhz4m",
      "7v3txdd4hcv0e1jd",
      "v6k98fmyags5ugfi",
      "uae6c909uc031a3l",
      "5rtsom1rerkdqg6s",
      "20o06zwhrv5uqflt",
      "104e8spzwv5c2s32")
  }

  private lazy val sRandom = SecureRandom.getInstanceStrong
  private lazy val md = MessageDigest.getInstance("MD5")

  def randomKey: String = {
    md.reset()
    new String(md.digest(sRandom.nextInt.toString.getBytes("utf8")), "utf8").take(16)
  }

  def encrypt(pw: String, key: String): String = {
    val k = if (key.isEmpty) randomKey else key
    val encrypted = EncryptTool.encrypt(pw.getBytes("utf8"), k.getBytes("utf8"))
    s"Encrypted password: ${new String(encrypted, "utf8")}  with key:$k "
  }
}
