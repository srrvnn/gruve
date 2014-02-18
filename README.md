gruve
=====

A web based game to test systems that give route instruction in uncertain virtual environments. This is a forked version of the game.  Game environment, system design and toolkit provided by [Interaction Lab](https://sites.google.com/site/hwinteractionlab/), Heroit Watt University.

To play a demo, it is necessary to run the system locally using a server than can run Java Server Pages and Servlets. 

setup
----

1.	Setup Web Server. It is necessary to have Apache Tomcat installed on the computer to setup and run the GRUVE game. Please download and install the web server from the [official site](http://tomcat.apache.org/index.html).

2.	Setup permissions. Give write permissions to the current user at \webapps inside the Tomcat installation directory to allow the game to create log files when required. 

3.	Setup installation location as environment variables and in Configuration.java. Set `%CATALINA_HOME%` to be the installation location of the directory. Set the installation location here:  `source\gruve\WEB-INF\classes\hw\macs\gruve\Configuration.java:15`. 

Ex.  `C:\Program Files\Apache Software Foundation\Tomcat 8.0\webapps\gruve`

4.	Copy the folder ‘gruve’ inside source into the `\webapps` folder of tomcat installation.

5.	Start the server, and go to `http://localhost:8080/gruve/`

gameplay
----

To read instructions, open `source/gruve/gruve-help.jsp` on your localhost.

reports
----

Find project reports at `reports/`.  
Not for reproduction.

bugs and todos
----

Find known bugs at `reports/bugs.md`.  
Find todos at `reports/todos.md`.

changelog
----

05.14 - Configuration class added to enter server address at one place.


