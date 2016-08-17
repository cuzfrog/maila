package com.github.cuzfrog.utils

/**
  * Created by cuz on 2016-08-17.
  */
private[cuzfrog] trait SimpleLogger {
  protected implicit val loggerAgent = this.getClass.getSimpleName
  def debug(x: Any, id: String = this.getClass.getSimpleName) = p(s"[Debug]:$x")
  def warn(x: Any, id: String = this.getClass.getSimpleName) = p(s"[Warn]:$x", ANSI_YELLOW)

  private def p(x: Any, color: String = "")(implicit agent: String) = println(s"$color[$agent]-$x$ANSI_RESET")

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
