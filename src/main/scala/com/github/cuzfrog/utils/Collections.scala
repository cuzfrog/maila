package com.github.cuzfrog.utils

import scala.reflect.runtime.universe._

object Collections {
  implicit class ExMap[A, B](val in: Map[A, B]) {
    /**
     * Ex-version of valueMap, which return a new map instead of an evil view.
     */
    def valueMap[C](f: B => C): Map[A, C] = {
      in.map { case (k, v) => (k, f(v)) }
    }

    /**
     * Ex-version of filterKeys, which return a new map instead of an evil view.
     */
    def keyFilter(f: A => Boolean): Map[A, B] = {
      in.filter(e => f(e._1))
    }

    /**
     * Filter a map by values.
     */
    def valueFilter(f: B => Boolean): Map[A, B] = {
      in.filter(e => f(e._2))
    }
  }

  implicit class TableT4[A, B, C, D](in: Seq[(A, B, C, D)]) {
    def toTree: Map[A, Map[B, Map[C, D]]] = {
      val out = in.view.groupBy(_._1).valueMap {
        b =>
          b.groupBy(_._2).valueMap {
            c => c.groupBy(_._3).valueMap(_.head._4)
          }
      }
      out.view.force
    }
  }

  implicit class TableT3[A, B, C](in: Seq[(A, B, C)]) {
    def toTree: Map[A, Map[B, C]] = {
      val out = in.view.groupBy(_._1).valueMap {
        b => b.groupBy(_._2).valueMap(_.head._3)
      }
      out.view.force
    }
  }
}