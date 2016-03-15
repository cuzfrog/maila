package com.github.cuzfrog.maila

import javax.mail.Message
import java.util.Date

trait Mail {
  def receiveDate:Date
  def subject:String
  def content:Object
  def contentType:String
}

object Mail{
  def apply(message:Message):Mail={
    new JmMail(message)
  }
  
  private class JmMail(message:Message) extends Mail{
    lazy val receiveDate=message.getReceivedDate
    lazy val subject=message.getSubject
    lazy val content=message.getContent
    lazy val contentType=message.getContentType
  }
}