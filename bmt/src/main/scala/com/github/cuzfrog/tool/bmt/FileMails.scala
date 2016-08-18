package com.github.cuzfrog.tool.bmt

import java.io.File
import java.nio.charset.Charset

import com.typesafe.config.Config
import com.github.cuzfrog.maila.Mail

/**
  * This class takes care of reading and parsing file that contains mails infomation.
  *
  * Created by cuz on 2016-08-09.
  */
private[bmt] class FileMails(config: Config, mailsPath: String) {

  private val encoding: String = config.getString("encoding").toLowerCase match {
    case "default" => Charset.defaultCharset.displayName
    case c => c
  }
  private val toHead = config.getString("head.to")
  private val toSubject = config.getString("head.subject")
  private val toText = config.getString("head.text")
  private val delimiter = config.getString("delimiter")


  private val bufferedSource = io.Source.fromFile(new File(mailsPath))(encoding)
  private val allRaw = try {
    bufferedSource.getLines.toList
  } finally {
    bufferedSource.close
  }
  private val RegexString = """"(.*)"""".r
  //(?=([^"]*"[^"]*")*[^"]*$) does not escape quote"
  private val all = allRaw.map(_.split(s"""$delimiter(?=(([^"]|(\\"))*"([^"]|(\\"))*")*([^"]|(\\"))*$$)""", -1).map {
    case RegexString(s) => s
    case os => os.trim
  })
  private val heads = all.head
  //println(s"headers: ${heads.mkString("|")}")
  private val mailList = all.tail.map { m => (heads zip m).toMap }
  val mails = mailList.map { m =>
    Mail(List(m(toHead)), m(toSubject), StringContext.treatEscapes(m(toText)))
  }
}
