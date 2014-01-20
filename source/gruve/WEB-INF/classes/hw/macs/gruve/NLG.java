package hw.macs.gruve;

import java.util.ArrayList;
import java.io.File;
import java.util.Iterator;
import org.json.simple.JSONObject;

import java.lang.Exception;

class NLG {

	HWCityModel cm;
	UserModel um;
	RG rg;
	
	public static void main(String[] arg) {
	}
	
	NLG(UserModel um, HWCityModel cm) {

		this.cm = cm;
		this.um = um;
		rg = new HWUNLG(um,cm);
	}

	public String realize(JSONObject da) {
		
		String cf = (String) da.get("cf");
		String sysUtt = "";
		Position currentUserCoor;
		Double currentUserOrient;

		if (cf.equals("presentRoute")) {

			sysUtt = rg.presentRoute(da);

		} else if (cf.equals("null")) {

			sysUtt = "";

		} else if (cf.equals("greetUser")) {

			sysUtt = "Hi. I am here to help you with directions in this game.";

		} else if (cf.equals("startWalking")) {

			sysUtt = "Let's get started. Move ahead, so that I know you are there.";

		} else if (cf.equals("introduceSelf")) {

			sysUtt = "Here is how we work: choose a street, and click on Send. I can help you get there.";

		} else if (cf.equals("acknowledge")) {

			sysUtt = "Alright.";

		} else if (cf.equals("acknowledgeRouteRequest")) {

			String dest = (String) da.get("entityName");
			sysUtt = "Alright. Finding directions to " + dest + ". One moment please...";								

		} else if (cf.equals("acceptThanks")) {

			sysUtt = "You are welcome!";

		} else if (cf.equals("destinationReached")) {

			String destination = (String) da.get("entityName");
			sysUtt = "We have reached " + destination;

		} else if (cf.equals("noRouteFound")) {

			sysUtt = "Sorry. I could not find the destination.";
		} 		

		System.out.println("System says: " + sysUtt);
		
		return sysUtt;
	}
	
}