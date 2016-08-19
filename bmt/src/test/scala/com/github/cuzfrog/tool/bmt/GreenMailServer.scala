package com.github.cuzfrog.tool.bmt

import ch.qos.logback.classic.{Level, Logger}
import com.icegreen.greenmail.util.GreenMail
import org.slf4j.LoggerFactory

/**
  * Created by Cause Frog on 8/19/2016.
  */
object GreenMailServer extends App {

  import com.icegreen.greenmail.util.ServerSetup._

  LoggerFactory.getLogger("com.icegreen.greenmail").asInstanceOf[Logger].setLevel(Level.DEBUG)
  val server = new GreenMail(SMTP_POP3)
  (0 until 100).map(i => (s"user$i@localhost.com", s"user$i@localhost.com", s"password$i$i"))
    .map(u => server.setUser(u._1, u._2, u._3))

  server.start()
  print("Input any thing to terminate>")
  scala.io.StdIn.readChar()

  server.stop()
}
