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

  final val TRANSFORM_KEYS = "w0j9j3pc1lht5c6b"

  val mailaWithObfuscation = Maila.newInstance("D:\\MailaTest\\config.xml", TRANSFORM_KEYS.getBytes("utf8"))
}
