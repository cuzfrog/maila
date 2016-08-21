package com.github.cuzfrog.tool.bmt

import java.util.Base64

import com.github.cuzfrog.utils.EncryptTool

/**
  * Created by cuz on 2016-08-09.
  */
private[bmt] class Keys(val pwEncoding: String) {
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

  private lazy val sRandom = new scala.util.Random(new java.security.SecureRandom())

  def randomKey: String = sRandom.alphanumeric.take(16).mkString

  def encrypt(pw: String, key: Option[String]): String = {
    val k = key match {
      case Some(k) => k
      case None => randomKey
    }

    s"Encrypted password: ${EncryptTool.encryptToBase64(pw, k, pwEncoding)}  with key:$k "
  }
}
