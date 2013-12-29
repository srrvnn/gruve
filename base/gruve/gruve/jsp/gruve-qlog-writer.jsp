<%@ page contentType="text/html; charset=iso-8859-1" language="java" %>
<%@ page import="java.io.*"  %>
<%
 String sessionId = request.getParameter("sessionId");
 String emailId = request.getParameter("emailId");
 String vCode = request.getParameter("vCode");
 session.setAttribute( "vCode", vCode );
 String age = request.getParameter("age");
 String engagingGame = request.getParameter("engagingGame");
 String playAgain = request.getParameter("playAgain");
 String moreLevels = request.getParameter("moreLevels");
 String buddyHelped = request.getParameter("buddyHelped");
 String easyInstructions = request.getParameter("easyInstructions");
 String wrongInstructions = request.getParameter("wrongInstructions");
 String familiarEdinburgh = request.getParameter("familiarEdinburgh");
 String familiarNeighbourhood = request.getParameter("familiarNeighbourhood");
 String gamingPerWeek = request.getParameter("gamingPerWeek");
 String otherComments = request.getParameter("otherComments");
 String gameOver = request.getParameter("gameOver");
 String gameWorld = request.getParameter("gameWorld");
 //always give the path from root. This way it almost always works.
 String fn = sessionId + "-questions.txt";
 String nameOfTextFile = "../webapps/gruve/qlogs/"+fn;
 try {   
    PrintWriter pw = new PrintWriter(new FileOutputStream(nameOfTextFile, true));
    pw.println("SSID:" + sessionId);
	pw.println("EmailID:" + emailId);
	pw.println("Verification Code:" + vCode);
	pw.println("age:" + age);
	pw.println("engagingGame:" + engagingGame);
	pw.println("playAgain:" + playAgain);
	pw.println("moreLevels:" + moreLevels);
	pw.println("buddyHelped:" + buddyHelped);
	pw.println("easyInstructions:" + easyInstructions);
	pw.println("wrongInstructions:" + wrongInstructions);
	pw.println("familiarEdinburgh:" + familiarEdinburgh);
	pw.println("familiarNeighbourhood:" + familiarNeighbourhood);
	pw.println("gamingPerWeek:" + gamingPerWeek);
	pw.println("otherComments:" + otherComments);
	pw.println("gameWorld:" + gameWorld);
	pw.println("gameOver:" + gameOver);
	//clean up
    pw.close();
    //out.println(q);
 } catch(IOException e) {
   out.println(e.getMessage());
 }
%>
<HTML>
<BODY>
Thank you for playing the game! <br/>
Here is your completion code <%= session.getAttribute( "vCode" ) %> <br/>
</BODY>
</HTML>