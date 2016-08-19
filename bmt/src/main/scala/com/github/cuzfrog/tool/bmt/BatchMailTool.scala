package com.github.cuzfrog.tool.bmt

import java.io.File

import com.github.cuzfrog.maila.Maila
import com.github.cuzfrog.utils.SimpleLogger
import com.sun.media.sound.InvalidFormatException
import com.typesafe.config.ConfigFactory

/**
  * Created by cuz on 2016-08-08.
  */
private[bmt] class BatchMailTool(args: Array[String]) extends SimpleLogger {

  override val loggerAgent = "BatchMailTool"

  private val _args: Seq[String] = if (args.isEmpty) List("-help") else args

  private val config = synchronized {
    val f = new File(configPath)
    if (f.exists() && f.isFile) {
      Maila.provideConfig(ConfigFactory.parseFile(f))
    }
    else {
      warn(s"config file not found with path:$configPath, fallback to default.")
      Maila.reloadConfig
    }
  }

  private lazy val version: String = getClass.getPackage.getImplementationVersion
  private lazy val mailsPath = argParse("-mailsPath|-m", errInfo = "Must specify -mailsPath: argument.")
  private lazy val configPath = argParse("-configPath|-c", "application.conf")
  private lazy val pw = argParse("-password|-p", "")
  private lazy val user = argParse("-user|-u", "")
  private lazy val console = System.console()

  private def _askPw: String = pw match {
    case "" =>
      val hasPath = config.hasPath("authentication.password")
      lazy val allowed = config.getBoolean("authentication.allow-none-encryption-password")
      if (hasPath && allowed) config.getString("authentication.password")
      else console.readPassword("Mail account password>").mkString
    case p => p
  }

  private def _askUser: String = user match {
    case "" =>
      if (config.hasPath("authentication.user")) config.getString("authentication.user")
      else console.readLine("Mail account/user>")
    case u => u
  }

  private lazy val key = argParse("-key", "")

  private lazy val mails = try {
    new FileMails(config.getConfig("bmt.file"), mailsPath).mails
  } catch {
    case e: NoSuchElementException =>
      throw new InvalidFormatException(s"File may be mal-formatted,err msg:${e.getMessage}")
  }
  private lazy val keys = new Keys(config.getString("authentication.password-encoding"))

  /*Key Logic:
    1.If user has specified a key, try to decrypt pw in config. But when pw is not present? throw an error.
    2.If user has not given a key, pass lazy ask-pw to maila.
      When needed try to find in config, if fails, prompt to ask user.
  */
  private lazy val maila = key match {
    case "" => Maila.newInstance(askUser = _askUser, askPassword = _askPw)
    case k => Maila.newInstance(key.getBytes("utf8"))
  }

  import BatchMailTool.p

  /**
    *
    * @return null if succeeded, otherwise the Exception.
    */
  def run(): Option[Exception] = try {
    _args.head.toLowerCase match {
      case "send" =>
        p("sending...")
        val cnt = maila.send(mails).length
        p(s"${mails.size} mails: $cnt of which sent successfully.")
      case "test" => p(s"${mails.size} mails ready to send.")
      case "randomkey" => println(keys.randomKey)
      case "encrypt" =>
        val pw = argParse("-password|-p")
        val key = argParse("-key", "")
        println(keys.encrypt(pw, key))
      case "-help" =>
        p(s"v$version - a simple cmd tool for sending batch text emails.")
        Helps.print()
      case "-version" => p(version)
      case _ => error("Bad arguments, use -help see instructions.")
    }
    None
  } catch {
    case e: Exception =>
      if (config.getBoolean("debug")) {
        //debug(maila.getConfig("").getBoolean("debug"))
        e.printStackTrace()
      }
      error(e.getMessage)
      Some(e)
  }

  private def argParse(prefixes: String, default: String = null, errInfo: String = "Bad or lack argument for:"): String = {
    _args find { arg =>
      prefixes.split("""\|""").map { prefix => arg.startsWith(prefix) }.foldLeft(false)(_ || _)
    } match {
      case Some(pathArg) => pathArg.split(":", 2).last
      case None => default match {
        case null => throw new IllegalArgumentException(errInfo + prefixes)
        case _ => default
      }
    }
  }
}

private[bmt] object BatchMailTool {
  def p(s: Any) = println(s"Batch mail tool: $s")
}