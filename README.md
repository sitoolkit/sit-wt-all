# Web Tester

Web Tester is an automatic test tool for web applications. Automatically operate the browser according to the created test script. 

* You can create test scripts **with non-programming**.
* You can also use test scripts recorded with **Selenium IDE**.
* **Evidence** (screenshot and operation log) is **created automatically** for the screen operation executed.
* Selenium and Appium are used for browser operation.

**Notes: Running this application requires Java 11.**

## Quick Start

### GUI 

Use GUI tools to create test projects and test scripts.
You can immediately try the included sample website and test script.

1. [Download](https://repo.maven.apache.org/maven2/io/sitoolkit/wt/sit-wt-app/3.0.0-beta.3/sit-wt-app-3.0.0-beta.3.jar) jar file.
1. Execute it by double clicking.
1. Select a directory to create your test project.
1. Select **Sample** Menu > **Start** to start sample web site and get sample test script.
1. Select **Test** Menu > **Run** / **Debug** to start test.

**If JNI error dialog occurs, may be using older version Java.**  
**Open properties of "sit-wt-app.jar", change "Opens with" to "javaw.exe" of Java 11.**

### CLI

To run test scripts in the test project at once, use the CLI tool (Maven Plugin).

#### Windows

```
cd \path\to\testproject
mvnw verify
mvnw sit-wt:open-report
```

#### MacOS

```
cd /path/to/testproject
./mvnw verify
./mvnw sit-wt:open-report
```


## Lisence

Web Tester is released under [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
