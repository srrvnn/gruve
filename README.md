gruve
=====

A web based game to test systems that give route instruction in uncertain virtual environments. This is a forked version of the game.  Game environment, system design and toolkit provided by [Interaction Lab](https://sites.google.com/site/hwinteractionlab/), Heroit Watt University.

To play a demo, it is necessary to run the system locally using a server than can run Java Server Pages and Servlets. 

setup
----

1. Install [Apache Tomcat](http://tomcat.apache.org/download-70.cgi), the server to run JSP files and Servlets. 
  	
2. Place the folder titled 'gruve' inside source at the webapps folder of Apache Tomcat. 

3. Give write permissions inside the ‘gruve’ folder. 

4. Enter the absolute location of the ‘gruve’ folder here: 

	`source/gruve/WEB-INF/classes/hw/macs/gruve/Configuration.java:13`
	
5. Enter the absolute location of Apache Tomcat in all the .bat files at `source/gruve/WEB-INF/classes`. 

6. On Windows: Run all .bat files at `source/gruve/WEB-INF/classes`

   On Unix: Run all commands inside all .bat files at `source/gruve/WEB-INF/classes`
   
7. Start Apache Tomcat server.

8. To play the game, open `source/gruve/game-index.html` on your localhost.
   	
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


