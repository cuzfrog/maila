package com.github.cuzfrog.tool.bmt

import com.github.cuzfrog.maila.Maila

/**
  * Created by cuz on 2016-08-08.
  */
private[bmt] object BatchMailTool extends App {

  //todo: add direct mail sending function


  private val _args: Seq[String] = if (args.isEmpty) List("-help") else args

  private val mailsPath = _args.find(_.startsWith("-mailsPath:")) match {
    case Some(pathArg) => pathArg.split(":").last
    case None => "Must specify -mailsPath: argument."
  }

  private val configPath = _args.find(_.startsWith("-configPath:")) match {
    case Some(pathArg) => pathArg.split(":").last
    case None => "./config.xml"
  }

  private val version: String = getClass.getPackage.getImplementationVersion

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
          e.printStackTrace()
          p(s"error with msg:${e.getMessage}")
      }
    case "-help" =>
      p(s"v$version - a simple cmd tool for sending batch text emails.")
      println("Use a csv file to define emails and a config.xml file to define mail server and authentication info.")
      println("----------------------")
      println("args              explanations(* means indispensable)")
      println("-mailsPath:      *the path of csv file that contains mail contents.")
      println("-configPath:      the path of config.xml file that contains maila configuration. default: ./")
      println("-obfuscateConfig: if true, config file will be obfuscated after first mail sending. default: true")
      println("-encoding:        mail content encoding. default:UTF8")
      println("-toHead:          the csv first line(head)'s field name for mail To. default: to")
      println("-subjectHead:     the csv first line(head)'s field name for mail Subject. default: subject")
      println("-textHead:        the csv first line(head)'s field name for mail Text content. default: text")
      println("----------------------")
      println("commands          explanations")
      println("send              run this application, must provide arguments.")
      println("-version          show version.")
      println("-help             print this help.")
    case "-version" => p(version)
    case _ => p("Bad arguments, use -help see instructions.")
  }

  private def p(s: Any) = println(s"Batch mail tool: $s")

  private lazy val willObfuscate = _args.find(_.startsWith("-obfuscateConfig:")) match {
    case Some(pathArg) => pathArg.split(":").last match {
      case "true" => true
      case "false" => false
    }
    case None => true
  }

  private lazy val maila = Maila.newInstance(configPath, willObfuscate, DefaultKeys.TRANSFORM_KEYS.map(_.getBytes("utf8")))

  private def mails = new Mails(_args, mailsPath).mails

}
