# Dev Memo


* logging integrate to slf4j + logback
* dependency
* webview
* distribution pom
* properties



# Usecase Scenario


SI-Tookit for Web Testingは3つの使用方法があります。

* Application
* CLI
* Embedded


# Application

SIT-WTはデスクトップアプリケーションとして実行できます。
sit-wt-app-xxx.jarをダウンロードし、ダブルクリックして実行してください。



# CLI

SIT-WTはMavenコマンドとしても実行できます。


```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>your.group.id</groupId>
  <artifactId>your-artifact-id</artifactId>

  <parent>
    <groupId>org.sitoolkit.wt</groupId>
    <artifactId>sit-wt-project</artifactId>
    <version>3.0</version>
  </parent>

</project>
```




generate-test-sources
  sit-wt:selenium2script
  sit-wt:script2java

integration-test
  failsafe:integration-test

post-integration-test
  surefire-report:failsafe-report-only

verify
  failsafe:verify


# Embedded



* pom.xml

```xml

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>your.group.id</groupId>
  <artifactId>your-artifact-id</artifactId>

  <dependencies>
    <dependency>
      <groupId>org.sitoolkit.wt</groupId>
      <artifactId>sit-wt-runtime</artifactId>
      <version>3.0</version>
    </dependency>
  </dependencies>

  <properties>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
  </properties>

</project>
```


```java

import org.sitoolkit.wt.TestRunner;

public class Main {

  public static void main(String[] args) {

    TestRunner runner = new TestRunner();
    runner.runTest("path/to/TestScript.xlsx");


  }

}

```

## change dependent library version


* pom.xml

```xml

  <dependencies>
    <dependency>
      <groupId>org.sitoolkit.wt</groupId>
      <artifactId>sit-wt-runtime</artifactId>
      <version>3.0</version>
    </dependency>
    <dependency>
      <groupId>org.seleniumhq.selenium</groupId>
      <artifactId>selenium-java</artifactId>
      <version>3.8.1</version>
    </dependency>
  </dependencies>


```


## Logging configuration


```
src/main/resources/logback.xml
```



# Patch


