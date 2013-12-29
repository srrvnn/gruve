<%@ page contentType="text/html; charset=iso-8859-1" language="java" %>
<%@ page import="java.io.*"  %>
<%
 String q =request.getParameter("q");
 String fn =request.getParameter("fn");
 String t =request.getParameter("t");
 boolean appendFile = true;
 if (t.equals("new")){ appendFile = false;}
 //always give the path from root. This way it almost always works.
// String nameOfTextFile = "../webapps/ROOT/spacebook/"+fn;
 String nameOfTextFile = fn;
 try {   
    PrintWriter pw = new PrintWriter(new FileOutputStream(nameOfTextFile, appendFile));
    pw.println(q);
    //clean up
    pw.close();
    //out.println(q);
 } catch(IOException e) {
   out.println(e.getMessage());
 }
%>
