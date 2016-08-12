package com.github.cuzfrog.testcontext


import java.time.LocalDate

import com.github.cuzfrog.maila.{Mail, Maila}
import com.icegreen.greenmail.junit.GreenMailRule
import com.icegreen.greenmail.util.ServerSetupTest
import utest._

/**
  * Created by cuz on 2016-08-12.
  */
object TestSuite extends TestSuite {
  lazy val greenMail = new GreenMailRule(ServerSetupTest.ALL)
  lazy val users = (0 to 100).map { i =>
    greenMail.setUser(s"user$i@localhost.com", s"user$i", s"password$i$i")
  }
  lazy val user0Key = "JYFi0VFzoUNZxLyj"

  val tests = this {
    'send {
      val maila = Maila.newInstance("D:\\MailaTest\\application.conf")
      val mail = Mail(List("user1@localhost.com", "user2@localhost.com"),
        "subject_to1", "text content:" + LocalDate.now())
      maila.send(List(mail))
    }
    'read {
      val a = List[Byte](1, 2)
      a(10)
    }
    'decrypt {
      val TRANSFORM_KEYS = "w0j9j3pc1lht5c6b"
      val maila = Maila.newInstance("D:\\MailaTest\\config.xml", TRANSFORM_KEYS.getBytes("utf8"))
    }

  }
}
