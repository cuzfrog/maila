package com.github.cuzfrog.tool.bmt

import java.io.File

import com.github.cuzfrog.maila.Mail

/**
  * Created by cuz on 2016-08-09.
  */
private[bmt] class Mails(_args: Seq[String], mailsPath: String) {

  private val encoding = _args.find(_.startsWith("-encoding:")) match {
    case Some(pathArg) => pathArg.split(":").last
    case None => "UTF8"
  }
  private val bufferedSource = io.Source.fromFile(new File(mailsPath))(encoding)
  private val allRaw = try {
    bufferedSource.getLines.toList
  } finally {
    bufferedSource.close
  }
  private val RegexString = """"(.)*"""".r
  private val all = allRaw.map(_.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1).map {
    case RegexString(s) => s
    case os => os.trim
  })
  private val heads = all.head
  println(heads.mkString("|"))
  private val mailList = all.tail.map { m => (heads zip m).toMap }
  val mails = mailList.map { m =>
    Mail(List(m("to")), m("subject"), m("text"))
  }
}
