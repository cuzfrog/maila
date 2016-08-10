package com.github.cuzfrog.testcontext

import java.time.{LocalDate, Month}

import com.github.cuzfrog.maila.{Mail, MailFilter, Maila}

/**
  * Created by cuz on 2016-08-05.
  */
object MailTest extends App {
  val maila = Maila.newInstance("D:\\MailaTest\\application.conf")
  val mail = Mail(List("cuzfrog@139.com"), "subject_t1", "text content")
  maila.send(List(mail))

  final val TRANSFORM_KEYS =
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
  val mailaWithObfuscation=Maila.newInstance("D:\\MailaTest\\config.xml",willObfuscate = true,TRANSFORM_KEYS.map(_.getBytes("utf8")))
}
