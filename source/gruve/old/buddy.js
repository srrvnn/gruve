
  var routeRequested;
  var route;
  var step, maxSteps;
  var currentStartOfStep,currentEndOfStep;
  var lastEndOfStep;
  var foundRoutes;
  var destination;

  var bCurrentUserPosition, bLastUserPosition, bCurrentUserOrientationAngle;
  var bLastUserLat, bLastUserLng;
  var instruction;
  var SOSAddress, EOSAddress, currentStreet, requiredBearing, distance2NextNode;
  var instructionNotDelivered, wrongDirectionInformed;
  var nWrongDirections;
  
  var landmarksInRange = new Array();
  var landmarksNearEndOfStep = new Array();

  

  function tellBuddyJs(inp){
    var temp11 = inp.split(":::");	
  	var temp10 = temp11[1].split(":");	
  	
	var userDA = temp10[0]; 
	var userLatLng = temp10[1].split(";");
  	var userLat = parseFloat(userLatLng[0]);
  	var userLng = parseFloat(userLatLng[1]);
  	
  	bCurrentUserPosition = new google.maps.LatLng(userLat, userLng);
  	
  	
  	if (userDA == "null"){
  		//alert(bLastUserPosition + ":" + bCurrentUserPosition);
  		// calculate user orientation
  		
  		if (bLastUserLat != userLat || bLastUserLng != userLng){
  			bCurrentUserOrientationAngle = getBearing(bLastUserPosition, bCurrentUserPosition);
  			//alert("CO " + bCurrentUserOrientationAngle);
  		}
  	
  		landmarksInRange = getLandmarksInRange(bCurrentUserPosition, 30);
  		//alert(landmarksInRange);
		
  		//if instruction is ready but not delivered.. do it now
		if (instructionNotDelivered == true){
			instruction = getInstruction();
			speak(instruction);	
		}

  		if (foundRoutes == true){
  			

			// alert("currentEndOfStep: " + currentEndOfStep);
			// is the user near the currentEndOfStep??
  			// user position has changed.. so if he is already enroute, check if he is close to the next node and give next instruction
			var di = getDistance(bCurrentUserPosition,currentEndOfStep);
			// when the user is 20 meters near the current Next node, we get the next instruction..
			if (di < 20) {
				instruction = "wait";
				speak(instruction);	
				instruction = getNextStep();
				speak(instruction);	
			}
	
			// is the user deviating from his heading towards the next node?
			// is his current distance to next node greater than his last distance to next node + 20 meters?
			var di2 = getDistance(bLastUserPosition,currentEndOfStep);
			if (di > (di2 + 10)) {
				wrongDirection = true;
				if (wrongDirectionInformed == false) {
					instruction = "Wrong direction.";
					wrongDirectionInformed = true;
					nWrongDirections++;
					speak(instruction);
					//alert("finding route to " + destination);
					getRoute2Destination(destination);
				}				
			} 
			
			// if the current distance is less than last distance, wrongDirectionInformed is reset to false
			if (di < di2) {
				wrongDirectionInformed = false;
			}
		}
  	}
  	else if (userDA.search(/greet/) != -1) {
		speak("Hello! Welcome.");
  		routeRequested = false;
		route = null;
		foundRoutes = false;
		destination = "null";		
		wrongDirectionInformed = false;
		bLastUserPosition = null;
		bCurrentUserOrientationAngle = null;
  	}
  	else if (userDA.search(/requestRoute/) != -1){
  		var temp11 = userDA.split("=");
  		destination = temp11[1];
  		speak("Directions to " + destination);
  		getRoute2Destination(destination);  		
  	} 
  	else if (userDA.search(/amIOnTrack/) != -1){
  	} 
  	else if (userDA.search(/stopDirections/) != -1){
		routeRequested = false;
		route = null;
		foundRoutes = false;
		destination = "null";
		speak("Okay.");
  	}
  	else if (userDA.search(/whichWay/) != -1){
  		// user is asking for next instruction.. we replan his route..
		if (destination != "null"){
    			getRoute2Destination(destination);
		}
  	}
  	else if (userDA.search(/getClosest/) != -1){
  		var temp11 = userDA.split("=");
  		var xloc = getClosestEntityLocation(temp11[1]); // calls the game world
  		if (xloc != "null"){
			speak("There is a "+ temp11[1] + " on " + xloc);
		} else {
			speak("Sorry. I cannot find it");
		}
  	} 
  	
  	bLastUserPosition = bCurrentUserPosition;
  	bLastUserLat = userLat;
  	bLastUserLng = userLng;
  }


  function getRoute2Destination(dest){
  	//alert("getting route to "+ dest);
  	dest = dest + ", " + city;
	var directionsService = new google.maps.DirectionsService();
	var request = {
		origin:bCurrentUserPosition, 
		destination:dest,
		travelMode: google.maps.DirectionsTravelMode.DRIVING
	};
	
	foundRoutes = false;
	directionsService.route(request, function(response, status) {
		if (status == google.maps.DirectionsStatus.OK) {
			//document.getElementById("getroute").disabled = true;
			//if there are several routes, we take the first one
			//since we don't have waypoints, there is only one leg and we therefore take the first leg as the route required
			route = response.routes[0].legs[0];
			foundRoutes = true;
			step = 0;
			maxSteps = route.steps.length;
			instruction = getNextStep();
			speak(instruction);
		} 
		else {
			foundRoutes = false;
			speak("Sorry I cannot find any route to this destination");
		}
	});	
	
  }

  function getNextStep() {
	var inst;
	//alert(step + "," + maxSteps);
	if (step == maxSteps - 1) {
		inst = "This is " + destination;
		foundRoutes = false;
		destination = "null";
	} else {
		// each leg consists of steps (mostly turn instructions) 
		// Google's instructions are already a part of the route from their Directions API
		// see http://code.google.com/apis/maps/documentation/javascript/services.html#DirectionsResults
		currentStartOfStep = route.steps[step].start_location;
		currentEndOfStep = route.steps[step].end_location;
		requiredBearing = getBearing(currentStartOfStep, currentEndOfStep);
		//alert(requiredBearing);
		SOSAddress = "null";
		EOSAddress = "null";
	  	currentStreet = "null";
	  	//var mid = getMidpoint(currentStartOfStep, currentEndOfStep);
		var next = getNextPoint(currentStartOfStep, 0.01, requiredBearing);
	  	getStreetName("startOfStep", currentStartOfStep);
		getStreetName("endOfStep", currentEndOfStep);
		getStreetName("midPoint", next);
		distance2NextNode = getDistance(bCurrentUserPosition,currentEndOfStep);
		//inst = getNextGoogleInstruction(route.steps[step]);		
		inst = getInstruction();
		step++;
	} 
	return inst;
  }

  function getInstruction(){
  	var ins1 = "null";
  	if (buddyId == "google"){
  		ins1 = getNextGoogleInstruction();
  	} else if (buddyId == "buddyJS"){
  		ins1 = getBuddy1Instruction();	
  	}
  	return ins1;
  }

  function getBuddy1Instruction(){
    var buddyIns = "null";
    
  	if (SOSAddress != "null" && EOSAddress != "null" && currentStreet != "null") {
  		instructionNotDelivered = false;
  		//var rdir = getRelativeTurnDirection(bCurrentUserOrientationAngle, requiredBearing);
		//var rdirPh = getRelativeDirectionPhrase(rdir);
  		var rdir = getRelativeOrient(requiredBearing,bCurrentUserOrientationAngle);
  		//alert(rdir);
		var rdirPh = getRelativeDirection(rdir);
		landmarksNearEndOfStep = getLandmarksInRange(currentEndOfStep, 20);
		//alert(landmarksNearEndOfStep[0].title);
		if (rdirPh != "none") {
			buddyIns = "turn " + rdirPh + " ";
			buddyIns += "and walk onto " + currentStreet + " "; 
		} else {
			buddyIns = "continue walking on this street "; 			
		}
		if (landmarksNearEndOfStep.length > 0){
			var landmarkId = landmarksNearEndOfStep[0];
			var landmarkTitle = getLandmarkTitle(landmarkId);
			var landmarkType = getLandmarkType(landmarkId);
			buddyIns += "until the ";
			if (landmarkTitle != "null"){
				buddyIns += landmarkTitle ;
			}
			if (landmarkType != "null"){
				buddyIns += " " + landmarkType;
			}
		}
		//buddyIns += " from " + SOSAddress;
		//buddyIns += " to " + EOSAddress;
		buddyIns += ". walk for " + distance2NextNode + " meters"; 
	} else {
		instructionNotDelivered = true;
		buddyIns = "";
	}
	return buddyIns;
  }


  // this function returns the instruction produced by google directions service..
  function getNextGoogleInstruction(){
  	return cleanup_googlemaps_instruction(route.steps[step].instructions);
  }

  /* function presentClosestLandmarks(){
	for(var i=0;i<landmark.length;i++){
		var d = getDistance(currentPosition,landmark[i].location);
		if (d < 0.03) {
			presentIfUnpresented(landmark[i], function(x,status){
				if (status == "ok") {
					speak(x);
				} 
			});
		}		
	}	
  }

  function presentIfUnpresented(x, callback){
	var presented = false;
	for(var k=0;k<presentedLandmarks.length;k++){
		if (x.id == presentedLandmarks[k]) {
			presented = true;
			break;
		}
	}
	if (presented == false) {
		presentedLandmarks.push(x.id);
		callback("you are near " + x.title, "ok");		
	} else {
		callback("", "null");
	}
  } */

  function cleanup_googlemaps_instruction(instruction) {
	var ins2speak = instruction;
	
	ins2speak = ins2speak.replace(/\<b\>/g,"");
	ins2speak = ins2speak.replace(/\<\/b\>/g,"");
	ins2speak = ins2speak.replace(/\<div.*?\>/g,". "); // remove div elements in the google generated instructions
	ins2speak = ins2speak.replace(/\<\/div\>/g,"");
	ins2speak = ins2speak.replace(/\/.*?\//g,"/"); // remove "/A8/" sort of words
	ins2speak = ins2speak.replace(/\/.*? /g," "); // remove "/A8 " sort of words
	ins2speak = ins2speak.replace(/\/.*?\./g," "); // remove "/A8." sort of words
	ins2speak = ins2speak.replace(/St/g, "street");
	ins2speak = ins2speak.replace(/Pl/g, "place");
	ins2speak = ins2speak.replace(/\/.*?$/g," "); // remove "/A8" sort of words at the end of the instruction
	
	return ins2speak;
  }

