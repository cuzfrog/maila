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
  lazy val user0Key = "JYFi0VFzoUNZxLyj".getBytes("utf8")


  //I'am user0, I try to interact with other users via email.
  val tests = this {
    'send_using_key {
      val maila = Maila.newInstance(key = user0Key)
      val mail1 = Mail(List("user1@localhost.com", "user2@localhost.com"),
        "subject1", "text content:" + LocalDate.now())
      val mail2 = Mail(List("user3@localhost.com", "user4@localhost.com"),
        "subject2", "text content:" + LocalDate.now())
      maila.send(List(mail1, mail2))
      val msgsOnServer = greenMail.getReceivedMessages
      val oneMail = msgsOnServer.find(m => m.getSubject == "subject2").get
      oneMail.getFrom.toString ==> "user0@localhost.com"
      oneMail.getAllRecipients.exists(a => a.toString == "user3@localhost.com") ==> true
      oneMail.getAllRecipients.exists(a => a.toString == "user4@localhost.com") ==> true
      oneMail.getAllRecipients.length ==> 2
      greenMail.getReceivedMessages.length ==> 2
    }
    'send_using_key {
      val maila = Maila.newInstance(key = user0Key)
      val mail1 = Mail(List("user1@localhost.com"), "subject1", "text content:" + LocalDate.now())
      maila.send(List(mail1))
    }
    'read {
      val a = List[Byte](1, 2)
      a(10)
    }
    'decrypt {
      val TRANSFORM_KEYS = "w0j9j3pc1lht5c6b"
      //val maila = Maila.newInstance("D:\\MailaTest\\config.xml", TRANSFORM_KEYS.getBytes("utf8"))
    }

  }
}
