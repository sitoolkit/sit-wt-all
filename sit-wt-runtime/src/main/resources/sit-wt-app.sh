#!/bin/sh

ENV_FILE=sit-wt.env
WT_APP_DIR=~/.m2/repository/io/sitoolkit/wt/sit-wt-app

if [[ ! -f ./${ENV_FILE} ]]; then
  ./mvnw exec:exec -Denv_file=${ENV_FILE}
fi

source ./${ENV_FILE}

WT_APP_JAR=${WT_APP_DIR}/${VERSION}/sit-wt-app-${VERSION}.jar
if [[ ! -f ${WT_APP_JAR} ]]; then
  ./mvnw -P app-jar-download
fi

java -jar ${WT_APP_JAR}
