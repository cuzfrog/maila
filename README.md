[![wercker status](https://app.wercker.com/status/c35467530ee75ff7ceb3a82f252886e6/s/master "wercker status")](https://app.wercker.com/project/bykey/c35467530ee75ff7ceb3a82f252886e6)
[ ![Download](https://api.bintray.com/packages/cuzfrog/maven/maila/images/download.svg) ](https://bintray.com/cuzfrog/maven/maila/_latestVersion)
# Maila

* Scala library wrapping javax.mail for reading and sending emails with simple APIs.
    * Utilizing Typesafe config, with which javax.mail properties are directly set.
    * Fully immutable instances, possibly being used in parallel.
    * Multiple password providing strategies.
* Includes **BatchMailTool**, a simple cmd tool for sending batch mails.(See below)

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
      authentication {
        user = Mike
        #password = "some crypt"
      }
    }

Default config and documentation is at [reference.conf](src/main/resources/reference.conf)

#####sending mail:

```scala
val maila = Maila.newInstance("D:\\MailaTest\\application.conf", key = "w0j9j3pc1lht5c6b")
val mail = Mail(List("recipient@google.com"), "subject", "text content")
maila.send(List(mail))
```

#####reading mail:

```scala
val filter = MailFilter(
    maxSearchAmount = 30,
    subjectFilter = _.contains("myKeyWord"),
    receiveDateFilter = _.isAfter(new LocalDate(2016, Month.APRIL, 1))
)
val mails = maila.read(filter) //get a List of mails
mails.foreach(m => println(m.contentText)) //print text content
```

#####password storing strategies:

 * Plain text in config file(forbidden by default):

   _Set `allow-none-encryption-password = true` in config._

       Maila.newInstance(configPath) //if cannot find password in config, fails immediately.

 * Encrypted password in config file:

   Encryption uses AES method. You need to provide a finite seq of 128/192/256bit keys.
   You can use Batch mail tool described below to generate key and encrypt password.

       Maila.newInstance(configPath,AESkey) //try to decrypt password in config with the AES key.

 * Call-by-name mode, ask password when running.
    ```scala
    val console = System.console()
    def askPassword = console.readPassword().mkString
    Maila.newInstance(configPath,askPassword)
    ```
---

##Batch mail tool

This project includes a simple cmd tool for sending batch mails. [Download](https://github.com/cuzfrog/maila/releases)

###How to use:

1. add java to PATH.
2. alter provided config file.
3. `bmt -help`  you can have all instructions. Examples:

#####Use a csv file to define mails, and send:

    bmt send -mailsPath:./mails.csv

mails.csv can be like this:(head line cannot be emitted.)

| to              | subject       | text  |
| --------------- |:-------------:| -----:|
| mike@google.com | Greet         | tt1   |
| john@google.com | Hello         | tt2   |
| etc             | etc           | etc   |

_*Only provided bmt.bat for windows_

#####Password tool:

    bmt encrypt -pw:myPassword

  will print encrypted password with randomly generated key. Use`-help` to see more.