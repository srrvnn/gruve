

  
  function speakUsingGoogle(inf){
		if (inf != "" && inf != "null" ){
			var sound_file_url = "http://translate.google.com/translate_tts?tl=en&q="+inf;
			document.getElementById("sound_element").innerHTML= "<embed src='"+sound_file_url+"' hidden=true autostart=true loop=false>";	
		}
  }

  
  
  function speakByCereproc(utt){ 
		//alert("processing:" + utt); 
		if (utt != "" && utt != "null" ){
			ttshttp=GetTTSXmlHttpObject(); // defined in log.js !? 
			 
			if (ttshttp==null) { 
			  alert ("Your browser does not support Ajax HTTP"); 
			  return; 
			} 
			
			//var url= "http://137.195.27.7:13933/"; 
			var url= "http://137.195.27.93:13933/"; 
			//The above is a Cereproc TTS server hosted at HWU
			ttshttp.onreadystatechange = cereprocOutput;  

			//var utt  =  "this is a test for cereproc synthesizer, yes, yes."; 
			var params = "voicename=Heather&inputtxt=" + utt; 
			//var params = "inputtxt=" + utt; 
			url = url + "?" + params; 

			ttshttp.open("POST",url,true); // async request is true 
			//ttshttp.open("POST",url,false); 
			ttshttp.setRequestHeader("Content-type","application/x-www-form-urlencoded"); 
			//ttshttp.setRequestHeader("Content-Length", params.length); 
			ttshttp.send(params); 
		}
  } 
 
  function cereprocOutput(){ 
	  //document.getElementById("debug").innerHTML = "xmlhttp Headers:" + xmlhttp.getAllResponseHeaders(); 
	  // alert("readystate=" + xmlhttp.readyState +", status="+xmlhttp.status); 
	  // If the request's status is "complete" 
	  if (ttshttp.readyState == 4) { 
	   
			// Check that a successful server response was received 
			if (ttshttp.status == 200) { 

				// process and send the received messages to the user(browser) 
				// e.g. outputurl=http://137.195.27.93:13933/Heatherfd0448d80baba93919afbd029118577d.mp3 
				var msg = ttshttp.responseText 
				//document.getElementById("debug").innerHTML = "cereprocOut:" + msg; 
				mp3url = msg.substring(msg.indexOf("=")+1); // after = sign and util end of string 
				playMP3(mp3url);
			} else { 

				// var msg = xmlhttp.responseText 
				// document.getElementById("debug").innerHTML = "cereprocOut:" + msg; 

				// An HTTP problem has occurred 
				//alert("HTTP error: "+ttshttp.status); 
				speakByCereproc(currentSysUtterance);
			} 
 
		} 
  }
  
  function playMP3(file){	
	//alert("playMP3:" + file);
	
	if (!stillPlaying) {
		//var sound_file_url = "http://translate.google.com/translate_tts?tl=en&q="+text;
		$("#jquery_jplayer_1").jPlayer("setMedia", {mp3:file});
		$("#jquery_jplayer_1").jPlayer("play");
	} else {
		yetToPlay = true;
		moreToPlay.push(file);
		//alert("Waiting in queue..");
		document.getElementById("waitQueue").innerHTML = moreToPlay;
	}
  }
  
  function GetTTSXmlHttpObject(){
	if (window.XMLHttpRequest){
		return new XMLHttpRequest();
	}

	if (window.ActiveXObject){
		return new ActiveXObject("Microsoft.XMLHTTP");
	}
	return null;
}