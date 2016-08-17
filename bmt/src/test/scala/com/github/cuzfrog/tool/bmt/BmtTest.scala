package com.github.cuzfrog.tool.bmt

import com.icegreen.greenmail.junit.GreenMailRule
import org.junit.{Before, Rule, Test}

/**
  * Created by Cause Frog on 8/17/2016.
  */
class BmtTest {
  import com.icegreen.greenmail.util.ServerSetupTest._
  private val server = new GreenMailRule(Array(SMTP, POP3, IMAP))

  private val user0Key = "JYFi0VFzoUNZxLyj".getBytes("utf8")
  private val users = (0 until 100).map(i => (s"user$i@localhost.com", s"user$i@localhost.com", s"password$i$i"))

  @Rule
  def greenMail = server

  @Before
  def createUser(): Unit = {
    users.map(u => greenMail.setUser(u._1, u._2, u._3))
  }

  @Test
  def sendMails(): Unit = {
    BatchMailTool.main(Array("test","-m:./src/test/resources/mails.csv","-u:user0@localhost.com","-p:password00"))
  }
}
