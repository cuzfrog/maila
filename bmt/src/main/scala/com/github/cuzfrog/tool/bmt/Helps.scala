package com.github.cuzfrog.tool.bmt

/**
  * Created by cuz on 2016-08-11.
  */
private[bmt] object Helps {
  def print(): Unit = {
    println("Use a csv file to define emails and a config file to define mail server and authentication info.")
    println("----------------------")
    println("args              explanations(* means indispensable)")
    println("-mailsPath|-m:    *the path of csv file that contains mail contents.")
    println("-configPath:      the path of config.xml file that contains maila configuration. default: ./application.conf")
    println("-pw:              the password for mail authentication.")
    println("-key:             the key to decrypt password in config.")
    println("----------------------")
    println("commands          explanations")
    println("send              run this application. ")
    println("     example:")
    println("     >bmt send -mailsPath:./mails.csv            this will ask password later.")
    println("     >bmt send -mailsPath:./mails.csv -pw:myPass")
    println("     >bmt send -mailsPath:./mails.csv -key:someKeyString")
    println("test              run this application to check mails, not sending.")
    println("-version          show version.")
    println("-help             print this help.")
    println("----------------------")
    println("tools             explanations")
    println("randomKey         generate and print a random 16-byte key in form of String.")
    println("encrypt           encrypt password with (random) key and print it and its key. ")
    println("     example:")
    println("     >bmt encrypt -pw:myPassword -key:w0j9j3pc1lht5c6b")
    println("     >bmt encrypt -pw:myPassword                 this will use a random key.")
    println("Goto http://github.com/cuzfrog/maila see more.")
  }
}
