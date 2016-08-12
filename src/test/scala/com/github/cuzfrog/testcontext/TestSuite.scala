package com.github.cuzfrog.testcontext


import com.github.cuzfrog.maila.{Mail, Maila}
import utest._

/**
  * Created by cuz on 2016-08-12.
  */
object TestSuite extends TestSuite {
  val tests = this {
    'send {
      val maila = Maila.newInstance("D:\\MailaTest\\application.conf")
      val mail = Mail(List("cuzfrog@139.com"), "subject_t1", "text content")
      maila.send(List(mail))


    }
    'decryption {
      val TRANSFORM_KEYS = "w0j9j3pc1lht5c6b"
      val maila = Maila.newInstance("D:\\MailaTest\\config.xml", TRANSFORM_KEYS.getBytes("utf8"))
    }
    'test3 {
      val a = List[Byte](1, 2)
      a(10)
    }
  }
}
