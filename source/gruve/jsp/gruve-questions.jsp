<!DOCTYPE html>

<%@ page contentType="text/html; charset=iso-8859-1" language="java" %>
<%@ page import="java.io.*"  %>
<%
 String sessionId = request.getParameter("sessionId");
 String emailId = request.getParameter("emailId");
 String gameOver = request.getParameter("gameOver");
 String gameWorld = request.getParameter("gameWorld");
 session.setAttribute( "sessionId", sessionId );
 session.setAttribute( "emailId", emailId );
 session.setAttribute( "gameOver", gameOver );
 session.setAttribute( "gameWorld", gameWorld );
%>
<html>
<head>
<title>  GRUVE client prototype - Questions</title>
<meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
<!-- Author: Srini Janarthanam -->

<style type="text/css">
  html { height: 100% }
  body { height: 80%; margin: 0; padding: 0 }

</style>
<script type="text/javascript">
	var vCode = "null";
	function initialize(){
		generateRandomValidationCode();
	}
	
	function generateRandomValidationCode(){
		vCode = "" + Math.random();
		vCode = vCode.replace("0.", "");
		document.uscores.vCode.value = vCode;
	}
	
	function validateForm(){
		if (!(document.getElementById("eg1").checked) && !(document.getElementById("eg2").checked) && !(document.getElementById("eg3").checked) && !(document.getElementById("eg4").checked)){
			alert("No response for statement 1 in section 2."); return false;
		}
		if (!(document.getElementById("pa1").checked) && !(document.getElementById("pa2").checked) && !(document.getElementById("pa3").checked) && !(document.getElementById("pa4").checked)){
			alert("No response for statement 2 in section 2."); return false;
		}	
		if (!(document.getElementById("ml1").checked) && !(document.getElementById("ml2").checked) && !(document.getElementById("ml3").checked) && !(document.getElementById("ml4").checked)){
			alert("No response for statement 3 in section 2."); return false;
		}	
		if (!(document.getElementById("bh1").checked) && !(document.getElementById("bh2").checked) && !(document.getElementById("bh3").checked) && !(document.getElementById("bh4").checked)){
			alert("No response for statement 1 in section 1."); return false;
		}
		if (!(document.getElementById("ei1").checked) && !(document.getElementById("ei2").checked) && !(document.getElementById("ei3").checked) && !(document.getElementById("ei4").checked)){
			alert("No response for statement 2 in section 1."); return false;
		}	
		if (!(document.getElementById("wi1").checked) && !(document.getElementById("wi2").checked) && !(document.getElementById("wi3").checked) && !(document.getElementById("wi4").checked)){
			alert("No response for statement 3 in section 1."); return false;
		}	
		if (!(document.getElementById("fe1").checked) && !(document.getElementById("fe2").checked)){
			alert("No response for question 3 in section 3."); return false;
		}	
		if (!(document.getElementById("fn1").checked) && !(document.getElementById("fn2").checked)){
			alert("No response for question 4 in section 3."); return false;
		}			
	}
</script>
</head>


