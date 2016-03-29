package com.github.cuzfrog.testcontext

import com.github.cuzfrog.maila.MailFilter
import com.github.cuzfrog.maila.Maila
import com.typesafe.scalalogging.LazyLogging
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Date

object Simulation1 extends App with LazyLogging {
  val maila = Maila.newInstance("""D:\MailaTest\testConfig.xml""")
  var hoursRegress=1
  private val filter = {
    val calender = Calendar.getInstance()

    val dateFilter = hoursRegress match {
      case n if (n > 0) =>
        calender.add(Calendar.HOUR_OF_DAY, -(hoursRegress))
        (date: Date) => 
          calender.getTime().before(date)
        case _ =>
        val d = calender.getTime()
        val formatter = new SimpleDateFormat("dd/mm/yyyy");
        (date: Date) => formatter.format(date) == formatter.format(d)
    }

    new MailFilter(subjectFilter = (subject: String) => subject.contains("堡垒上传成功"),
      receiveDateFilter = dateFilter)
  }
  val mails = maila.read(filter)
  logger.info(mails.size.toString)
  val PatternBass = """(?s)您好，您下载的【智能查询】名称【.*】导出成功，路径【(.*)】，解压密码【(\d{6})】.*""".r
  val PatternSSa = """(?s)您好，文件【.*】下载成功，堡垒路径【(.*)】，解压密码【(\d{6})】.*""".r
  //val TestP="""您好，文件【test1.zip】下载成功，堡垒路径【/test1.zip】，解压密码【123456】(.*)""".r
  
  val out = mails.map {
    mail =>
      mail.contentText match {
        case PatternBass(path, pw) => (path, pw)
        case PatternSSa(path, pw) => (path, pw)
        //case TestP(s) => println("catch"+s)
        case s => throw new IllegalArgumentException("Bad content:" + s)
      }
  }
  
  
  
  

  out.foreach(o => logger.info(o.toString))
}