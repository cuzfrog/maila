package com.github.cuzfrog.maila

import javax.mail.Session
import java.util.Properties

private[maila] trait Server {
  def reader(account:Account): Reader
}

private[maila] object Server {
  def apply(config: Configuration): Server = {
    new JmServer(config)
  }

  private class JmServer(config: Configuration) extends Server {
    val properties = new Properties();
    properties.put("mail.pop3.host", config.hostPop3);
    properties.put("mail.pop3.port", "995");
    properties.put("mail.pop3.starttls.enable", "true");
    val emailSession = Session.getDefaultInstance(properties);

    //create the POP3 store object and connect with the pop server
    val store = emailSession.getStore("pop3s");
    
    def reader(account:Account)={
      Reader(store,config.hostPop3,account.user,account.password)
    }
  }
}