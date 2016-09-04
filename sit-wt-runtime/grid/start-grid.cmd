@rem http://repo.jenkins-ci.org/releases/org/seleniumhq/selenium/selenium-server-standalone/2.53.0/selenium-server-standalone-2.53.0.jar

start java -jar selenium-server-standalone-2.53.0.jar -role hub

start java -jar selenium-server-standalone-2.53.0.jar -role node -nodeConfig node.json -Dwebdriver.chrome.driver=C:\ProgramData\sitoolkit\repository\selenium\chrome\runtime\chromedriver_win32-2.23.exe
