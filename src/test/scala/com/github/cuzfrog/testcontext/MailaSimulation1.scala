package com.github.cuzfrog.testcontext

import com.github.cuzfrog.maila.MailFilter
import com.github.cuzfrog.maila.Maila
import com.typesafe.scalalogging.LazyLogging

object Simulation1 extends App with LazyLogging {
  val maila = Maila.newInstance("""D:\MailaTest\testConfig.xml""")

  val filter = new MailFilter(subjectFilter = (subject: String) => subject.contains("堡垒上传成功"))
  val mails = maila.read(filter)
  logger.info(mails.size.toString)
  val PatternBass = """您好，您下载的【智能查询】名称【.*】导出成功，路径【(.*)】，解压密码【(\d{6})】""".r
  val PatternSSa = """您好，文件【.*】下载成功，堡垒路径【(.*)】，解压密码【(\d{6})】""".r

  val out = mails.map {
    mail =>
      mail.contentText match {
        case PatternBass(path, pw) => (path, pw)
        case PatternSSa(path, pw) => (path, pw)
        case s => throw new IllegalArgumentException("Bad content:" + s)
      }
  }
  
  
  
  

  out.foreach(o => logger.info(o.toString))
}