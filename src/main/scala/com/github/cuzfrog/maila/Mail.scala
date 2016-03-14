package com.github.cuzfrog.maila

import javax.mail.Message

trait Mail {
  
}

object Mail{
  
  
  def apply(message:Message):Mail={
    new JmMail(message)
  }
  
  private class JmMail(message:Message) extends Mail
}