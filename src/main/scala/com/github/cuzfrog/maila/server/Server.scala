package com.github.cuzfrog.maila.server

import javax.mail.Session

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
    val properties = config.serverProps
    lazy val session = Session.getDefaultInstance(properties)
    session.setDebug(false)
    lazy val store = session.getStore("pop3s")
    lazy val transport = session.getTransport("smtps")

    def reader(account: Account) = {
      store.connect(account.user, account.password)
      Reader(store)
    }

    def sender(account: Account) = {
      transport.connect(account.user, account.password)
      Sender(session, transport, account.user)
    }
  }

}