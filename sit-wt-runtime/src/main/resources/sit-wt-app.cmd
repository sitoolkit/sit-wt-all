set ENV_FILE=sit-wt.env
set WT_APP_DIR=%USERPROFILE%\.m2\repository\io\sitoolkit\wt\sit-wt-app

if not exist %ENV_FILE% (
  call mvnw exec:exec -Denv_file=%ENV_FILE%
)

for /F "delims== tokens=1,2" %%i in (%ENV_FILE%) do (
  if %%i == VERSION (
    set VERSION=%%j
  )
)

set WT_APP_JAR=%WT_APP_DIR%\%VERSION%\sit-wt-app-%VERSION%.jar

if not exist %WT_APP_JAR% (
  call mvnw -P app-jar-download
)

java -jar %WT_APP_JAR%
