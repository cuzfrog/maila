[![wercker status](https://app.wercker.com/status/904c2e592f536e3a298bb1bbf18c398f/s/master "wercker status")](https://app.wercker.com/project/bykey/904c2e592f536e3a298bb1bbf18c398f)
[ ![Download](https://api.bintray.com/packages/cuzfrog/maven/maila/images/download.svg) ](https://bintray.com/cuzfrog/maven/maila/_latestVersion)
# Maila

* Scala library wrapping javax.mail for reading and sending emails with simple APIs.
* Includes **BatchMailTool**, a simple cmd tool for sending batch mails.(See below)

###Usage:

1. provide a config.xml: (it will migrate to typesafe config)

        <config>
        <host pop3="some ip or url" smtp="some ip or url"></host>
        <user>yourname@google.com</user>
        <password>yourmailpassword</password>
        </config>

2. sending mail:

    ```scala
      val maila = Maila.newInstance("D:\\MailaTest\\config.xml")
      val mail = Mail(List("recipient@google.com"), "subject", "text content")
      maila.send(List(mail))
    ```

3. reading mail:
    ```scala
      val filter = MailFilter(
        maxSearchAmount = 30,
        subjectFilter = _.contains("myKeyWord"),
        receiveDateFilter = _.isAfter(new LocalDate(2016, Month.APRIL, 1))
      )
      val mails = maila.read(filter) //get a List of mails
      mails.foreach(m => println(m.contentText)) //print text content
    ```

4. obfuscating`config.xml`, so that you don't worry about your password in that file.
Encryption uses AES. you need to provide a finite seq of keys. Maila will randomly choose one of them
to obfuscate config file. When decrypting, maila will try to decrypt using all the keys
provided. So once a `config.xml` has been obfuscated, it can only be accessed by a maila
instance with the same group of keys.

    ```scala
      final val TRANSFORM_KEYS =
        List("w0j9j3pc1lht5c6b",
          "pelila8h8xyduk8u",
          "pqzlv3646t5czf43",
          "rlea96gwkutwhz4m",
          "7v3txdd4hcv0e1jd",
          "v6k98fmyags5ugfi",
          "uae6c909uc031a3l",
          "5rtsom1rerkdqg6s",
          "20o06zwhrv5uqflt",
          "104e8spzwv5c2s32")
      val mailaWithObfuscation=Maila.newInstance("D:\\MailaTest\\config.xml",true,TRANSFORM_KEYS.map(_.getBytes("utf8")))
    ```
---

##Batch mail tool

This project includes a simple cmd tool for sending batch mails.

###How to use:

1. add java to PATH.
2. `bmt -help`  you can have all instructions and examples:

Use csv file to define mails, and send:

    bmt send -mailsPath:./mails.csv

_*Only provided bmt.bat for windows_