


	function loadJavaScript(filename){
		alert("Loading.. " + filename);
		if (document.createElement && document.getElementsByTagName) {
			var fileref=document.createElement('script');
			fileref.setAttribute('type','text/javascript');
			fileref.setAttribute('src', filename);
			//if (typeof fileref != "undefined"){
				document.getElementsByTagName('head')[0].appendChild(fileref);
				alert("File loaded");
				//helloGameWorld();
			//}
		} else {
			alert("unsupported browser");
		}
		
	}