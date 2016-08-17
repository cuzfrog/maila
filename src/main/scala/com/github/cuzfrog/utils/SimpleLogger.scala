package com.github.cuzfrog.utils

/**
  * Created by cuz on 2016-08-17.
  */
private[cuzfrog] trait SimpleLogger {
  def debug(x: Any) = println(s"${this.getClass.getSimpleName}-Debug:$x")
}
