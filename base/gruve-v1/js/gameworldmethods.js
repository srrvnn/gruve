

var objects;
var objects_ext;
var game_over;
var game_lost;
var obj_list, loc_list;
var player_position, player_orientation;

function initiateGameworld(map){
	city = "Edinburgh";
	objects = new Array();
	objects_ext = new Array();
	sb = new Array(); // array of speech balloons for each object to interact with the user

	obj_list = new Array();
	loc_list = new Array();

	game_over = false;
	game_lost = false;
	loadGameWorld(map);
}

// populate the worlds locations on the dropdown list..
function populateGameWorldLocations(d){
	var pos;
	var geocoder = new google.maps.Geocoder();
	for(o=0; o<objects.length; o++){
		if (objects_ext[o].location == 'null'){
			// do nothing
		} else {
			if (loc_list.indexOf(objects_ext[o].location) == -1) {
				var op = document.createElement("option");
				op.text = objects_ext[o].location;
				loc_list.push(objects_ext[o].location);
				try{
					d.add(op, null);
				} catch (e) {
				}
			} 
		}		
	}
}

function populateGameWorldObjects(d){
	var pos;
	for(o=0; o<objects.length; o++){
		if (objects_ext[o].type == 'null' || objects_ext[o].type == 'treasure' || objects_ext[o].type == 'chest' || objects_ext[o].type == 'key'){
			// do nothing
		} else {
			if (obj_list.indexOf(objects_ext[o].type) == -1) {
				var op = document.createElement("option");
				op.text = objects_ext[o].type;
				obj_list.push(objects_ext[o].type);
				try{
					d.add(op, null);
				} catch (e) {
				}
			}
		}		
	}
}

function createNewGameObject(lat,lon,img,type){
	objects[nobjects] = new google.maps.Marker({map: map, position: new google.maps.LatLng(lat, lon), icon: img});
	objects_ext[nobjects] = {visible: true, type:type};
	sb[nobjects] = new google.maps.InfoWindow();
}



function refreshPanel(){
	var keys_in_hand = '';
	if (gotKey1 == true){ keys_in_hand += ' key1';}
	if (gotKey2 == true){ keys_in_hand += ' key2';}
	
	document.getElementById("panel").innerHTML = "Money: " + money + " || Energy: " + energy + " || Keys:" + keys_in_hand ;
}

function restaurant_clicked(index){
	if (energy < 100 && money > 1) {
		energy += 10;
		money--;
		playSound("sound/eating.mp3");
		sb[index].setContent('Click for more!');
		sb[index].open(map,objects[5]);
	} else if (money < 1) {
		sb[index].setContent('Looks like you have no money to buy food.');
		sb[index].open(map,objects[5]);
	} else {
		sb[index].setContent('You cant eat more than this.');
		sb[index].open(map,objects[5]);
	}
	refreshPanel();
}

function atm_clicked(index) {
	if (money_withdrawn < 200) {
		money += 10;
		money_withdrawn += 10;
		sb[index].setContent('Click for more!');
		sb[index].open(map,objects[8]);
	} else {
		sb[index].setContent('Your daily allowance has reached. Sorry you cannot withdraw more.');
		sb[index].open(map,objects[8]);
	}
	refreshPanel();
}

function playSound(url){
	document.getElementById("sound_element").innerHTML = "<embed src='"+url+"' hidden=true autostart=true loop=false>";;
}

function gamePosChanged(){
	if (energy > 0){
		//energy--;
	} 

	if (energy == 0) {
		//alert(currentUserPosition);
		var newPosition = new google.maps.LatLng(currentUserPosition.lat() + Math.cos(toRadian(currentHeading)) * 0.0001, currentUserPosition.lng() + Math.sin(toRadian(currentHeading)) * 0.0001);
		//alert(newPosition);
		objects[9].setPosition(newPosition);
		sb[9].setContent('Ha Ha.. You lost all your energy. You have lost the game now!');
		sb[9].open(map,objects[9]);
		objects[9].setVisible(true);
		game_lost = true;
		game_over = true;
		document.getElementById("panel").innerHTML = "GAME LOST! DUE TO ZERO ENERGY :(" ;
		return;
	} 

	showClosestObjects();
	closeAllSBs();	

	//getting robbed
	if (Math.random() < 0.01){
		money -= 20;
		if (money < 0) { money = 0; }
	}	
	
	refreshPanel();
}

function showClosestObjects(){
	var i;
	for(i=0;i < objects.length;i++){
		var d = getDistance(currentUserPosition,objects[i].position);
		//show objects that are less than 30 meters away from the user
		if (d < 30 && objects_ext[i].visible == true) {
			objects[i].setVisible(true);
		} else {
			objects[i].setVisible(false);
		}		
	}	
}

//close al speech baloons
function closeAllSBs(){
	var i;
	for(i=0;i < objects.length;i++){
		sb[i].close();	
	}	
}

function getClosestEntityLocation(x){
	var o, d, min_index;
	var min = 10000;
	min_index = -1;
	for(o=0; o<objects.length; o++){
		if (objects_ext[o].type == x){
			d = getDistance(currentUserPosition,objects[o].position);
			if (d < min) {
				min = d;
				min_index = o;
			}	
		}
	}
	var enloc = "null";
	if (min_index != -1) {
		enloc = objects_ext[min_index].location;
	}
	return enloc;
}
