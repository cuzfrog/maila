package com.github.cuzfrog.maila.server

import java.util.Properties
import javax.mail.Message.RecipientType
import javax.mail.{Address, Session}
import javax.mail.internet.{InternetAddress, MimeMessage}

import com.github.cuzfrog.maila.{Account, Configuration}

private[maila] trait Server {
  def reader(account: Account): Reader
  def sender(account: Account): Sender
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
    lazy val session = Session.getDefaultInstance(properties);

    //create the POP3 store object and connect with the pop server
    lazy val store = session.getStore("pop3s");

    lazy val message = new MimeMessage(session);

    def reader(account: Account) = {
      store.connect(config.hostPop3, account.user, account.password)
      Reader(store)
    }

    def sender(account: Account) = {
      //todo: add authentication
      message.setFrom(new InternetAddress(account.user))
      Sender(message)
    }
  }
}