# sit-wt-app

## JRE generation

Execute the following on a console for MacOS or GitBash for Windows:

```
cd sit-wt-all/sit-wt-app
mkdir -p target
cd target

export FX_VERSION=11.0.2
curl -L -o fx-jmods.zip http://gluonhq.com/download/javafx-${FX_VERSION//./-}-jmods-windows
unzip fx-jmods.zip
rm -rf ../jre
jlink --compress=2 --output ../jre --module-path javafx-jmods-${FX_VERSION}/ --add-modules javafx.web,javafx.fxml,java.xml,jdk.unsupported,java.sql,java.naming,java.desktop,java.management,java.security.jgss,java.instrument
```