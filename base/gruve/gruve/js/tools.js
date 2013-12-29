



function sleep(ms)
	{
		var dt = new Date();
		dt.setTime(dt.getTime() + ms);
		while (new Date().getTime() < dt.getTime());
	}
	
function toRadian(x) {
	var rad = x * (3.14/180);
	return rad;
}

function toDegree(x) {
	var d = x * (180/3.14);
	return d;
}

function getDistance(p1,p2){
	var d = 0;
	var lat1 = p1.lat();
	var lat2 = p2.lat();
	var lon1 = p1.lng();
	var lon2 = p2.lng();

	// following code is from http://www.movable-type.co.uk/scripts/latlong.html

	var r = 6371; // km
	var dLat = toRadian(lat2-lat1);
	var dLon = toRadian(lon2-lon1);
	lat1 = toRadian(lat1);
	lat2 = toRadian(lat2);

	var a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2); 
	var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
	d = r * c;
	d = Math.round(d * 1000); // converting to meters
	return d;	
}


function getBearing(p1,p2){

	var lat1 = p1.lat();
	var lat2 = p2.lat();
	var lon1 = p1.lng();
	var lon2 = p2.lng();

	var dLat = toRadian(lat2-lat1);
	var dLon = toRadian(lon2-lon1);
	lat1 = toRadian(lat1);
	lat2 = toRadian(lat2);

	// following code is from http://www.movable-type.co.uk/scripts/latlong.html
	var y = Math.sin(dLon) * Math.cos(lat2);
	var x = Math.cos(lat1)*Math.sin(lat2) - Math.sin(lat1)*Math.cos(lat2)*Math.cos(dLon);
	var orient360 = toDegree(Math.atan2(y, x));
	/* if (orient360 >= 0){
		if (lat1 > lat2 || lon1 > lon2){
			orient360 += 180;
		}
	} else {// if orient is negative
		if (lat1 < lat2){				
			orient360 += 180; 
		} else {
			orient360 += 360;
		}
	} */
	
	orient360 = (orient360 + 360) % 360;
	return orient360;
}

/*
function getRelativeTurnDirection(userBearing, reqBearing){
	var diff = reqBearing - userBearing;
	if (diff > 180) {diff = 360 - diff;}
	return diff;
	
}
*/

function getRelativeDirection(nextUserOrientDel){
	var direction = "";
	if (nextUserOrientDel < 30) {
		//direction = "slightly left";
		direction = "none";
	} else if (nextUserOrientDel >= 30 && nextUserOrientDel < 110) {
		direction = "left";
	} else if (nextUserOrientDel >= 110 && nextUserOrientDel < 160) {
		direction = "around on your left";
	} else if (nextUserOrientDel >= 160 && nextUserOrientDel < 200) {
		direction = "around";
	} else if (nextUserOrientDel >= 200 && nextUserOrientDel < 250) {
		direction = "around on your right";
	} else if (nextUserOrientDel >= 250 && nextUserOrientDel < 330) {
		direction = "right";
	} else if (nextUserOrientDel >= 330){
		//direction = "slightly right";
		direction = "none";
	}						
	return direction;
}

	
function getRelativeOrient(rorient,  corient){
	//rorient is require orientation
	//corient is current orientation
	var relOrient = orientRoundup(getOrientDel(rorient, corient));
	return relOrient;
}



function orientRoundup(testOrient2){
	if (testOrient2 < 0) {testOrient2 += 360;}
	if (testOrient2 > 360) {testOrient2 -= 360;}
	return testOrient2;
}

function getOrientDel(nextOrient,  currentOrient){
	var orientDel = nextOrient - currentOrient;
	if (orientDel < -180){
		orientDel += 360;
	} else if (orientDel > 180){ //if the user has to turn more than 180 on his left
		orientDel = -(360 - orientDel); // he can turn less on his right 
	}
	return orientDel;
}
	
//getNextPoint(p1,d) gives the coordinates of the next point at d km in the bearing brng
function getNextPoint(p1, d, brng){
	var lat1 = toRadian(p1.lat());
	var lon1 = toRadian(p1.lng());
	brng = toRadian(brng);
	var r = 6371; // km
	var lat2 = Math.asin(Math.sin(lat1)*Math.cos(d/r) + Math.cos(lat1)*Math.sin(d/r)*Math.cos(brng) );
	var lon2 = lon1 + Math.atan2(Math.sin(brng) * Math.sin(d/r) * Math.cos(lat1), Math.cos(d/r) - Math.sin(lat1) * Math.sin(lat2));
	lon2 = (lon2+3*Math.PI) % (2*Math.PI) - Math.PI;
	lon2 = toDegree(lon2);
	lat2 = toDegree(lat2);
	/* var lat2 = lat1 + d/r * Math.cos(brng);
	var lon2 = lon1 - d/r * Math.sin(brng);
	*/
	return new google.maps.LatLng(lat2, lon2);
}

function getMidpoint(node1, node2){
	var midLat = (node1.lat() + node2.lat()) / 2;
	var midLng = (node1.lng() + node2.lng()) / 2;
	return new google.maps.LatLng(midLat, midLng);		
}

function getStreetName(type, node1){
	var geocoder = new google.maps.Geocoder();
	geocoder.geocode({'latLng': node1}, function(results, status) {
	if (status == google.maps.GeocoderStatus.OK) {
	        if (results[0]) {
	         	var temp;  
				var max = results[0].address_components.length;
				var i = 0;
				while (i < max){
					if (results[0].address_components[i].types.toString().search("route") != -1){
						temp = results[0].address_components[i].long_name;
						break;
					}
					i++;
				}	
				// replace all abbreviations in street names.. e.g Pl to Place
				if (temp.search(/.Pl$/) != -1) {
					temp = temp.replace("Pl", "Place");
				} 
				if (temp.search(/.St$/) != -1) {
					temp = temp.replace("St", "Street");
				} 
				if (temp.search(/^W\s/) != -1) {
					temp = temp.replace("W", "West");
				} 
				if (temp.search(/^E\s/) != -1) {
					temp = temp.replace("E", "East");
				}
				
				//assign streetname to appropriate variable
				if (type == "startOfStep"){
					SOSAddress = temp;	
				} else if (type == "endOfStep"){
					EOSAddress = temp;
				} else if (type == "midPoint"){
					currentStreet = temp;
					//alert(currentStreet);
				}
	        }
	      } else {
				if (type == "startOfStep"){
					SOSAddress = "NONE";	
				} else if (type == "endOfStep"){
					EOSAddress = "NONE";
				} else if (type == "midPoint"){
					currentStreet = "NONE";
					//alert(currentStreet);
				}
				//alert("Geocoder failed due to: " + status);
	      }
	});
}

