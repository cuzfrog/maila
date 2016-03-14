package com.github.cuzfrog

import com.typesafe.scalalogging.LazyLogging
import org.slf4j.LoggerFactory
import com.typesafe.scalalogging.Logger
package object maila {
  val logger = Logger(LoggerFactory.getLogger(this.getClass.getName))
}