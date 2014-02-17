package hw.macs.gruve;

import java.util.ArrayList;
import java.io.File;
import java.util.Iterator;
import org.json.simple.JSONObject;

import java.util.Random;

import java.lang.Exception;

class NLG {

	HWCityModel cm;
	UserModel um;
	RG rg;

	boolean presentingRoute; 
	
	public static void main(String[] arg) {
	}
	
	NLG(UserModel um, HWCityModel cm) {

		this.cm = cm;
		this.um = um;
		rg = new HWUNLG(um,cm);

		presentingRoute = false;
	}

	public String realize(JSONObject da) {
		
		String cf = (String) da.get("cf");
		String sysUtt = "";
		Position currentUserCoor;
		Double currentUserOrient;

		if (cf.equals("presentRoute")) {

			sysUtt = "* NOW: * " + rg.presentRoute(da);
			presentingRoute = true;

		} else if (cf.equals("null")) {

			if(presentingRoute) {

				String[] smalltalk = { "We are getting there. Head Straight", 
				"I will update your next move. Until then, keep going.", 
				"Keep moving. In your buddy you must trust.", 
				"Head Straight. Use the Which Direction now? button if you are need help.",
				"Your next update with start with: * NOW *. ",
				"" };

				Random r = new Random();
				int c = r.nextInt(4);

				try {

					sysUtt = smalltalk[c];

				} catch (Exception e) {

					sysUtt = "You are are doing good. Keep going.";	
				}			

			} else {

				sysUtt = "";
			}

		} else if (cf.equals("greetUser")) {

			sysUtt = "Hi. I am here to help you with directions in this game.";

		} else if (cf.equals("startWalking")) {

			sysUtt = "Let us get started. Have you met the Pirate yet?";

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
			sysUtt = "Hurray!! We have reached " + destination;
			presentingRoute = false;

		} else if (cf.equals("noRouteFound")) {

			sysUtt = "Sorry. I could not find the destination.";
		} 		

		System.out.println("System says: " + sysUtt);
		
		return sysUtt;
	}
	
}