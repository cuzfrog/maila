[![wercker status](https://app.wercker.com/status/904c2e592f536e3a298bb1bbf18c398f/s/master "wercker status")](https://app.wercker.com/project/bykey/904c2e592f536e3a298bb1bbf18c398f)
[ ![Download](https://api.bintray.com/packages/cuzfrog/maven/maila/images/download.svg) ](https://bintray.com/cuzfrog/maven/maila/_latestVersion)
# Maila

* Scala library wrapping JavaMail for reading and sending emails with simple APIs.
* This project includes **BatchMailTool**, a simple cmd tool for sending batch mails.(See below)

###Usage:

1. provide a config.xml:



2. sending mail:

    ```scala
    val maila = Maila.newInstance("D:\\MailaTest\\config.xml")
    val mail = Mail(List("recipient@google.com"), "subject", "text content")
    maila.send(List(mail))
    ```

3. reading mail:


4. obfuscating config.xml (so that you don't worry about your password in that file.)


---

##Batch mail tool

This project includes a simple cmd tool for sending batch mails.

###How to use:

1. add java to PATH.
2. `bmt -help`  you can have all instructions and examples:

Use csv file to define mails, and send:

    bmt send -

_*Only provided bmt.bat for windows_