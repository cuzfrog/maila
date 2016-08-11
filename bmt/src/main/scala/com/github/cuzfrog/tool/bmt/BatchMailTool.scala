package com.github.cuzfrog.tool.bmt

import com.github.cuzfrog.maila.Maila

/**
  * Created by cuz on 2016-08-08.
  */
private[bmt] object BatchMailTool extends App {

  //todo: add direct mail sending function

  //todo: add simulation

  private val _args: Seq[String] = if (args.isEmpty) List("-help") else args

  private def argParse(prefix: String, default: String = null, errInfo: String = "Bad or lack argument for:"): String = {
    _args.find(_.startsWith(prefix)) match {
      case Some(pathArg) => pathArg.split(":").last
      case None => default match {
        case null => throw new IllegalArgumentException(errInfo + prefix)
        case _ => default
      }
    }
  }

  private val version: String = getClass.getPackage.getImplementationVersion
  private val mailsPath = argParse("-mailsPath:", errInfo = "Must specify -mailsPath: argument.")
  private val configPath = argParse("-configPath:", "./application.conf")
  private lazy val pw = argParse("-pw", "")
  private lazy val console = System.console()

  private def askPw: String = if (pw.isEmpty) {
    print("Password>")
    console.readPassword().mkString
  } else pw

  private lazy val key = argParse("-key", "")
  /* Logic:
  1.If user has specified a key, try to decrypt pw in config. But when pw is not present? throw an error.
  2.If user has not given a key, pass lazy ask-pw to maila:
  - a.when "allow-none-encryption-password" is true, try to use pw in config,
  -- (1).when pw is present in config, ask-pw will never be invoked.
  -- (2).when pw is not present, ask pw.
  - b.if false, ask pw directly when needed.
   */
  private val maila = key match {
    case "" => Maila.newInstance(configPath, askPw)
    case k => Maila.newInstance(configPath, key.getBytes("utf8"))
  }

  _args.head.toLowerCase match {
    case "send" =>
      try {
        //todo: add success count
        p("sending...")
        maila.send(mails)
        p(s"${mails.size} mails sent.")
      }
      catch {
        case e: Exception =>
          //e.printStackTrace()
          p(s"error with msg:${e.getMessage}")
      }
    case "randomKey" => println(Keys.randomKey)
    case "encrypt" =>
      val pw = argParse("-pw")
      val key = argParse("-key", "")
      println(Keys.encrypt(pw, key))
    case "-help" =>
      p(s"v$version - a simple cmd tool for sending batch text emails.")
      Helps.print()
    case "-version" => p(version)
    case _ => p("Bad arguments, use -help see instructions.")
  }

  def p(s: Any) = println(s"Batch mail tool: $s")

  private def mails = new CsvMails(maila.getConfig("bmt"), mailsPath).mails

}
