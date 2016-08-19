package com.github.cuzfrog.maila

/**
  * Created by Cause Frog on 8/14/2016.
  */
object ConfigOverrideTest extends App{
  val maila = Maila.newInstance(askPassword = "somePW")
  println(Maila.currentConfig.getConfig("server").entrySet())
}
