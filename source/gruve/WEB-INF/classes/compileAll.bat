cd %~dp0 
javac -Xlint:unchecked -cp "../lib/*;." hw/macs/gruve/*.java
javac -Xlint:unchecked -cp "%CATALIN_HOME%\lib\servlet-api.jar;../lib/json_simple-1.1.jar;." GruveServlet.java