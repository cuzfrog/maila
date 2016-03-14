package com.github.cuzfrog.testcontext

import com.github.cuzfrog.maila.MailFilter
import com.github.cuzfrog.maila.Maila
import com.typesafe.scalalogging.LazyLogging

object Simulation1 extends App with LazyLogging  {
  val maila=Maila.newInstance("""D:\MailaTest\testConfig.xml""")
  val subjectFun=(subject:String)=>subject.contains("""堡垒上传成功""")

  val filter=new MailFilter(subjectFilter=subjectFun)
  val mails=maila.read(filter)
  
  val pattern1="""您好，您下载的【智能查询】名称【(.*)】导出成功，路径【/地市/桂林/桂林_经分/20160314110404_chengzhen_203267259_4G卡用户未开通4G功能日清单.zip】，解压密码【950798】"""
  val pattern2="""您好，文件【.*】下载成功，堡垒路径【/地市/桂林/桂林_经分/20160314103726_渠道接触2_chengzhen.zip】，解压密码【446609】"""
}