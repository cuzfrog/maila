package com.github.cuzfrog.maila.server

import javax.mail.Session

import com.github.cuzfrog.maila.Configuration

private[maila] trait Server {
  def reader: Reader

  def sender: Sender
}

private[maila] object Server {
  def apply(config: Configuration): Server = {
    new JmServer(config)
  }

  private class JmServer(config: Configuration) extends Server {
    val properties = config.serverProps
    lazy val session = Session.getInstance(properties)
    session.setDebug(config.config.getBoolean("server.javax.mail.debug"))
    lazy val store = session.getStore(config.storeType)
    lazy val transport = session.getTransport(config.transportType)

    def reader = {
      store.connect(config.user, config.password)
      Reader(store)
    }

    def sender = {
      transport.connect(config.user, config.password)
      Sender(session, transport, config.user)
    }
  }

}