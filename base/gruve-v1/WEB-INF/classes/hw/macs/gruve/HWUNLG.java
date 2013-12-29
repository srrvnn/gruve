package hw.macs.gruve;
import hw.macs.gruve.Configuration;

import org.json.simple.JSONObject;
import java.util.Iterator;

import java.io.BufferedWriter;
import java.io.FileWriter;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.lang.Exception;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.lang.Math;

public class HWUNLG implements RG{

	HWCityModel cm;
	UserModel um;	
	
	public HWUNLG(UserModel userModel, HWCityModel cityModel){
		this.cm = cityModel;
		this.um = userModel;
	}
	
	public String presentRoute(JSONObject da){

		System.out.println("     ---------------");
		System.out.println("Presenting Route Instructions");
		
		Route route = (Route) da.get("route");
		Integer index = (Integer) da.get("index");
		Double co = (Double) da.get("currentUserOrientation");
		Position currentUserCoor = new Position((String) da.get("currentUserCoor"));
		
		System.out.println("Index: " + index);
		System.out.println("Route: "); route.displayRoute();	

		System.out.println("User Coordinates: " + currentUserCoor.toString());

		Iterator<RouteElement> ire = route.getNodes().iterator();
		String wayName = route.getNodes().get(0).getWayName();
		String sysUtt = "";

		StringBuilder sb = new StringBuilder();

		while (ire.hasNext()){
			RouteElement re = ire.next();			
			if (re.getIndex() == index - 1){
				wayName = re.getWayName();	
			}
			if (re.getIndex() == index){
				Position nextCoor = cm.getCoordinates(re.getNodeId());
				Double turnAngle = Tools.getRelativeOrient(currentUserCoor, nextCoor, co);

				System.out.println("Target Coordinates: "+ nextCoor.toString());
				System.out.println("Turn Angle:" + turnAngle);									
				String relPosition = Tools.getRelativePosition(turnAngle);

				double d = Math.round(route.distance * 100.0) / 100.0;
				int blocks = route.rel.size() - 1;

				if(index == 1){

					if(blocks>1) sb.append("We are <b>" + blocks + " blocks (node) </b> from our destination. <br/>");
					if(blocks==1) sb.append("We are <b> 1 block (node) </b> from our destination. <br/>");
				}					

				sb.append(tellDirection(turnAngle));
				sb.append(tellAction(turnAngle));
				sb.append(tellPathName(wayName));				
			}
		}

		sysUtt = sb.toString();
		return sysUtt;
	}


	private String tellDirection(Double turnAngle){

		if(turnAngle < 0.00) turnAngle += 360.00;

		ArrayList<String> directions =  new ArrayList<String>();
		directions.add("Head straight");
		directions.add("Head right"); directions.add("Head right");
		directions.add("Turn right"); directions.add("Turn right"); directions.add("Turn right");
		directions.add("Turn around"); directions.add("Turn around"); directions.add("Turn around");
		directions.add("Turn around"); directions.add("Turn around"); directions.add("Turn around");
		directions.add("Turn left"); directions.add("Turn left"); directions.add("Turn left");
		directions.add("Head left"); directions.add("Head left");
		directions.add("Head straight");

		return directions.get((int)(turnAngle/20)) + ", ";		
	}

	private String tellAction(Double turnAngle){

		
		if(turnAngle < 0.00) turnAngle += 360.00;

		ArrayList<String> actions =  new ArrayList<String>();
		actions.add("continue on");
		actions.add("continue on"); actions.add("continue on");
		actions.add("walk onto"); actions.add("walk onto"); actions.add("walk onto");
		actions.add("continue on"); actions.add("continue on"); actions.add("continue on");
		actions.add("continue on"); actions.add("continue on"); actions.add("continue on");
		actions.add("walk onto"); actions.add("walk onto"); actions.add("walk onto");
		actions.add("continue on"); actions.add("continue on");
		actions.add("continue on");

		return actions.get((int)(turnAngle/20)) + ": ";
	}

	private String tellPathName(String wayName){

		return wayName + ".";
	}

}