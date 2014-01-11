package hw.macs.gruve;

import org.json.simple.JSONObject;
import java.util.Iterator;

public class HWUNLG implements RG{
	HWCityModel cm;
	UserModel um;
	
	public HWUNLG(UserModel userModel, HWCityModel cityModel){
		this.cm = cityModel;
		this.um = userModel;
	}
	
	public String presentRoute(JSONObject da){
		Route route = (Route) da.get("route");
		Integer index = (Integer) da.get("index");
		Double co = (Double) da.get("currentUserOrientation");
		Position currentUserCoor = new Position((String) da.get("currentUserCoor"));
		
		System.out.println("Route exists: ");
		route.displayRoute();
		System.out.println("Index: " + index);
		Iterator<RouteElement> ire = route.getNodes().iterator();
		String wayName = "null";
		String sysUtt = "";
		while (ire.hasNext()){
			RouteElement re = ire.next();
			if (re.getIndex() == index - 1){
				wayName = re.getWayName();	
			}
			if (re.getIndex() == index){
				Position nextCoor = cm.getCoordinates(re.getNodeId());
				Double turnAngle = Tools.getRelativeOrient(currentUserCoor, nextCoor, co);
				System.out.println("Turn ANGLE:" + turnAngle);	
								
				String relPosition = Tools.getRelativePosition(turnAngle);
				if (relPosition.equals("in front")){
					sysUtt = "Continue walking onto " + wayName;
				} else if (relPosition.equals("before right")){
					sysUtt = "Turn slightly right  onto " + wayName;
				} else if (relPosition.equals("alongside right")){
					sysUtt = "Turn right onto " + wayName;
				} else if (relPosition.equals("behind right")){
					sysUtt = "Turn around to your right and walk onto " + wayName;
				} else if (relPosition.equals("before left")){
					sysUtt = "Turn slightly left  onto " + wayName;
				} else if (relPosition.equals("alongside left")){
					sysUtt = "Turn left onto " + wayName;
				} else if (relPosition.equals("behind left")){
					sysUtt = "Turn around to your left and walk onto " + wayName;
				} else if (relPosition.equals("behind")){
					sysUtt = "Turn around and walk onto " + wayName;
				} 										
			}
		}
		return sysUtt;
	}

}