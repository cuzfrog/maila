package com.github.cuzfrog

import java.io.{File, PrintWriter}
import java.nio.file.Files

import scala.io.Source
import scala.util.Random

/**
  * Created by cuz on 2016-08-16.
  */
object TempTest extends App {

  val f ="""    libraryDependencies += "com.github.cuzfrog" %% "maila" % "lastest-version""""
  val regex ="""(?<=libraryDependencies \+= "com\.github\.cuzfrog" %% "maila" % ")[\d\w\-\.]+(?=")"""
  val r = f.replaceAll(regex, "0.2.2").replaceAll(regex, "asdf")

  updateFile(new File("README.MD"), regex, "0.2.2")

  def updateFile(file: File, regex: String, value: String): Unit = {
    val temp = new File(s"${System.getenv("TEMP")}/README.MD_${Random.alphanumeric.take(8).mkString}.txt") // Temporary File
    val w = new PrintWriter(temp)
    Source.fromFile(file).getLines
      .map { line => line.replaceAll(regex, value) }
      .foreach(x => w.println(x))
    w.close()
    Files.delete(file.toPath)
    temp.renameTo(file)
  }
}
