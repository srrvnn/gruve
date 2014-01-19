# compile all .required java files for the build #
##################################################

import os

folder_source = 'D:\Projects\gruve\source'
folder_tomcat = 'C:\Program Files\Apache Software Foundation\Tomcat 8.0'

folder_servlets = folder_source + '\gruve\WEB-INF\classes'
folder_jsps = folder_source + '\gruve\WEB-INF\classes\hw\macs\gruve'

folder_lib = folder_source + '\gruve\WEB-INF\lib'
folder_tomcatlib = folder_tomcat + '\lib'

# print ('javac -Xlint:unchecked -cp "'+ folder_tomcatlib +'\servlet-api.jar;'+ folder_lib +'\*;." '+ folder_servlets +'\GruveServlet.java')

os.chdir(folder_servlets)

os.system('javac -Xlint:unchecked -cp "'+ folder_lib +'\*;." '+ folder_jsps +'\*.java')
os.system('javac -Xlint:unchecked -cp "'+ folder_tomcatlib +'\servlet-api.jar;'+ folder_lib +'\*;." '+ folder_servlets +'\GruveServlet.java')
