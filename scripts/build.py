folder_scripts = 'D:\Projects\gruve\scripts'

# clean 

print ("Cleaning.")
exec(open(folder_scripts + "\clean.py").read())

# compile

print ("Compiling.")
exec(open(folder_scripts + "\compile.py").read())

# deploy

print ("Deploying.")
exec(open(folder_scripts + "\deploy.py").read())