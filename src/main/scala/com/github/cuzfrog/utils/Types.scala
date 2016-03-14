package com.github.cuzfrog.utils

import scala.reflect.runtime.universe._
import com.typesafe.scalalogging.LazyLogging

object Types {
  implicit class ExType[U: TypeTag](that: U) {
    /**
     * Use TypeTag to deal with Erasure.
     */
    def iisInstanceOf[T: TypeTag] = typeOf[U] weak_<:< typeOf[T]
  }

  trait Enum{
    protected final def Value(name: String): Element = ScalaEnum.NewValue(name)
    final def withName(s: String): Element = ScalaEnum.withName(s).asInstanceOf[Element]
    
    private[Enum] object ScalaEnum extends Enumeration {
      private[Enum] class EnumValue(name: String)  extends Val(nextId, name)
      private[Enum] final def NewValue(name: String): Element = new Element(name)
    }
    class Element(name: String) extends ScalaEnum.EnumValue(name)
  }
}