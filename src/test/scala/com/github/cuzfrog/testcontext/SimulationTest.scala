package com.github.cuzfrog.testcontext

import java.security.KeyException
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicInteger
import javax.mail.AuthenticationFailedException

import com.github.cuzfrog.maila.{Mail, Maila}
import com.icegreen.greenmail.junit.GreenMailRule
import com.icegreen.greenmail.util.{GreenMailUtil, ServerSetupTest}
import org.junit.Assert._
import org.junit.{Before, BeforeClass, Rule, Test}

import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.util.Random

import org.hamcrest.CoreMatchers._

object SimulationTest {
  @BeforeClass
  def printInfo(): Unit = println("[Test begins]I'm user0 by application.conf, I'm trying to interact with other users through mails.")
}

/**
  * Created by cuz on 2016-08-12.
  */
class SimulationTest {

  import ServerSetupTest._

  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.duration._

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
  def usingKey(): Unit = {
    val mail1 = Mail(List("user1@localhost.com", "user2@localhost.com"), "subject1", "content1")
    val mail2 = Mail(List("user3@localhost.com", "user4@localhost.com"), "subject2", "content2")
    val maila = Maila.newInstance(key = user0Key)
    maila.send(List(mail1, mail2))
    val msgsOnServer = greenMail.getReceivedMessages
    val oneMail = msgsOnServer.find(m => m.getSubject == "subject2").get
    assertEquals("user0@localhost.com", oneMail.getFrom.head.toString)
    assertTrue(oneMail.getAllRecipients.exists(a => a.toString == "user3@localhost.com"))
    assertTrue(oneMail.getAllRecipients.exists(a => a.toString == "user4@localhost.com"))
    assertEquals(2, oneMail.getAllRecipients.length)
    assertEquals(4, greenMail.getReceivedMessages.length)

    maila.read()
  }

  @Test
  def usingPw(): Unit = {
    val maila = Maila.newInstance(askPassword = "password00")
    val mail1 = Mail(List("user1@localhost.com"), "subject1", "text content:" + LocalDate.now())
    maila.send(List(mail1))
    assertEquals(1, greenMail.getReceivedMessages.length)

    GreenMailUtil.sendTextEmailTest("user0@localhost.com", "user15@localhost.com", "subject15", "text15")
    val receivedMail = maila.read().head
    assertThat(receivedMail.subject, equalTo("subject15"))
    assertThat(receivedMail.sender, containsString("user15"))
    assertThat(receivedMail.recipients.head, containsString("user0"))
  }

  @Test
  def usingDelayedPw(): Unit = {

    def getPw = Future {
      Thread.sleep(300)
      "password00"
    }
    val maila = Maila.newInstance(askPassword = Await.result(getPw, 600 milliseconds))
    GreenMailUtil.sendTextEmailTest("user0@localhost.com", "user5@localhost.com", "subject5", "text5")
    GreenMailUtil.sendTextEmailTest("user0@localhost.com", "user6@localhost.com", "subject6", "text6")

    val receivedMails = maila.read()
    assertEquals(2, receivedMails.length)
    val m1 = receivedMails.head
    //println(s"${m1.subject}|${m1.contentText}|${m1.recipients}|${m1.sender}")
    assertTrue(receivedMails.exists(m => m.subject == "subject5" && m.sender.contains("user5") && m.contentText.contains("text5")))
  }

  @Test(expected = classOf[IllegalArgumentException])
  def usingInvalidKey(): Unit = {
    val maila = Maila.newInstance(key = "invalidKey".getBytes("utf8"))
    maila.read()
  }

  @Test(expected = classOf[KeyException])
  def usingWrongKey(): Unit = {
    val maila = Maila.newInstance(key = "JYFi0VFzoUN_miss".getBytes("utf8"))
    maila.read()
  }

  @Test(expected = classOf[AuthenticationFailedException])
  def usingWrongPwRead(): Unit = {
    val maila = Maila.newInstance(askPassword = "wrongPw")
    val mail1 = Mail(List("user99@localhost.com"), "subject_wrongPw", "text content:" + LocalDate.now())
    maila.send(Seq(mail1)) //green mail does not support any smtp authentication
    maila.read()
  }

  @Test(timeout = 10000)
  def concurrentSimulationMultipleRecipient(): Unit = {
    def getPw = Future {
      Thread.sleep(20)
      "password00"
    }
    val mailas = List(Maila.newInstance(key = user0Key),
      Maila.newInstance(askPassword = Await.result(getPw, Duration.Inf)))

    def randomString(length: Int) = Random.alphanumeric.take(length + 1).mkString
    def randomUser = users(Random.nextInt(users.length))._1
    def randomRecipient: Seq[String] = (0 to Random.nextInt(30)).map(i => randomUser)
    def randomMail = Mail(randomRecipient, randomString(8), randomString(Random.nextInt(8000)))
    def randomMailBundle = (0 to Random.nextInt(30)).map(i => randomMail)

    val counter = new AtomicInteger(0)

    mailas.foreach {
      maila =>
        val mails = randomMailBundle
        maila.send(mails, isParallel = true)
        val cnt = counter.addAndGet(mails.map(_.recipients.size).sum)
      //println(s"$cnt mails sent.")
    }

    Thread.sleep(300)
    val msgsOnServer = greenMail.getReceivedMessages
    assertThat(counter.get(), equalTo(msgsOnServer.size))

    mailas.head.read()
  }

  @Test
  def imapTest(): Unit = {
    System.setProperty("config.resource", "imap.conf")
    val maila = Maila.newInstance(user = "user0@localhost.com", password = "password00")
    System.clearProperty("config.resource")
    GreenMailUtil.sendTextEmailTest("user0@localhost.com", "user25@localhost.com", "subject25", "text25")
    GreenMailUtil.sendTextEmailTest("user0@localhost.com", "user26@localhost.com", "subject26", "text26")

    val receivedMails = maila.read()
    assertEquals(2, receivedMails.length)
    assertTrue(receivedMails.exists(m => m.subject == "subject25" && m.sender.contains("user25") && m.contentText.contains("text25")))
    val m1 = receivedMails.head
    assertThat(m1.receiveDate,equalTo(LocalDate.now()))
  }
}
