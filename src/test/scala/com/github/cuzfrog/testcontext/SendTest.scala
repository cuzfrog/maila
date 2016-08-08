package com.github.cuzfrog.testcontext

import com.github.cuzfrog.maila.{Mail, Maila}

/**
  * Created by cuz on 2016-08-05.
  */
object SendTest extends App {
  val maila = Maila.newInstance("D:\\MailaTest\\config.xml")
  val mail = Mail(List("recipient@google.com"), "subject", "text content")
  maila.send(List(mail))
}
