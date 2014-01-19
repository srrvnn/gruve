# clean all .class to prepare for a rebuild #
#############################################

import os

folder_source = 'D:\Projects\gruve\source'
folder_servlets = folder_source + '\gruve\WEB-INF\classes'
folder_jsps = folder_source + '\gruve\WEB-INF\classes\hw\macs\gruve'

debug = False	

# delete all .class files in servlets folder
############################################

if debug: print (folder_servlets)

for f in os.listdir(folder_servlets):

	ext = os.path.splitext(str(f))[-1].upper()

	if ext == ".CLASS":
		os.unlink(os.path.join(folder_servlets, f))

if debug: print ("Cleaned Servlets")

# delete all .class files in jsps folder 
########################################

if debug: print (folder_jsps)

for f in os.listdir(folder_jsps):

	ext = os.path.splitext(str(f))[-1].upper()

	if ext == ".CLASS":
		os.unlink(os.path.join(folder_jsps, f))

if debug: print ("Cleaned JSPs")		



