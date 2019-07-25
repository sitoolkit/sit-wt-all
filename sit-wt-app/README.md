# sit-wt-app

## Generate JRE

Execute the following on a console for MacOS or GitBash for Windows:

```
cd sit-wt-all/sit-wt-app
mkdir -p target
cd target

export FX_VERSION=11.0.2
export OS_TYPE=<windows/mac/linux>
curl -L -o fx-jmods.zip http://gluonhq.com/download/javafx-${FX_VERSION//./-}-jmods-${OS_TYPE}
unzip fx-jmods.zip
rm -rf ../jre
jlink --compress=2 --output ../jre --module-path javafx-jmods-${FX_VERSION}/ --add-modules javafx.web,javafx.fxml,java.xml,jdk.unsupported,java.sql,java.naming,java.desktop,java.management,java.security.jgss,java.instrument
```

### For cross platform

1. Download and extract the OpenJDK for target OS as a compressed file such as zip or tar.gz
2. 

```
export JMODS_DIR=<path/to/openjdk/jmods/>
jlink --compress=2 --output ../jre --module-path "javafx-jmods-${FX_VERSION}/;${JMODS_DIR}" --add-modules javafx.web,javafx.fxml,java.xml,jdk.unsupported,java.sql,java.naming,java.desktop,java.management,java.security.jgss,java.instrument
```


## Run application

### Eclipse

1. Open window > preferences
1. Select Java > Installed JREs and add sit-wt-app/jre directory as Standard VM 
1. In Run Configurations, select the JRE you added in the previous step

### Command Line

Run following commands:

```
cd sit-wt-all
./mvnw clean install -Dmaven.test.skip=true
cd sit-wt-app/target
../jre/bin/java -jar sit-wt-app-3.0.0-beta.2-SNAPSHOT.jar
```
