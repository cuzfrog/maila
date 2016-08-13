package com.github.cuzfrog.maila

import java.time.LocalDate
import javax.mail.Message

case class MailFilter(
                       maxSearchAmount: Int = 30,
                       subjectFilter: (String => Boolean) = String => true,
                       receiveDateFilter: (LocalDate => Boolean) = LocalDate => true,
                       javaMessageFilter: (Message => Boolean) = Message => true)