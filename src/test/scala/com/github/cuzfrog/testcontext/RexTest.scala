package com.github.cuzfrog.testcontext

object RexTest extends App {
  val s1="""from hw_web7 (unknown[10.182.17.117])
	by rmsmtp-syy-appsvr09-12009 (RichMail) with SMTP id 2ee9568dd3cbe88-c17e3;
	Thu, 07 Jan 2016 10:56:11 +0800 (CST)"""
  
  val P1="""(?s).*([\w]{3},\s[\d]{2}\s[\w]{3}\s[\d]{4}\s[.]{8}\s+[\d]{4}).*""".r
  val P2="""(?s).*([\w]{3},\s[\d]{2}\s[\w]{3}\s[\d]{4}\s[\d:]{8}\s\+[\d]{4}).*""".r
  s1 match{
    case P2(s)=> println(s)
    case _ => println("eluded")
  }
}