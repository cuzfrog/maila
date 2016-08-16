package com.github.cuzfrog.testcontext

/**
  * Created by cuz on 2016-08-16.
  */
object TempTest extends App {
  val o = "zzzSSpp"
  println(o.scanLeft("")((l, r) => if (!(l contains r)) l + " " + r else l + r).last)
}
