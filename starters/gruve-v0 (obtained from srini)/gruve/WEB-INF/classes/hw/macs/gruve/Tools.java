package hw.macs.gruve;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


public class Tools{

	public static void main(String[] arg){
		
	}
	
	
	/**
	* Get the relative position of an object based on its relative orientation
	* e.g. An object is on the "alongside right" if the relative orientation is between 60 and 120 degrees
	*/
	public static String getRelativePosition(double nextUserOrientDel){
		String direction = "";
		if (nextUserOrientDel > 0 && nextUserOrientDel < 20) {
			direction = "in front"; //on the right
		} else if (nextUserOrientDel >= 20 && nextUserOrientDel < 60) {
			direction = "before right";
		} else if (nextUserOrientDel >= 60 && nextUserOrientDel < 120) {
			direction = "alongside right";
		} else if (nextUserOrientDel >= 120 && nextUserOrientDel < 160) {
			direction = "behind right";
		} else if (nextUserOrientDel >= 160 && nextUserOrientDel <= 180){ 
			direction = "behind";
		} else if (nextUserOrientDel < 0 && nextUserOrientDel > -20) {
			direction = "in front"; //on the left
		} else if (nextUserOrientDel <= -20 && nextUserOrientDel > -60) {
			direction = "before left";
		} else if (nextUserOrientDel <= -60 && nextUserOrientDel > -120) {
			direction = "alongside left";
		} else if (nextUserOrientDel <= -120 && nextUserOrientDel > -160) {
			direction = "behind left";
		} else if (nextUserOrientDel <= -160 && nextUserOrientDel >= -180){ 
			direction = "behind";
		} 					
		return direction;
	}

	/** 
	* Get bearing between two position objects
	*/
	public static double getOrient(Position p1, Position p2){
		double lat1 = p1.getLat();
		double lon1 = p1.getLon();
		double lat2 = p2.getLat();
		double lon2 = p2.getLon();
		
		double dLat = Math.toRadians(lat2-lat1);
		double dLon = Math.toRadians(lon2-lon1);
		lat1 = Math.toRadians(lat1);
		lat2 = Math.toRadians(lat2);

		// following code is from http://www.movable-type.co.uk/scripts/latlong.html
		double y = Math.sin(dLon) * Math.cos(lat2);
		double x = Math.cos(lat1)*Math.sin(lat2) - Math.sin(lat1)*Math.cos(lat2)*Math.cos(dLon);
		double orient = Math.toDegrees(Math.atan2(y, x));
		//converting orient from a scale of -180 .. + 180 to 0-359 degrees
		double orient360 = (orient + 360) % 360;
		return orient360;
	} 
	
	/** 
	* Round up orientation to be between 0 and 359 degrees
	*/
	public static double orientRoundup(double testOrient2){
		if (testOrient2 < 0) {testOrient2 += 360;}
		if (testOrient2 > 360) {testOrient2 -= 360;}
		return testOrient2;
	}
	
	/** 
	* Get the difference between two bearings
	*/
	public static double getOrientDel(double currentOrient, double nextOrient){
	    return (currentOrient + 180 -  nextOrient) % 360 - 180;
	} 

	/** 
	* Get the relative bearing between two positions with respect to another bearing corient
	*/
	public static double getRelativeOrient(Position oldPos, Position newPos, double corient){
		double orient360 = getOrient(oldPos, newPos);
		double relOrient = getOrientDel(orient360, corient);
		return relOrient;
	}

	/** 
	* Get the distance between two position objects
	* Implementation of algorithm in http://www.movable-type.co.uk/scripts/latlong.html
	*/
	public static double distance(Position p1, Position p2){
		
		double lat1 = p1.getLat(); 
		double lon1 = p1.getLon();
		double lat2 = p2.getLat(); 
		double lon2 = p2.getLon();
		
		double r = 6371; // km
		double dLat = Math.toRadians(lat2-lat1);
		double dLon = Math.toRadians(lon2-lon1);
		double nlat1 = Math.toRadians(lat1);
		double nlat2 = Math.toRadians(lat2);

		double a = Math.sin(dLat/2) * Math.sin(dLat/2) + 
		        Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(nlat1) * Math.cos(nlat2); 
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
		double d = r * c;
		return d;
		
	}
	
	/** 
	* Get the next position from position p in direction orient for a distance distance
	*/
	public static Position getNextCoordinate(Position p, double distance, double orient){
		distance = distance/1000;
		double lat1 = p.getLat();
		double lon1 = p.getLon();
		lat1 = Math.toRadians(lat1);
		lon1 = Math.toRadians(lon1);
		orient = Math.toRadians(orient);
		double r = 6371; // km
		double lat2 = Math.asin(Math.sin(lat1)*Math.cos(distance/r) + Math.cos(lat1)*Math.sin(distance/r)*Math.cos(orient) );
		double lon2 = lon1 + Math.atan2(Math.sin(orient) * Math.sin(distance/r) * Math.cos(lat1), Math.cos(distance/r) - Math.sin(lat1) * Math.sin(lat2));
		lon2 = (lon2+3*Math.PI) % (2*Math.PI) - Math.PI;
		lon2 = Math.toDegrees(lon2);
		lat2 = Math.toDegrees(lat2);
		Position next = new Position(lat2 + "," + lon2); 
		return next;
	}
	
	/** 
	* Get the closest position nearest to the reference position from an ArrayList of positions
	*/
	public static Position getClosest2reference(ArrayList<Position> c, Position reference){
    	// Gets the closest point to the user's current position
    	Position temp;
    	Position min = new Position(0,0);
    	double d;
    	double minDistance = 1000;
    	Iterator<Position> t = c.iterator();
    	while (t.hasNext()){
    		temp = (Position) t.next();
    		//System.out.println("Checking: " + temp.getLat() + "," + temp.getLon());
    		d = Tools.distance(reference, temp);
    		if (d < minDistance){
    			min = temp;
    			minDistance = d;
    		}
    	}
    	return min;
    }
	
	
	/** 
	* Get Levenshtein distance between two CharSequence objects
	*/
	public static int computeLevenshteinDistance(CharSequence str1, CharSequence str2) {
             int[][] distance = new int[str1.length() + 1][str2.length() + 1];

             for (int i = 0; i <= str1.length(); i++)
                     distance[i][0] = i;
             for (int j = 0; j <= str2.length(); j++)
                     distance[0][j] = j;

             for (int i = 1; i <= str1.length(); i++)
                     for (int j = 1; j <= str2.length(); j++)
                             distance[i][j] = minimum(
                                             distance[i - 1][j] + 1,
                                             distance[i][j - 1] + 1,
                                             distance[i - 1][j - 1]
                                                             + ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0
                                                                             : 1));

             return distance[str1.length()][str2.length()];
    }
	
	private static int minimum(int a, int b, int c) {
         return Math.min(Math.min(a, b), c);
	}
	
	
	
	/** 
	* convert distance into time based on speed
	*/
	public static Double distanceToSeconds(Double distance, Double speed){
		//speed = distance/time;
		//distance must be in meters.
		
		Double time = 0.0;
		if (speed == 0.0){
			speed = 1.3; // average walking speed
		}
		time = distance/speed;
		return time;
	}
}