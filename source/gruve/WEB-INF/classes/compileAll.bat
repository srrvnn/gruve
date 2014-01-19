cd %~dp0 
javac -Xlint:unchecked -cp "../lib/json_simple-1.1.jar;../lib/slf4j-api-1.6.2.jar;../lib/slf4j-log4j12-1.6.2.jar;." hw/macs/gruve/*.java
javac -Xlint:unchecked -cp "C:\Program Files\Apache Software Foundation\Tomcat 8.0\lib\servlet-api.jar;../lib/json_simple-1.1.jar;." GruveServlet.java