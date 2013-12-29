
var city;
var landmark = new Array(); // the values of the array will be populated in landmarks.js
//e.g. landmark[0] = {location: new google.maps.LatLng(55.94536617365653,-3.191478368234584), id: 'l0', title: 'RBS', type: 'bank'};



function getLandmarksInRange(pos, dis){
	var i;
	var cl = new Array();
	for(i=0;i < landmark.length;i++){
		var d = getDistance(pos,landmark[i].location);
		//show objects that are less than dis meters away from the point
		if (d < dis) {
			cl.push(landmark[i].id);
		} 		
	}
	return cl;
}

function getLandmarkTitle(id){
	var i;
	for(i=0;i < landmark.length;i++){
		if (landmark[i].id == id) {
			return landmark[i].title;
		} 		
	}
	return "null";
}

function getLandmarkType(id){
	var i;
	for(i=0;i < landmark.length;i++){
		if (landmark[i].id == id) {
			return landmark[i].type;
		} 		
	}
	return "null";
}