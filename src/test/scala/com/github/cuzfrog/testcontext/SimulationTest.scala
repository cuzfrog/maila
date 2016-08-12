package com.github.cuzfrog.testcontext

import java.time.LocalDate

import com.github.cuzfrog.maila.{Mail, Maila}
import com.icegreen.greenmail.junit.GreenMailRule
import com.icegreen.greenmail.util.ServerSetupTest
import org.junit.Assert._
import org.junit.{Before, Rule, Test}

/**
  * Created by cuz on 2016-08-12.
  */
object SimulationTest {

  @Rule
  final val greenMail = new GreenMailRule(ServerSetupTest.ALL)
  final val user0Key = "JYFi0VFzoUNZxLyj".getBytes("utf8")

  @Before
  def createUser(): Unit = (0 to 100).map { i =>
    greenMail.setUser(s"user$i@localhost.com", s"user$i", s"password$i$i")
  }

  //I'am user0, I try to interact with other users via email.

  @Test
  def sendUsingKey(): Unit = {
    println("test sendUsingKey begin")
    val maila = Maila.newInstance(key = user0Key)
    val mail1 = Mail(List("user1@localhost.com", "user2@localhost.com"),
      "subject1", "text content:" + LocalDate.now())
    val mail2 = Mail(List("user3@localhost.com", "user4@localhost.com"),
      "subject2", "text content:" + LocalDate.now())
    maila.send(List(mail1, mail2))
    val msgsOnServer = greenMail.getReceivedMessages
    val oneMail = msgsOnServer.find(m => m.getSubject == "subject2").get
    assertEquals("user0@localhost.com", oneMail.getFrom.toString)
    assertTrue(oneMail.getAllRecipients.exists(a => a.toString == "user3@localhost.com"))
    assertTrue(oneMail.getAllRecipients.exists(a => a.toString == "user4@localhost.com"))
    assertEquals(2, oneMail.getAllRecipients.length)
    assertEquals(2, greenMail.getReceivedMessages.length)
    println("test sendUsingKey end")
  }

  @Test
  def sendUsingPw(): Unit = {
    val maila = Maila.newInstance(askPassword = "password00")
    val mail1 = Mail(List("user1@localhost.com"), "subject1", "text content:" + LocalDate.now())
    maila.send(List(mail1))
    assertEquals(1, greenMail.getReceivedMessages.length)
  }
}
