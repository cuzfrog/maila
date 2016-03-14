package com.github.cuzfrog.maila

private[maila] trait Account {
  val user: String
  val password: String
}

private[maila] object Account {
  def apply(user: String, password: String): Account = {
    new SimpleAccout(user,password)
  }
  
  def apply(config:Configuration):Account={
    new SimpleAccout(config.user,config.password)
  }

  private class SimpleAccout(val user: String,val password: String) extends Account
}