package hw.macs.gruve;

import java.util.*;
import java.io.File;

public class Route {
	Double distance;
	ArrayList<RouteElement> rel;
	
	Integer i;
	public static final String fsep = System.getProperty("file.separator");
	
	public static void main (String[] args){
		
	
	}
	
	Route(){
		i = 0;
		rel = new ArrayList<RouteElement>();
		distance = 0.0;
	}
	
	public void displayRoute(){
		System.out.println("Displaying route");
		System.out.println("Distance :" + distance);
		Iterator<RouteElement> ire = rel.iterator();
		while(ire.hasNext()){
			RouteElement re = ire.next();
			System.out.println(re.index + "," + re.nodeId + "," + re.edgeId + "," + re.wayName);
		}
	}
	
	public void add(String node, String nextNode){
		String currentDir = ".."+ fsep + "webapps" + fsep + "gruve" + fsep + "WEB-INF" + fsep;
		String classesDir = currentDir + "classes" + fsep;
		HWCityModel cm = new HWCityModel(new File(classesDir + "mymap.osm"));
		
		RouteElement re = new RouteElement();
		re.index = i;
		re.nodeId = node;
		if (!nextNode.equals("null")){
			re.edgeId = cm.getWayId(node, nextNode);
			re.wayName = cm.getWayName(re.edgeId);
			re.distance = cm.getDistance(node, nextNode);
			distance += re.distance;
		} else {
			re.edgeId = "null";
			re.wayName = "null";
			re.distance = 0.0;
		}
		rel.add(re);	
		i++;
	}
	
	public ArrayList<RouteElement> getNodes(){
		return rel;
	}
}