<%@ page contentType="text/html; charset=iso-8859-1" language="java" %>
<%@ page import="java.io.*"  %>
<%
 String q =request.getParameter("q");
 String fn =request.getParameter("fn");
 //always give the path from root. This way it almost always works.

 String nameOfTextFile = "../webapps/ROOT/gruve/logs/"+fn;
 //String nameOfTextFile = "h:/www/"+ fn;
 try {   
    PrintWriter pw = new PrintWriter(new FileOutputStream(nameOfTextFile, true));
    pw.println(q);
    //clean up
    pw.close();
    //out.println(q);
 } catch(IOException e) {
   out.println(e.getMessage());
 }
%>
