package com.github.cuzfrog.testcontext

import java.time.{LocalDate, Month}

import com.github.cuzfrog.maila.{MailFilter, Maila}

/**
  * Created by cuz on 2016-08-10.
  */
object ReadTest extends App {
  System.setProperty("config.file","""D:\MailaTest\application.conf""")
  val maila = Maila.newInstance(key = "JPATdo6ZJD1I2cy2".getBytes("utf8"))

  val filter = MailFilter(
    maxSearchAmount = 1,
    filter = m => m.subject.length>3 && m.receiveDate.isAfter(LocalDate.of(2016, Month.APRIL, 1))
  )
  val mails = maila.read(filter) //get a List of mails
  mails.foreach(m => println(m.contentText)) //print text content

}
