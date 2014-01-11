package hw.macs.gruve;


import java.util.ArrayList;
import java.io.File;
import java.util.Iterator;
import org.json.simple.JSONObject;

class NLG{
	HWCityModel cm;
	UserModel um;
	RG rg;
	
	public static void main(String[] arg){
	}
	
	NLG(UserModel um, HWCityModel cm){
		this.cm = cm;
		this.um = um;
		rg = new HWUNLG(um,cm);
	}
	
	/*
	* Generate NL utterance for a given DA from the dialogue manager
	*/
	public String realize(JSONObject da){
		
		String cf = (String) da.get("cf");
		String sysUtt = "";
		Position currentUserCoor;
		Double currentUserOrient;
				
		if (cf.equals("null")){
			sysUtt = "";
		} 
		else if (cf.equals("greetUser")){
			sysUtt = "Hi. I am your buddy. I can help you with directions.";
		}
		else if (cf.equals("startWalking")){
			sysUtt = "I need your orientation. Please start walking.";
		}		
		else if (cf.equals("introduceSelf")){
			sysUtt = "I can help you with directions. Select a street you want to go to and click the send button. You might find clues that you are looking for when you reach the street.";
		}
		else if (cf.equals("acknowledge")){
			sysUtt = "ok";
		} 
		else if (cf.equals("acknowledgeRouteRequest")){
			String dest = (String) da.get("entityName");
			sysUtt = "ok. finding directions to " + dest;
		} 
		else if (cf.equals("acceptThanks")){
			sysUtt = "you are welcome";
		} 
		else if (cf.equals("destinationReached")){
			String destination = (String) da.get("entityName");
			sysUtt = "This is " + destination;
		}
		else if (cf.equals("noRouteFound")){
			sysUtt = "Sorry. I could not find the destination.";
		} 
		else if (cf.equals("presentRoute")){
			sysUtt = rg.presentRoute(da);
		}
		
		return sysUtt;
	}
	
}