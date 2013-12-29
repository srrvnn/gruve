<!DOCTYPE html>

<%@ page contentType="text/html; charset=iso-8859-1" language="java" %>
<%@ page import="java.io.*"  %>
<%
 String email = request.getParameter("email");
 session.setAttribute( "email", email );
%>

<html>
<head>
<title>  GRUVE client prototype - HELP - version 3</title>
<meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
<!-- Author: Srini Janarthanam -->
<!-- Date: 02 Sep 2011 -->
<!-- Gruve 3.1 + log -->

<style type="text/css">
  html { height: 100% }
  body { height: 100%; margin: 0; padding: 0; background-color:#b0c4de; }

</style>

<script type="text/javascript"> 
	var email;

	function init() {
		email = '<%= session.getAttribute("email") %>';			
	}
	
	function gotoGames(){
		window.location.href = "gruve-user.jsp?id=" +email; 
	}
</script>
</head>


<body>
	<div id="help">
	<h1> Instructions </h1>
	<h2> How to play? </h2>
	<h3> Objective </h3>
	Your objective is to unravel a sequence of clues in order to find the treasure chest. In order to open the treasure chest, you need to know where it is and the key to open it. You will get this if you follow the instructions of the game people (e.g. Bagpipers, Pirates, Highland dancers, etc) you meet on the streets. You can interact with the game people by clicking on them. You can pick up keys and open chests by clicking on them.<br/><br/>

You can seek help from your buddy using the panel of buttons. When the game begins you will be assigned a buddy, who has a birds-eye view of the game world. He can therefore tell you where some game people are, how to get from one place to another, etc. Using the buttons and lists in the "Ask your buddy" panel you can construct requests and questions to your buddy. Your buddy will respond by speaking to you. So make sure you turn your audio on. <br/>
	<h3> Navigation </h3>
	After the webpage is fully loaded, click on the <i>gameworld</i> panel. Use the arrow keys on your keyboard to walk through in the <i>gameworld</i>. Up/Down keys for walking. Left/Right keys for turning. <b>Click on the <i>gameworld</i> panel if the keys seem unresponsive.</b> Sometimes when you are browsing down the lists in the buddy panel, it helps to first click on the webpage, before you click on the gameworld panel. At the beginning, if the gameworld panel appears blank, it may still be loading. If it appears fractured, use left/right keys to turn around a bit, to refresh the view.<br/>
	<h3> Interaction </h3>
	You can ask the system questions by using the buttons and lists in the <i>interaction</i> panel. Click on the lists to see all options and choose the one you need. After selecting the option that you need, press the SEND button. There are two lists: Streets and People. So you can ask your buddy to give you instructions to a street you want to go to or ask him where a person you want to meet is. <br/>
	<h3> Constraints </h3>
	To win the game, you have to find the treasure before your energy drains out. You can notice that it reduces as you walk. In order to increase it, you need to get some food at a restaurant. You can ask your buddy where the nearest restaurant is. In order to buy food, you need money. You can get cash at a cash machine. Your buddy can help you find one. However, there is a limit on how much you can totally withdraw. Also beware of the pickpocket, who can leave you with an empty pocket when you need cash. <br/>
	
	<h2> How to quit? </h2>
	You can quit anytime by clicking on the QUIT link on the top right corner of the screen. You will be taken to a page with questions about the game and your helper buddy. 
	
	
	<h2> Technical requirements </h2>
	Best played in Google Chrome browser. In some browsers, the audio doesn't work. In some, the buddy panel lists don't get populated.<br/>
	Turn your volume on as instructions from your buddy will be in audio format.<br/>
	<h2> Acknowledgements </h2>
	Thanks to Google Maps API, which has been used extensively in this game.
	</div>
	
	<h3>FAQs</h3>
	<h3><a href="game-index.html">Goto Index page</a><h3>
</body>
</html>