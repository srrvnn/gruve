

	function tellSBServlet(buddy, da,utt,pos,email){
		var xhr = new XMLHttpRequest();
		xhr.onreadystatechange = function() {
			if (xhr.readyState == 4) {
				var data = xhr.responseText;
				instruction = data;
				speak_instruction();
			}
		}
		if (buddy == "system"){
			xhr.open('GET', 'GruveServlet?sessionId='+sessionId+'&userDA='+da+'&userUtterance='+utt+'&userPosition='+pos+'&userEmail='+email, true);
		} else {
			xhr.open('GET', 'GruveWizServlet?sessionId='+sessionId+'&userType='+userType+'&userDA='+da+'&userUtterance='+utt+'&userPosition='+pos+'&userEmail='+email, true);		
		}
		xhr.send(null);
	}
	
