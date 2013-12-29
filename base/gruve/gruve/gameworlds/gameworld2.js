
//objects 



var gotKey1;
var gotKey2;
var showKey2;
var money;
var energy;

function helloGameWorld(){
	alert("Game world loaded");
}

function loadGameWorld(map){

	gotKey1 = false;
	gotKey2 = false;
	showKey2 = false;
	
	money = 20;
	money_withdrawn = 0;

	energy = 100;
	
	player_position = new google.maps.LatLng(55.945722,-3.1870830000000296); 
	player_orientation = 150;
	 
	objects[0] = new google.maps.Marker({map: map, position: new google.maps.LatLng(55.944568820372126, -3.1870695078735025), icon: 'images/scotty.gif'}); //
	objects[1] = new google.maps.Marker({map: map, position: new google.maps.LatLng(55.94978499434283, -3.1910686815147074), icon: 'images/bagpiper2.jpg'}); 
	objects[2] = new google.maps.Marker({map: map, position: new google.maps.LatLng(55.94703205947441, -3.186962219512907), icon: 'images/tchest-closed.gif'}); // 
	objects[3] = new google.maps.Marker({map: map, position: new google.maps.LatLng(55.94476107881733, -3.1849666560058267), icon: 'images/key.gif'}); //
	objects[4] = new google.maps.Marker({map: map, position: new google.maps.LatLng(55.94612338280094, -3.184856685436216), icon: 'images/atm.gif'}); // 
	objects[5] = new google.maps.Marker({map: map, position: new google.maps.LatLng(55.94578243580349, -3.1850739443664224), icon: 'images/burgersmall.gif'}); // 
	objects[6] = new google.maps.Marker({map: map, position: new google.maps.LatLng(55.94265970229938, -3.187761517799345), icon: 'images/dancer.gif'}); // 
	objects[7] = new google.maps.Marker({map: map, position: new google.maps.LatLng(55.94367510647594, -3.185959073341337), icon: 'images/chest.png'}); // 
	objects[8] = new google.maps.Marker({map: map, position: new google.maps.LatLng(55.94280840960467, -3.182960363662687), icon: 'images/atm.gif'}); // 
	objects[9] = new google.maps.Marker({map: map, position: new google.maps.LatLng(55.94556615069049, -3.1869729483489664), icon: 'images/piratesmall.gif'}); 
	//objects[10] = new google.maps.Marker({map: map, position: new google.maps.LatLng(55.949906641613616, -3.190175505912748), icon: new google.maps.MarkerImage('images/pirate.gif')});
	
	objects_ext[0] = {name: 'bagpiper1', visible: true, type: 'bagpiper', location: 'Crichton Street'};
	objects_ext[1] = {name: 'bagpiper2', visible: true, type: 'bagpiper', location: 'null'};
	objects_ext[2] = {name: 'treasure_chest', visible: true, type: 'treasure', location: 'South College Street'};
	objects_ext[3] = {name: 'key1', visible: true, type: 'key', location: 'West Nicolson Street'};
	objects_ext[4] = {name: 'atm1', visible: true, type: 'cash machine', location: 'Nicolson Street'};
	objects_ext[5] = {name: 'restaurant1', visible: true, type: 'restaurant', location: 'Nicolson Street'};
	objects_ext[6] = {name: 'dancer1', visible: true, type: 'dancer', location: 'Buccleuch Place'};
	objects_ext[7] = {name: 'chest1', visible: true, type: 'chest', location: 'Windmill Street'};
	objects_ext[8] = {name: 'atm', visible: true, type: 'cash machine', location: 'South Clerk Street'};
	objects_ext[9] = {name: 'pirate1', visible: true, type: 'pirate', location: 'Potterrow'};
	
	sb[0] = new google.maps.InfoWindow();
	sb[1] = new google.maps.InfoWindow();
	sb[2] = new google.maps.InfoWindow();
	sb[3] = new google.maps.InfoWindow();
	sb[4] = new google.maps.InfoWindow();
	sb[5] = new google.maps.InfoWindow();
	sb[6] = new google.maps.InfoWindow();
	sb[7] = new google.maps.InfoWindow();
	sb[8] = new google.maps.InfoWindow();
	sb[9] = new google.maps.InfoWindow();

	//piper1
	google.maps.event.addListener(objects[0], 'click', function() {
		//log(objects_ext[0].name + ' clicked');
		if (gotKey1 == false){
			sb[0].setContent('I saw a key on West Nicolson street that you may want.');
			sb[0].open(map,objects[0]);
			//log('piper1 clicked before possesing key1');
		} /* else {
			sb[0].setContent('You have your key, I see. Good. The Scottish dancer at Hill Place has the next clue.');
			sb[0].open(map,objects[0]);
			//log('piper1 clicked after possesing key1');
		} */		
        });

	//treasure
	google.maps.event.addListener(objects[2], 'click', function() {
		//log(objects_ext[2].name + ' clicked');
		if (gotKey2 == true){
			objects[2].setIcon('images/treasure.png');
			sb[2].setContent('<div><b>Congratulations!</b></div>');
			sb[2].open(map,objects[2]);
			//log('treasure opened');
			game_over = true;
		} else {
			var newPosition = new google.maps.LatLng(currentUserPosition.lat() + Math.cos(toRadian(currentHeading)) * 0.0001, currentUserPosition.lng() + Math.sin(toRadian(currentHeading)) * 0.0001);
			objects[9].setPosition(newPosition);
			sb[9].setContent('Looks like the treasure chest. But now, we need to find the key to open it.');
			sb[9].open(map,objects[9]);
			objects[9].setVisible(true);
			//log('tried to open chest without key');
		}
		
        });
	
	//key1 
	google.maps.event.addListener(objects[3], 'click', function() {
		//log(objects_ext[3].name + ' clicked');
		objects[3].setVisible(false);
		objects_ext[3].visible = false;
		gotKey1 = true;
		var newPosition = new google.maps.LatLng(currentUserPosition.lat() + Math.cos(toRadian(currentHeading)) * 0.0001, currentUserPosition.lng() + Math.sin(toRadian(currentHeading)) * 0.0001);
		objects[9].setPosition(newPosition);
		sb[9].setContent('Excellent!.. Lets go look for the chest now..');
		sb[9].open(map,objects[9]);
		objects[9].setVisible(true);
		refreshPanel();
        });

	//atm
	google.maps.event.addListener(objects[4], 'click', function() {
		//log(objects_ext[4].name + ' clicked');
		atm_clicked(4);
		refreshPanel();
        });

	//restaurant
	google.maps.event.addListener(objects[5], 'click', function() {
		//log(objects_ext[5].name + ' clicked');
		restaurant_clicked(5);
		refreshPanel();
        });
        
        //dancer
	google.maps.event.addListener(objects[6], 'click', function() {
		//log(objects_ext[6].name + ' clicked');
		//if (money > 10) {
		//	money -= 10;
			sb[6].setContent('I saw a chest on Windmill Street. Is that the one you are looking for?');
			sb[6].open(map,objects[6]);
		//} else {
		//	sb[6].setContent('I have some information for you. Do you have 10 quids?');
		//	sb[6].open(map,objects[6]);
		//}
		refreshPanel();
        });

	//chest on chambers street
        google.maps.event.addListener(objects[7], 'click', function() {
		//log(objects_ext[7].name + ' clicked');
		if (gotKey1 == true) {
			if (showKey2 == true){
				objects_ext[7].visible = false;
				objects[7].setVisible(false);
				gotKey2 = true;
				showKey2 = false;
				var newPosition = new google.maps.LatLng(currentUserPosition.lat() + Math.cos(toRadian(currentHeading)) * 0.0001, currentUserPosition.lng() + Math.sin(toRadian(currentHeading)) * 0.0001);
				objects[9].setPosition(newPosition);
				sb[9].setContent('Is this the key to the treasure chest? Lets find it now.');
				sb[9].open(map,objects[9]);
				objects[9].setVisible(true);
				//sb[7].setContent('Good. You have the key to the treasure. Head to South College street.');
				//sb[7].open(map,objects[7]);
			} else {
				objects[7].setIcon('images/key3.png');
				showKey2 = true;
			}
		} else {
			var newPosition = new google.maps.LatLng(currentUserPosition.lat() + Math.cos(toRadian(currentHeading)) * 0.0001, currentUserPosition.lng() + Math.sin(toRadian(currentHeading)) * 0.0001);
			objects[9].setPosition(newPosition);
			sb[9].setContent('Oh no! The chest is locked. We need to find its key somehow.');
			sb[9].open(map,objects[9]);
		}
		refreshPanel();
        });

        //atm on High street
	google.maps.event.addListener(objects[8], 'click', function() {
		//log(objects_ext[8].name + ' clicked');
		atm_clicked(8);
		refreshPanel();
        });

	//pirate
	google.maps.event.addListener(objects[9], 'click', function() {
		//log(objects_ext[9].name + ' clicked');
		sb[9].setContent('<div>Welcome to <b> GRUVE Treasure Hunt Game </b>. Please help me find the treasure. Your buddy knows some locations where we can find clues to finding the treasure. Where should we try first?</div>');
		sb[9].open(map,objects[9]);
		refreshPanel();
        });

	//setting visibility
	var z;
	for(z=0;z<objects.length;z++){
		objects[z].setVisible(false);
	}

	map.setPosition(player_position);
	map.setPov({heading: player_orientation, zoom:1, pitch:0});
	//alert("game created");	
}  

//alert("Game world loaded");
