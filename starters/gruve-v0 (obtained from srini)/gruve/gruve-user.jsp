<!DOCTYPE html>
<html>
<head>
<title>  GRUVE client prototype - version 4</title>
<meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
<!-- Author: Srini Janarthanam -->
<!-- Date: 11 Jan 2012 -->
<!-- Gruve 4 + log -->

<style type="text/css">
  html { height: 100% }
  body { height: 80%; margin: 0; padding: 0; background-color:#b0c4de;}
  #panel { font-size:30px;}
  #howto {position:fixed; font-size:30px; top:0px; right:5px;}
  #map_canvas { height: 100% }
  #instruction { font-size:large; }	
  #busy { position:absolute; top:50px; right:0; width:200px;}
</style>
<link href="jplayer_skin/blue.monday/jplayer.blue.monday.css" rel="stylesheet" type="text/css" />

<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.6/jquery.min.js"></script>
<script type="text/javascript" src="jplayer_js/jquery.jplayer.min.js"></script>
<script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?sensor=false"></script>
<script type="text/javascript" src="js/gameworldmethods.js"></script> 
<script type="text/javascript" src="js/loadJS.js"></script> 
<script type="text/javascript" src="js/tools.js"></script> 
<script type="text/javascript" src="js/servletAccess.js"></script> 
<script type="text/javascript" src="js/tts.js"></script>

<!--Change the game world by setting it to appropriate gameworlds below-->
<script type="text/javascript" src="gameworlds/gameworld1.js"></script> 


<script type="text/javascript">
  var map;
  var roadmap;
  var lastPosition;
  var currentUserPosition,currentUserPositionPlusError;	
  var lat, lng;
  var marker;
  var buddyId;
  var currentPano;
  var currentHeading, currentBearing;
  var service;
  var lastSpoken;
  var soundFilePlaying;
  var instruction;
  var sessionId, userEmail;
  var userPosition;
  var userDA;
  var userResponse;
  var buddy, userType;
  var maxDistError;
  
  // following are variable concerning jplayer
  var stillPlaying = false;
  var moreToPlay = new Array();
  
  var showText, showDebugPanel, logAll;
  var lastRequestTimeStamp, setTimer, t;

  
  //<![CDATA[
  $(document).ready(function(){

	$("#jquery_jplayer_1").jPlayer({
		ready: function (event) {
			$(this).jPlayer();
		},
		swfPath: "js",
		supplied: "mp3",
		wmode: "window"
	});
	
	$("#jquery_jplayer_1").bind($.jPlayer.event.play, function(event) { // Add a listener to report the time play began
		$("#playBeganAtTime").text("Play began at time = " + event.jPlayer.status.currentTime);
	});
	
	$("#jquery_jplayer_1").bind($.jPlayer.event.playing, function(event) { 
		$("#playing").text("Playing...");
		stillPlaying = true;
	});
	
	$("#jquery_jplayer_1").bind($.jPlayer.event.ended, function(event) { 
		$("#playing").text("Ended");
		stillPlaying = false;
		if (moreToPlay.length > 0){
			var file = moreToPlay.shift();
			$("#jquery_jplayer_1").jPlayer("setMedia", {mp3:file});
			$("#jquery_jplayer_1").jPlayer("play");
			document.getElementById("waitQueue").innerHTML = moreToPlay;
		}
	});
  });
  
  //]]>
  
  
  
  function gotoQuestionnaire(){
	window.location.href = "jsp/gruve-questions.jsp?sessionId="+sessionId+"&emailId="+userEmail+"&gameOver=false"+"&gameWorld="+gameworld; 
  }
  
  function initialize() {

	userEmail = '<%= request.getParameter("email") %>';
	
	//Set buddy as wizard if you want the user to interact with a wizard instead of the system
	buddy = "system";
	//buddy = "wizard";
	
	//Set the following variable to add error to the user location information 
	//sent to the buddy system
	maxDistError = 0.00; // in km 
	
	sessionId = (new Date()).toUTCString();
	sessionId = sessionId.replace(/,/g,"");
	sessionId = sessionId.replace(/ /g,"-");
	sessionId = sessionId.replace(/:/g,"-");

	userType = "user";
	
	showText = true;
	showDebugPanel = false;
  	logAll = true;
	
	
	soundFilePlaying = false;
	
	//tts="none";
	//tts = "google";
	tts = "cereproc";
	
	setTimer = true;
	//setTimer = false;
	
	//automatically load the gameworld using the value of world parameter in the url..
	//http://localhost:8080/gruve/gruve4.jsp?world=gameworld
	//var gameworld='<%= request.getParameter("world") %>';
	//alert(gameworld);
	//loadJavaScript('gameworlds/' + gameworld + '.js'); 
	//helloGameWorld();
	

	myStreetViewOptions = {
		// disableDoubleClickZoom: true disables zooming using double click
		disableDoubleClickZoom: true,
		// addressControl: false disables address being displayed on the top left cornet of streetview
		addressControl: false,  
		//scrollwheel: false disables zooming using the mouse scrollwheel
		scrollwheel: false,
		zoomControl: false,
		//panControl: false disables pan control on the top left corner.. this will stop users from looking up or down.. 
		panControl: false,
		//linksControl: false disables the street/road labels and arrows in the streetview. it also disables navigation using arrow keys in the streetview.
		linksControl: true,
		position: currentUserPosition,
		visible: true	
	}
	map = new google.maps.StreetViewPanorama(document.getElementById("map_canvas"), myStreetViewOptions);
	
	initializeGame();
	currentHeading = map.getPov().heading;
	currentUserPosition = player_position;
	lastPosition = currentUserPosition;
	
	
	google.maps.event.addListener(map, 'position_changed', function() {
		positionChanged();
  	});

	google.maps.event.addListener(map, 'pov_changed', function() {
		povChanged();
  	});	

	userDA = {"cf": "null"};
	userUtterance = "null";
	
	userResponse = "null";
	lastRequestTimeStamp = Date.now();
	sendLocationInfo();
	
  }

  function initializeGame(){
	initiateGameworld(map);	
	populateGameWorldLocations(document.getElementById("dest"));
	populateGameWorldObjects(document.getElementById("objects"));
	userResponse = "greet";
  }

  function povChanged(){
	currentHeading = map.getPov().heading % 360;
  }

  function positionChanged(){
	if (game_over == true) {
		map.setPosition(currentUserPosition);	
	} else {
		lastPosition = currentUserPosition;
	    currentUserPosition = map.getPosition();
		if (lastPosition != null && lastPosition != currentUserPosition){
			currentBearing = getBearing(lastPosition, currentUserPosition);
			//document.getElementById("debug").innerHTML = "Debug: " + " LP:" + lastPosition +  " CP:" + currentUserPosition + " CurrentBearing:" + currentBearing;
		}
		userPosition = "" + currentUserPosition.lat() + ";" + currentUserPosition.lng();
		gamePosChanged();
	}
	sendLocationInfo();
	userDA =  {"cf": "null"};
 }

  function debug(){
	if (showDebugPanel == true){
		document.getElementById("debug").innerHTML = "Debug: " + "WDI:" + wrongDirectionInformed + " Orient : " + currentHeading + " BO: " + bCurrentUserOrientationAngle + " RO: " + requiredBearing;
	}
  }

  ////////////////////////////////////////////////////
  // USER REQUESTS
  ////////////////////////////////////////////////////

  function help(){
	userDA =  {"cf": "requestHelp"};
	sendUtteranceInfo();
  }
  
  function repeat(){
	userDA =  {"cf": "requestRepeat"};
	sendUtteranceInfo();
  }	

  function getRoutePressed(){
	var destIndex = document.getElementById("dest").selectedIndex;
	var destination = document.getElementById("dest").options[destIndex].text;
	userDA =  {"cf": "requestRoute", "destination_name": destination, "destination_type":"street"};
	sendUtteranceInfo();
  }

  
  function abandonRoute(){
	userDA =  {"cf": "stopDirections"};
	sendUtteranceInfo();
  }

  // this function is called when the user is confused what to do next on the route and presses the button	
  function whatNextPressed(){
	userDA =  {"cf": "requestDirection"};
	sendUtteranceInfo();
  }

  function okPressed(){
	userDA = {"cf":"acknowledge"};
	sendUtteranceInfo();
  }
  
  function quitGame(){
	userDA = {"cf":"quitGame"};
	sendUtteranceInfo();
  }

  //  
  function speak_instruction() {
	var ins2speak = instruction;
	ins2speak = ins2speak.replace(/(\n|\r)+$/, '');
	ins2speak = ins2speak.replace(/'/, '');
	if (ins2speak != "") {
		if (showText == true){
			document.getElementById("instruction").innerHTML= "Instruction : " + ins2speak;	
		}
		speak(ins2speak);	
	}	
  }


  function speak(inf){
    currentSysUtterance = inf;
	var file;
	if (tts == "google") { 
		speakUsingGoogle(inf);
	} else if (tts=="cereproc"){
		speakByCereproc(inf);
	}	
  }

  
  function addErrorToUserPosition(){
	currentUserPositionPlusError = getNextPoint(currentUserPosition, (Math.random() * maxDistError), currentBearing);
	//document.getElementById("debug").innerHTML += " CPE: " + currentUserPositionPlusError;
  }
  
  function sendUtteranceInfo(){
	clearTimeout(t);
	userDA = JSON.stringify(userDA);
	addErrorToUserPosition();
	tellSBServlet(buddy, userDA, userUtterance, currentUserPosition.lat() + ";" + currentUserPosition.lng(),userEmail);
	
	userDA = {"cf": "null"};
	userUtterance = "null";	
	
	//if setTimer == true, every two seconds the client sends a message to the server.. 
	//whether or not the user has spoken or moved.. 
	if (setTimer == true){
		//alert("setting timer u");
		t = setTimeout("sendLocationInfo()",10000);
	} else {
		clearTimeout(t);
	} 
	
  }
  
  function sendLocationInfo(){
	
	if (game_over == true){
		var r = confirm("Thanks for playing. Can you answer a few questions to help our research?");
		if (r == true){
			window.location.href="jsp/gruve-questions.jsp?sessionId="+sessionId+"&emailId="+userEmail+"&gameOver=true"+"&gameWorld="+gameworld;
		}
	} 
	clearTimeout(t);
	lastRequestTimeStamp = Date.now();
	addErrorToUserPosition(); 
	
	userDA = {"cf": "null"};
	userDA = JSON.stringify(userDA);
	tellSBServlet(buddy,userDA, "null", currentUserPositionPlusError.lat() + ";" + currentUserPositionPlusError.lng(),userEmail);

	userDA = {"cf": "null"};
	userUtterance = "null";	
	
	
	//if setTimer == true, every two seconds the client sends a message to the server.. 
	//whether or not the user has spoken or moved.. 
	if (setTimer == true){
		//alert("setting timer l");
		t = setTimeout("sendLocationInfo()",10000);
	} else {
		clearTimeout(t);
	} 
  }

</script>
</head>


<body onload="initialize()" onunload="alert('closing')" onpagehide="alert('closing2')" onbeforeunload="alert('closing3')">
	<div id="panel"></div>
	<div id="map_canvas" style="width:100%; height:100%"></div>
	<div id="map"></div>
	<div id="controls">
	ASK YOUR BUDDY <br/> 
	<button id="help" onClick="help()"> Help </button>
	|||
	How do I get to  
	<select name="Destination" id="dest"></select>
	<button id="getroute" onClick="getRoutePressed()"> Send </button>
	|||
	<button id="abandon" onClick="abandonRoute()"> Stop directions </button>
	|||
	<button id="whatnext" onClick="whatNextPressed()"> Which direction now? </button>
	|||
	<button id="okButton" onClick="okPressed()"> Ok </button>
	|||	
	<button id="repeat" onClick="repeat()"> Repeat instruction </button>
	|||
	</div>
	<div id="howto">
		<a href="javascript:gotoQuestionnaire()">QUIT </a>&nbsp;&nbsp;&nbsp;&nbsp;
		<a href="gruve-help.jsp" title="Opens in a new window" target="_blank">How to play?</a>
	</div>
  	<div id="buddy_text"></div>
	<div id="debug"></div>
	
	<div id="instruction"></div>
	
	<!-- Google TTS output needs to be embedded in this sound_element div -->
	<div id="sound_element"></div>
	<!-- jplayer requires the following div code -->
	<div id="jquery_jplayer_1" class="jp-jplayer"></div>
	<div id="jp_container_1" class="jp-audio">
		<div class="jp-type-single"></div>
	</div>
	<!-- jplayer code ends-->
  </div>
</body>
</html>