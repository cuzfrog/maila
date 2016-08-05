package com.github.cuzfrog.testcontext

import com.github.cuzfrog.maila.Maila

/**
  * Created by cuz on 2016-08-05.
  */
object SendTest extends App {
  val maila = Maila.newInstance("D:\\MailaTest\\config1.xml")
  maila.send(List("cuzfrog@139.com"), "test1", "test111")
}
