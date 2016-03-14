package com.github.cuzfrog.maila

import java.util.Date

class MailFilter(
  val maxSearchAmount: Int = 30,
  val subjectFilter: (String => Boolean) = (String => true),
  val receiveDateFilter: (Date => Boolean) = (Date => true))