package com.github.cuzfrog.tool.bmt

import com.github.cuzfrog.utils.SimpleLogger
import com.icegreen.greenmail.junit.GreenMailRule
import com.sun.media.sound.InvalidFormatException
import org.junit.{Before, Rule, Test}


/**
  * Created by Cause Frog on 8/17/2016.
  */
class BmtTest extends SimpleLogger {

  import com.icegreen.greenmail.util.ServerSetupTest._

  private val server = new GreenMailRule(Array(SMTP, POP3, IMAP))

  private val user0Key = "JYFi0VFzoUNZxLyj".getBytes("utf8")
  private val users = (0 until 100).map(i => (s"user$i@localhost.com", s"user$i@localhost.com", s"password$i$i"))
  private val commonArgs = Array("-p:password00", "-c:src/test/resources/application.conf")

  private def bmt(args:Array[String])=new BatchMailTool(args)

  @Rule
  def greenMail = server

  @Before
  def createUser(): Unit = {
    users.map(u => greenMail.setUser(u._1, u._2, u._3))
  }

  @Test
  def sendMails(): Unit = {
    val f = "bmt/src/test/resources/mails.csv"
    bmt(Array("test", s"-m:$f") ++ commonArgs).run().map(throw _)
    bmt(Array("send", s"-m:$f") ++ commonArgs).run().map(throw _)
  }

  @Test(expected = classOf[InvalidFormatException])
  def mailformattedMails(): Unit = {
    val f = "bmt/src/test/resources/malformatted.csv"
    bmt(Array("send", s"-m:$f") ++ commonArgs).run().map(throw _)

    val msgsOnServer = greenMail.getReceivedMessages
    val r = msgsOnServer.map(m => s"${m.getSubject}|${m.getContent}").mkString(System.lineSeparator())
    info(r)
  }

  @Test
  def ecapseTextMails(): Unit = {
    val f = "bmt/src/test/resources/escapeText.csv"
    bmt(Array("send", s"-m:$f") ++ commonArgs).run().map(throw _)

    val msgsOnServer = greenMail.getReceivedMessages
    val r = msgsOnServer.map(m => s"${m.getSubject}|${m.getContent}").mkString(System.lineSeparator())
    info(r)
  }
}
