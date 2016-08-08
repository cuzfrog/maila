package com.github.cuzfrog.tool

import java.io.File

import com.github.cuzfrog.maila.{Mail, Maila}

/**
  * Created by cuz on 2016-08-08.
  */
object BatchMailTool extends App {

  val _args: Seq[String] = if (args.isEmpty) List("-help") else args

  private val mailsPath = _args.find(_.startsWith("-mailsPath:")) match {
    case Some(pathArg) => pathArg.split(":").last
    case None => "Must specify -mailsPath: argument."
  }

  private val configPath = _args.find(_.startsWith("-configPath:")) match {
    case Some(pathArg) => pathArg.split(":").last
    case None => "./"
  }

  private val version: String = getClass.getPackage.getImplementationVersion

  _args.head.toLowerCase match {
    case "send" =>
      try {
        p("sending...")
        maila.send(mails)
        p(s"${mails.size} mails sent.")
      }
      catch {
        case e: Exception => p(s"error with msg:${e.getMessage}")
      }
    case "-help" =>
      p("a simple cmd tool for sending batch text emails.")
      println("Use a csv file to define emails and a config.xml file to define mail server and authentication info.")
      println("----------------------")
      println("args            (* means indispensable)explanations")
      println("-mailsPath:     *the path of csv file that contains mail contents.")
      println("-configPath:     the path of config.xml file that contains maila configuration. default: ./")
      println("-encoding:       mail content encoding. default:GBK")
      println("-toHead:         the csv first line(head)'s field name for mail To. default: to")
      println("-subjectHead:    the csv first line(head)'s field name for mail Subject. default: subject")
      println("-textHead:       the csv first line(head)'s field name for mail Text content. default: text")
      println("----------------------")
      println("commands         explanations")
      println("send:            run this application, must provide arguments.")
      println("-version:        show version.")
      println("-help:           print this help.")
    case "-version" => p(version)
    case _ => "Bad arguments, use -help see instructions."
  }

  def p(s: Any) = println(s"Batch mail tool: $s")

  private lazy val maila = Maila.newInstance(configPath, willObfuscate = true, TRANSFORM_KEYS.map(_.getBytes("utf8")))

  private lazy val mails = {
    val encoding = _args.find(_.startsWith("-encoding:")) match {
      case Some(pathArg) => pathArg.split(":").last
      case None => "GBK"
    }
    val bufferedSource = io.Source.fromFile(new File(mailsPath))(encoding)
    val allRaw = try {
      bufferedSource.getLines
    } finally {
      bufferedSource.close
    }
    val all = allRaw.map(_.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1).map(_.trim)).toList
    val heads = all.head

    val mailList = all.tail.map { m => (heads zip m).toMap }
    mailList.map { m =>
      Mail(List(m("to")), m("subject"), m("text"))
    }
  }

  private final val TRANSFORM_KEYS =
    List("w0j9j3pc1lht5c6b",
      "pelila8h8xyduk8u",
      "pqzlv3646t5czf43",
      "rlea96gwkutwhz4m",
      "7v3txdd4hcv0e1jd",
      "v6k98fmyags5ugfi",
      "uae6c909uc031a3l",
      "5rtsom1rerkdqg6s",
      "20o06zwhrv5uqflt",
      "104e8spzwv5c2s32")
}
