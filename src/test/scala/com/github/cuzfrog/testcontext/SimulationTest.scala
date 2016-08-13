package com.github.cuzfrog.testcontext

import java.time.LocalDate

import com.github.cuzfrog.maila.{Mail, MailFilter, Maila}
import com.icegreen.greenmail.junit.GreenMailRule
import com.icegreen.greenmail.util.{GreenMailUtil, ServerSetup}
import org.junit.Assert._
import org.junit.{Before, Rule, Test}

import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.util.Random

/**
  * Created by cuz on 2016-08-12.
  */
class SimulationTest {

  import ServerSetup._

  private val server = new GreenMailRule(Array(SMTP, POP3))
  private val user0Key = "JYFi0VFzoUNZxLyj".getBytes("utf8")
  private val users = (0 until 100).map(i => (s"user$i@localhost.com", s"user$i", s"password$i$i"))
  @Rule
  def greenMail = server

  @Before
  def createUser(): Unit = {
    users.map(u => greenMail.setUser(u._1, u._2, u._3))
    println("[Test begins]I'm user0 by application.conf, I'm trying to interact with other users through mails.")
  }

  @Test
  def sendUsingKey(): Unit = {
    val mail1 = Mail(List("user1@localhost.com", "user2@localhost.com"), "subject1", "content1")
    val mail2 = Mail(List("user3@localhost.com", "user4@localhost.com"), "subject2", "content2")
    val maila = Maila.newInstance(key = user0Key)
    maila.send(List(mail1, mail2))
    val msgsOnServer = greenMail.getReceivedMessages
    val oneMail = msgsOnServer.find(m => m.getSubject == "subject2").get
    assertEquals("user0", oneMail.getFrom.head.toString)
    assertTrue(oneMail.getAllRecipients.exists(a => a.toString == "user3@localhost.com"))
    assertTrue(oneMail.getAllRecipients.exists(a => a.toString == "user4@localhost.com"))
    assertEquals(2, oneMail.getAllRecipients.length)
    assertEquals(4, greenMail.getReceivedMessages.length)
  }

  @Test
  def sendUsingPw(): Unit = {
    val maila = Maila.newInstance(askPassword = "password00")
    val mail1 = Mail(List("user1@localhost.com"), "subject1", "text content:" + LocalDate.now())
    maila.send(List(mail1))
    assertEquals(1, greenMail.getReceivedMessages.length)
  }

  @Test
  def readUsingDelayedPw(): Unit = {
    import scala.concurrent.duration._
    def getPw = Future {
      Thread.sleep(300)
      "password00"
    }
    val maila = Maila.newInstance(askPassword = Await.result(getPw, 600 milliseconds))
    GreenMailUtil.sendTextEmailTest("user0@localhost.com", "user5@localhost.com", "subject5", "text5")
    GreenMailUtil.sendTextEmailTest("user0@localhost.com", "user6@localhost.com", "subject6", "text6")


  }

  @Test
  def simulateSingleRecipient(): Unit = {

  }

  @Test
  def simulateMultipleRecipient(): Unit = {

  }
}
