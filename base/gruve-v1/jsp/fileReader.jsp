<%@ page contentType="text/html; charset=iso-8859-1" language="java" %>
<%@ page import="java.io.*"  %>
<%
 String q =request.getParameter("q");
 String fn =request.getParameter("fn");
 //following refers to c:/logs/
 String nameOfTextFile = fn;
 try {   
	BufferedReader input = new BufferedReader(new FileReader(nameOfTextFile));
	String line = "";
	while ((line = input.readLine()) != null) {
		out.println(line);
	}
	input.close();
 } catch(IOException e) {
   	out.println(e.getMessage());
 }
%>

