package com.github.cuzfrog.utils

/**
  * Override val loggerAgent:String for logger name display.
  * Created by cuz on 2016-08-17.
  */
trait SimpleLogger {

  import Console._
  import SimpleLogger._

  implicit val loggerAgent = this.getClass.getSimpleName
  def debug(x: Any) = p(x, Debug)
  def info(x:Any) = p(x, Info)
  def warn(x: Any) = p(x, Warn, YELLOW)
  def error(x: Any) = p(x, Error, RED)

  private def p(x: Any, level: Level, color: String = "")(implicit agent: String) = println(s"[$agent]$color[$level]$x$RESET")
}

object SimpleLogger {
  private sealed trait Level
  private case object Info extends Level
  private case object Debug extends Level
  private case object Warn extends Level
  private case object Error extends Level
}
