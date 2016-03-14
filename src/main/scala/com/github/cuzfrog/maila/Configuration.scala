package com.github.cuzfrog.maila

import scala.xml.XML
import scala.xml.Elem

private[maila] trait Configuration {
  def hostPop3: String
  def user: String
  def password: String
}

private[maila] object Configuration {
  def fromFile(path: String): Configuration = {
    val xml = XML.loadFile(path)
    new XmlConfiguration(xml)
  }
  
  def obfuscate(path: String): Unit = {

  }

  private class XmlConfiguration(xml: Elem) extends Configuration {
    val hostPop3 = (xml \ "host").head.attribute("pop3").get.head.text
    val user=(xml \ "user").head.text
    val password=(xml \ "password").head.text
  }
}