<body onload="initialize()">
	<div id="questions">
	<h1> Questions </h1>
	Session Id: <%= session.getAttribute( "sessionId" ) %><br />
	Email Id: <%= session.getAttribute( "emailId" ) %><br />
	Please answer a few questions to help our research<br />
	<form name="uscores" action="gruve-qlog-writer.jsp" onsubmit="return validateForm()" method="post">
	
	<h2>1. About your buddy </h2>
	
	Please rate your agreement with the following statements about the buddy assigned to you.<ol>
	<li>Your buddy helped you to complete the tasks in the game</li>
	<input type="radio" name="buddyHelped" id="bh1" value="agree">Agree</input>
	<input type="radio" name="buddyHelped" id="bh2" value="slightlyAgree">Slightly Agree</input>
	<input type="radio" name="buddyHelped" id="bh3" value="slightlyDisagree">Slightly Disagree</input>
	<input type="radio" name="buddyHelped" id="bh4" value="disagree">Disagree</input>
	<li>Your buddy's instructions were easy to understand</li>
	<input type="radio" name="easyInstructions" id="ei1" value="agree">Agree</input>
	<input type="radio" name="easyInstructions" id="ei2" value="slightlyAgree">Slightly Agree</input>
	<input type="radio" name="easyInstructions" id="ei3" value="slightlyDisagree">Slightly Disagree</input>
	<input type="radio" name="easyInstructions" id="ei4" value="disagree">Disagree</input>
	<li>Your buddy's instructions were accurate</li>
	<input type="radio" name="wrongInstructions" id="wi1" value="agree">Agree</input>
	<input type="radio" name="wrongInstructions" id="wi2" value="slightlyAgree">Slightly Agree</input>
	<input type="radio" name="wrongInstructions" id="wi3" value="slightlyDisagree">Slightly Disagree</input>
	<input type="radio" name="wrongInstructions" id="wi4" value="disagree">Disagree</input>
	</ol>
	
	<h2>2. About the game </h2>
	Please rate  your agreement with the following statements about the game.
	<ol>
	<li>You found the game engaging</li>
	<input type="radio" name="engagingGame" id="eg1" value="agree">Agree</input>
	<input type="radio" name="engagingGame" id="eg2" value="slightlyAgree">Slightly Agree</input>
	<input type="radio" name="engagingGame" id="eg3" value="slightlyDisagree">Slightly Disagree</input>
	<input type="radio" name="engagingGame" id="eg4" value="disagree">Disagree</input>
	<li>You will play it again (i.e. another similar navigation game or another neighbourhood)</li>
	<input type="radio" name="playAgain" id="pa1" value="agree">Agree</input>
	<input type="radio" name="playAgain" id="pa2" value="slightlyAgree">Slightly Agree</input>
	<input type="radio" name="playAgain" id="pa3" value="slightlyDisagree">Slightly Disagree</input>
	<input type="radio" name="playAgain" id="pa4" value="disagree">Disagree</input>
	<li>You would like to play more difficult and challenging levels of the game</li>
	<input type="radio" name="moreLevels" id="ml1" value="agree">Agree</input>
	<input type="radio" name="moreLevels" id="ml2" value="slightlyAgree">Slightly Agree</input>
	<input type="radio" name="moreLevels" id="ml3" value="slightlyDisagree">Slightly Disagree</input>
	<input type="radio" name="moreLevels" id="ml4" value="disagree">Disagree</input>
	
	</ol>
	<h2>3. About yourself </h2>
	<ol>
	<li>Your age: <input type="text" name="age"></input></li> 
	<li>How many hours do you spend playing computer games per week? <input type="text" name="gamingPerWeek"></input></li>
	<li>How would your rate your knowledge of streets in Edinburgh in general?</li>
	<input type="radio" name="familiarEdinburgh" id="fe1" value="yes">Familiar</input>
	<input type="radio" name="familiarEdinburgh" id="fe2" value="no">Not familiar</input>
	<li>How would you rate your knowledge of the streets in the gameworld (the neighbourhood) before you played the game?</li>
	<input type="radio" name="familiarNeighbourhood" id="fn1" value="yes">Familiar</input>
	<input type="radio" name="familiarNeighbourhood" id="fn2" value="no">Not familiar</input>
	<li>Any other comments: <br/>
	<textarea name="otherComments" cols="40"></textarea>
	</ol>
	<input type="hidden" name="sessionId" value="<%= session.getAttribute( "sessionId" ) %>" />
	<input type="hidden" name="emailId" value="<%= session.getAttribute( "emailId" ) %>" />
	<input type="hidden" name="gameOver" value="<%= session.getAttribute( "gameOver" ) %>" />
	<input type="hidden" name="gameWorld" value="<%= session.getAttribute( "gameWorld" ) %>" />
	<input type="hidden" name="vCode" value="" />
	<input type="submit" />
	</form>
	</div>
</body>
</html>