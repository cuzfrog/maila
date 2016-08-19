package com.github.cuzfrog

import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicReference

/**
  * Created by cuz on 2016-08-16.
  */
object TempTest extends App {

  sealed trait A
  case class A1() extends A{
    println("A1 is initialized.")
  }
  case object A2 extends A

  val ref= new AtomicReference[A]()

  ref.lazySet(new A1)
  ref.set(A2)

  println(ref.get())
}
