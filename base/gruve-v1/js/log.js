var xmlhttp5;
var logFile = "abc.txt";
var sessionId; 

function createLogFile(){
	//creating a new log file with date + random number..
	var d=new Date();
	d = (d + "").replace(/:/g,"-");
	d = d.replace(/\sGMT.*/,"");
	d = d.replace(/\s/g,"-");
	
	var r = Math.random();
	r = "" + r;
	r = r.replace(/0\./,"");
	sessionId = d+"-"+r;
	logFile = sessionId + ".txt";
	//alert("logFile: " +logFile);		
}

// dummy log() when the webpage is not actually served by a webserver
function log(f){}

function log(f){
	if (logAll == true){
		xmlhttp5=GetXmlHttpObject();
		
		if (xmlhttp5==null)
		{
			alert ("Your browser does not support Ajax HTTP");
			return;
		}
		
		var d=new Date();
		d = (d + "").replace(/:/g,"-");
		d = d.replace(/\sGMT.*/,"");
		d = d.replace(/\s/g,"-");
		//var url="writejsp.jsp?q="+f.user.value;
		var url="gruvelogwriter.jsp?fn="+logFile+"&q="+d+":::"+f;
		xmlhttp5.onreadystatechange = getOutput;
		xmlhttp5.open("POST",url,true);
		xmlhttp5.send(null);
	}
}

function getOutput(){
	if (xmlhttp5.readyState==4){		
	}
}

function GetXmlHttpObject(){
	if (window.XMLHttpRequest){
		return new XMLHttpRequest();
	}

	if (window.ActiveXObject){
		return new ActiveXObject("Microsoft.XMLHTTP");
	}
	return null;
}