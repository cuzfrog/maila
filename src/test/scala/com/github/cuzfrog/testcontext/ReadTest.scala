package com.github.cuzfrog.testcontext

import java.time.{LocalDate, Month}

import com.github.cuzfrog.maila.{MailFilter, Maila}

/**
  * Created by cuz on 2016-08-10.
  */
object ReadTest extends App {
  val maila = Maila.newInstance("D:\\MailaTest\\application.conf")

  val filter = MailFilter(
    maxSearchAmount = 10,
    subjectFilter = _.contains("堡垒上传成功"),
    receiveDateFilter = _.isAfter(LocalDate.of(2016, Month.APRIL, 1))
  )
  val mails = maila.read(filter) //get a List of mails
  mails.foreach(m => println(m.contentText)) //print text content
}
