package com.github.cuzfrog.testcontext

object RexTest extends App {
  val s1="""您好，文件【test1.zip】下载成功，堡垒路径【/test1.zip】，解压密码【123456】 
    
    ataadsffdas"""
  
  val P1="""(?s)您好，文件【test1.zip】下载成功，堡垒路径【/test1.zip】，解压密码【123456】(.*)""".r
  s1 match{
    case P1(s)=> println(s)
    case _ => println("eluded")
  }
}