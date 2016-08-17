package com.github.cuzfrog.utils

/**
  * Override val loggerAgent:String for logger name display.
  * Created by cuz on 2016-08-17.
  */
trait SimpleLogger {
  implicit val loggerAgent = this.getClass.getSimpleName
  def debug(x: Any) = p(x, Debug)
  def warn(x: Any) = p(x, Warn, ANSI_YELLOW)
  def error(x: Any) = p(x, Error, ANSI_RED)

  private def p(x: Any, level: Level, color: String = "")(implicit agent: String) = println(s"[$agent]$color[$level]$x$ANSI_RESET")

  private sealed trait Level
  private case object Debug extends Level
  private case object Warn extends Level
  private case object Error extends Level

  private final val ANSI_RESET = "\u001B[0m"
  private final val ANSI_BLACK = "\u001B[30m"
  private final val ANSI_RED = "\u001B[31m"
  private final val ANSI_GREEN = "\u001B[32m"
  private final val ANSI_YELLOW = "\u001B[33m"
  private final val ANSI_BLUE = "\u001B[34m"
  private final val ANSI_PURPLE = "\u001B[35m"
  private final val ANSI_CYAN = "\u001B[36m"
  private final val ANSI_WHITE = "\u001B[37m"
}
