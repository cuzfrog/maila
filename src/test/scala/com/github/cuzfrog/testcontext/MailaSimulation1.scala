package com.github.cuzfrog.testcontext

import com.github.cuzfrog.maila.MailFilter
import com.github.cuzfrog.maila.Maila
import com.typesafe.scalalogging.LazyLogging
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Date

object MailaSimulation1 extends App with LazyLogging {
  final val TRANSFORM_KEY = List("w0j9j3pc1lht5c6b",
    "pelila8h8xyduk8u",
    "pqzlv3646t5czf43",
    "rlea96gwkutwhz4m",
    "7v3txdd4hcv0e1jd",
    "v6k98fmyags5ugfi",
    "uae6c909uc031a3l",
    "5rtsom1rerkdqg6s",
    "20o06zwhrv5uqflt",
    "104e8spzwv5c2s32")
  val maila = Maila.newInstance("""D:\MailaTest\testConfig.xml""", true, keys = TRANSFORM_KEY.map(_.getBytes("utf8")))
  var hoursRegress = 6
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