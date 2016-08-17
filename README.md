[![wercker status](https://app.wercker.com/status/c35467530ee75ff7ceb3a82f252886e6/s/master "wercker status")](https://app.wercker.com/project/bykey/c35467530ee75ff7ceb3a82f252886e6)
[ ![Download](https://api.bintray.com/packages/cuzfrog/maven/maila/images/download.svg) ](https://bintray.com/cuzfrog/maven/maila/_latestVersion)
# Maila

* Scala library wrapping javax.mail for reading and sending emails with simple APIs.
    * Utilizing Typesafe config, within which javax.mail properties are directly set.
    * Support parallel sending.
    * Multiple password providing strategies.
    * Current support sending text mail and reading mime(through parse mime object).
* Includes **BatchMailTool**, a simple cmd tool for sending batch mails.(See below)

Built against scala 2.11, dependencies: [build.sbt](build.sbt)

###Usage:

Resolver:

    resolvers += "bintray-cuzfrog-maven" at "http://dl.bintray.com/cuzfrog/maven"

Artifact:

    libraryDependencies += "com.github.cuzfrog" %% "maila" % "lastest-version"

#####provide an application.conf or whatever config file that conforms to Typesafe Config.
Necessary config is list below:

    maila {
      server {
        mail.pop3s.host = "some host"
        mail.smtps.host = "some host"
      }
      #not really necessary, see below: password providing strategies.
      authentication {
        #user = Cause
        #password = "some crypt"
      }
    }

Default with documentation [reference.conf](src/main/resources/reference.conf)

Typical and for-testing [application.conf](src/test/resources/application.conf)

_Different config source can be used, see [Typesafe config](https://github.com/typesafehub/config)_

#####sending mail:

```scala
import com.github.cuzfrog.maila.{Mail, Maila}
val maila = Maila.newInstance(user = "user0@some.com" ,password = "pw")
val mail = Mail(List("recipient@google.com"), "subject", "text content")
maila.send(List(mail))
maila.send(List(mail),isParallel =  true) //sending every mail in Future.
```

#####reading mail:

```scala
import com.github.cuzfrog.maila.{MailFilter, Maila}

val maila = Maila.newInstance(user = "user0@some.com" ,password = "pw")
val mails1 = maila.read() //get a List of mails using default filter.

val filter = MailFilter(
    maxSearchAmount = 30,
    filter = _.subject.contains("myKeyWord")
)
val mails2 = maila.read(filter) //get a List of mails
mails2.foreach(m => println(m.contentText)) //print text content
```

#####password providing strategies:

 * Supply user and password directly:

       Maila.newInstance(user = "user0@some.com" ,password = "pw")

 * Plain text in config file(forbidden by default):

   _Set `allow-none-encryption-password = true` in config._

       //System.setProperty("config.resource", "imap.conf") //if necessary.
       //Everytime an instance created, property cache is invalidated.
       Maila.newInstance() //if cannot find password in config, fails immediately.

 * Encrypted password in config file:

   Encryption uses AES method. You need to provide a finite seq of 128/192/256bit keys.
   Password string is in form of Base64.
   You can use Batch mail tool described below to generate key and encrypt password.

       val AESkey = "JYFi0VFzoUNZxLyj".getBytes("utf8")
       Maila.newInstance(AESkey) //try to decrypt password in config with the AES key.

 * Call-by-name mode, ask password when running.

       val console = System.console()
       def askPassword = console.readPassword().mkString
       Maila.newInstance(askPassword)

---

##Batch mail tool

This project includes a simple cmd tool for sending batch mails. Acquire binary from:
* Release: [Download](https://github.com/cuzfrog/maila/releases)
* Build: run sbt `>batchMailTool/assembly`, which, in addition, will generate the windows bat file pointing to the current version.

###How to use:

1. add java to PATH.
2. alter provided config file.
3. `>bmt -help`  you can have all instructions.

#####Use a csv file to define mails, and send:

    >bmt send -m:./mails.csv

  extra args:
  * `-pw:` if emitted, it will prompt and ask user to type in.
  * `-key:` if specified, `-pw` will be ignored, bmt tries to decrypt pw from config file.

mails.csv can be like this:(head line cannot be emitted.)

| to              | subject       | text  |
| --------------- |:-------------:| -----:|
| mike@google.com | Greet         | tt1   |
| john@google.com | Hello         | tt2   |
| etc             | etc           | etc   |

_*Only provided bmt.bat for windows_

#####Password tool:

    >bmt encrypt -pw:myPassword

  will print encrypted password with randomly generated key. Use`-help` to see more.

#####Debug mode:
In config file:

    maila {
        #When set to true, exception stacktrace will be printed.
        debug = false
    }