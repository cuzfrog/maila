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

  private val javaMailDebug = Configuration.config.getBoolean("server.javax.mail.debug")

  private class JmServer(config: Configuration) extends Server {
    val properties = config.serverProps
    lazy val session = Session.getInstance(properties)
    session.setDebug(javaMailDebug)
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