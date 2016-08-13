package com.github.cuzfrog.maila

import java.time.LocalDate

//todo:redesign to be more general.

case class MailFilter(
                  maxSearchAmount: Int = 30,
                  subjectFilter: (String => Boolean) = String => true,
                  receiveDateFilter: (LocalDate => Boolean) = LocalDate => true)