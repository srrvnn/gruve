# compile all .required java files for the build #
##################################################

import os

folder_source = 'D:\Projects\gruve\source'
folder_tomcat = 'C:\Program Files\Apache Software Foundation\Tomcat 8.0'

os.system('xcopy /s /y /q /d "'+ folder_source +'" "'+ folder_tomcat +'\webapps"')