package hw.macs.gruve;

import hw.macs.gruve.Configuration;

import java.io.File;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import java.lang.Exception;

public class GruveIM {
	//DefaultMap map;

   	String sessionId, userEmail;
	ArrayList<Position> prevCoordinates;
	Position reportedUserCoor, currentUserCoor, prevUserCoor;
	Position prevReportedUserCoor;
	double inferredUserOrient, reportedUserCoorAcc, inferredUserCoorAcc, predictedUserCoorAcc, currentUserCoorAcc;
	LinkedList<Double> pastOrientations, last10PaceRates;
	LinkedList<Long> last10GPSTimestamps;
	String userPA, prevUserPA;
	String userUtterance;
	String userUtteranceWaitingEC;
	String userPosition, userPosType;
	ArrayList<String> currentUserLocation, nextNodeLocation, currentWays;
	String currentWay;
	String sysDACF, prevSysDACF;
	String userDACF;
	String prevSysUtt;
	String sysUttType, prevSysUttType;
	String readyForASR;
	JSONObject sysDA, prevSysDA;
	JSONObject userDA, prevUserDA;
	JSONObject lastRDUserDA;
	String chooseCoordinate, prevChooseCoordinate;
	Double userPaceRate;
	int runsWithoutOrientation;
	
	//Iterator<String> route;
	Position destinationCoor, nextNodeCoor, nextNextNodeCoor;
	String resolvedEntityName, resolvedEntityId, resolvedEntityType, resolvedEntityDesc;  
	String lastResolvedEntityName, lastResolvedEntityId, lastResolvedEntityType, lastResolvedEntityDesc;  
	ArrayList<String> resolvedEntityDuplicateIds, lastResolvedEntityDuplicateIds;
	
	Route currentRoute;
	Iterator<RouteElement> routeNodes;
	Iterator<String> majorTurnNodes;
	String nextMajorTurnNode;
	String destinationId, destinationName;
	ArrayList<String> destinationLoc, destinationDuplicateIds;
	ArrayList<String> destinationsRejected;
	double distanceToGoal, distanceToNextMajorTurnNode;
	
	double currentUserOrientation, prevUserOrient, nextUserOrientDel, expectedOrientation, predictedNextUserOrient, turnAngleAtNextNode;
	double currentDistanceFromNextNode, prevDistanceFromNextNode, currentStepSize, lastStepSize, userUtteranceConfScore;
	double expectedSpeed, streetviewSpeed, walkingSpeed;
	
	ArrayList<String> landmarkInformed, landmarksCloseToNextNode;
	String closestLandmarkId;
	
	long lastUpdate, gpsTime;
	long elapsedTimeWithoutUtterances, lastTimeOfUtterance;
	long elapsedTimeWithSameCoordinates, lastTimeOfDiffCoor;
	long elapsedTimeOfDeviation, lastTimeOfNoDeviation;
	
	
	RouteElement prevRE, nextRE, nextNextRE;
	String prevNode, nextNode, nextNextNode, prevWayOnRoute, expectedWayOnRoute, nextWayOnRoute, prevWayName, expectedWayName, currentSlope;
	
	boolean streetview, streetviewServlet, gruve;
	
	int nUserOffCourse;
	double close2NodeDistance, interNodeDistance;
	double userReward, totalReward;
	int randomAction, maxAction;
	boolean convEnded = false;
	boolean poiService;
	
	Hashtable<String,Boolean> bsv = new Hashtable<String, Boolean>();
	
	Hashtable<String,String> entitiesInContext;
	String lastMentionedEntityId;
	String lastMentionedEntityType;
	
	
	int run;
	
	String logName, systemLog, coorLog;
	LogWriter l;
	
	String cityModel;
	HWCityModel cm;
	UserModel um;
	//LocationLog ll;
	
	NLG nlg;
		
	// set the absolute path for your application here : 
	// public static final String root = "C:\\Program Files\\Apache Software Foundation\\Tomcat 7.0\\webapps\\gruve";
	public static final String fsep = System.getProperty("file.separator");
	
	public GruveIM(String sessionId, String userEmail){

		// One initial run..
		
		//set to true if the webclient interacts with the IM over a servlet.. 
		//set to false if the webclient interacts with the IM over ICE..
		streetviewServlet = true;
		
		//set to true if the webclient is playing the GRUVE game
		//gruve = true;
		gruve = false;
		// System.out.println("Gruve server:" + gruve);

		String currentDir = Configuration.root + fsep + "WEB-INF" + fsep;								
		
		String classesDir = currentDir + "classes" + fsep;
		String userModelsDir = classesDir + "usermodels" + fsep;
		
		(new File(currentDir + "logs" + fsep + sessionId)).mkdir();
		
		logName = currentDir + "logs" + fsep + sessionId + fsep;
		systemLog = logName + "systemLog";
		
		
		um = new UserModel(userModelsDir, sessionId, userEmail);
		
		cityModel = "HWU";
		streetview = true;
		 
		cm = new HWCityModel(new File(classesDir + "mymap.osm"));
		
		nlg = new NLG(um, cm);
		
		bsv = new Hashtable<String,Boolean>();
		bsv.put("cmReady", true);
		
		resetEpisode();
		// System.out.println("New Gruve IM created for " + sessionId);
	}
	
	public void close(){
		l.close();
	}
	
	public String run(String userResponse){

		resetTurn();
		
		sysDA = new JSONObject();		

		run++;	
		
		l.log("");
		l.log("Run: " + run);
		l.log("");
		long now = (new Date()).getTime();
		l.log("Time: " + now);
		
		long timeBetweenUpdates = 0;
		if (lastUpdate != 0){
			timeBetweenUpdates = now - lastUpdate;
			l.log("Time between updates: " + timeBetweenUpdates);
		}

		Object obj = JSONValue.parse(userResponse);
		JSONObject uResponse = (JSONObject) obj;
		
		
		l.log("userResponse:" + userResponse);
		
		if(uResponse.containsKey("sessionId")){
			sessionId = (String) uResponse.get("sessionId");
		}
		if(uResponse.containsKey("userEmail")){
			userEmail = (String) uResponse.get("userEmail");
		}
		
		if (uResponse.containsKey("userDA")){
			bsv.put("userDAavailable", true);
			// System.out.println(uResponse.get("userDA"));
			userDA = (JSONObject) JSONValue.parse(uResponse.get("userDA").toString());
			userDACF = (String) userDA.get("cf");
		} else {
			userDA = null;
			userDACF = "null";
			bsv.put("userDAavailable", false);
		}
		
		if (uResponse.containsKey("userPosition")){
			userPosition = (String) uResponse.get("userPosition");
			lastUpdate = now;
			l.log("User location reported:" + userPosition.toString());
		} else {
			l.log("User location not reported");
			userPosition = "null";			
		}
	   
		if (uResponse.containsKey("userOrientation")){
             //XKL
             //currentUserOrientation = (Double) uResponse.get("userOrientation");
             String orient = uResponse.get("userOrientation").toString();
             currentUserOrientation  = new Double(Double.parseDouble(orient));
             l.log("User orientation from PT:" + currentUserOrientation);
             if (bsv.get("userStepSizeAvailable")){
            	 bsv.put("userOrientationFromPT", true);
            	 bsv.put("userOrientationAvailable", true);
     			//storing past orientations.. 
     			if (pastOrientations.size() > 10){
     				pastOrientations.removeFirst();
     			}
     			pastOrientations.addLast(currentUserOrientation);
     			l.log("Past orientations: " + pastOrientations.toString());
             }
	    }
	
	    l.log("User DA: " + userDA);
				
		
		if (!userPosition.equals("null")){
			//temp[2] is user location "lat;lon;accuracy"
			String[] temp2 = userPosition.split(";");
			double lat, lng;
			lat = Double.valueOf(temp2[0]);
			lng = Double.valueOf(temp2[1]);
			reportedUserCoor = new Position(lat,lng);
			
			bsv.put("reportedCoordinatesAvailable", true);
			l.log("User reported position: " + reportedUserCoor.toString());
			
			//assign the chosen coordinate as CurrentUserCoordinate
			currentUserCoor = reportedUserCoor;
			bsv.put("userCoordinatesAvailable", true);
			bsv.put("userCoordinatesReported", true);
			currentUserCoorAcc = reportedUserCoorAcc;
			l.log("Coordinates used: reported");
			
		} else {
			bsv.put("userCoordinatesAvailable", false);
		}

		
		//where is the user
		if(bsv.get("userCoordinatesAvailable") && bsv.get("cmReady")) { 
			l.log("User current position: " + currentUserCoor.toString());
			currentWays = cm.getAdjacentWays(currentUserCoor);
			l.log("Current ways list (possibilities): " + currentWays);
			if (currentWays.size() > 0){
				currentWay = currentWays.get(0);
			} else {
				currentWay = "null";
			}
			l.log("Current way: " + currentWay);
			currentUserLocation = cm.getAdjacentStreetNames(currentUserCoor);
			l.log("User location: " + currentUserLocation);
		}
		
		//calculating the user's orientation, stepsize..etc 
		if(bsv.get("userCoordinatesAvailable")) { 
			if (bsv.get("userOrientationFromPT") == false){
				
				if (prevUserCoor != null && bsv.get("userCoordinatesAvailable")){ 
					currentStepSize = Tools.distance(currentUserCoor, prevUserCoor) * 1000;
					l.log("Current Step Distance (in meters): " + currentStepSize);
					//when the GPS report average around the same coordinate.. the user may not be walking...
					//yet to implement that.. so here is a simple rule..
					if (!Double.isNaN(currentStepSize) && currentStepSize > 2){
						bsv.put("userIsWalking", true);
						userPA = "walking";
					} else {
						bsv.put("userIsWalking", false);
						userPA = "still";
					}
				} else {
					l.log("Current Step Distance: unknown");
					//assuming that the user is still
					bsv.put("userIsWalking", false);
					userPA = "still";
				}
				l.log("user walking: " + bsv.get("userIsWalking"));
				
				//if the user is not at the same spot...
				if (prevUserCoor != null && bsv.get("userIsWalking") && !prevUserCoor.equals(currentUserCoor)){
					currentUserOrientation = Tools.getOrient(prevUserCoor,currentUserCoor);
					bsv.put("userOrientationAvailable", true);
					l.log("Using calculated orientation");
				} 
				
				if (bsv.get("userOrientationAvailable") == false && prevUserOrient != 0.0){
					currentUserOrientation = prevUserOrient;
					bsv.put("userOrientationAvailable", true);
					l.log("Using previous orientation");
				}		
			} else {
				l.log("Using PT orientation");
			}
			
			
			
			if (bsv.get("userOrientationAvailable")){
				l.log("User orientation:" + currentUserOrientation);
				if (bsv.get("userInitialOrientationAvailable") == false){
					bsv.put("userInitialOrientationAvailable", true);
				}
			} else {
				l.log("User Orientation: unknown");				
			}			
		}
		
		l.log("User DACF: " + userDACF);
		//setting internal variables based on user DACF.
		
		
		//process user responses to sys questions/requests
		if (userDACF.equals("informDisagreement")){
			
		}
		else if (userDACF.equals("acknowledge") || userDACF.equals("informAgreement")){
			
		} 

		//Processing user requests
		if (userDACF.equals("requestRepeat")){
			bsv.put("userRequestedRepeat", true);
		}
		else if (userDACF.equals("requestHelp")){
			bsv.put("userRequestedHelp", true);
		}
		else if (userDACF.equals("requestRoute")){
			destinationName = (String) userDA.get("destination_name");
			if (bsv.get("navigatingUser")){
				bsv.put("navigatingUser", false);
			}
			bsv.put("userRequestedRoute", true); //userRequestedRoute will be true until it is acknowledged
			bsv.put("routeNotFound", false);
			bsv.put("routeRequested", false);
		} 
		else if (userDACF.equals("requestDirection")) {
			bsv.put("userAsksForDirection", true);
		}
		else if (userDACF.equals("stopDirections")){
			bsv.put("needToAcknowledgeUser", true);
			bsv.put("navigatingUser", false);
			bsv.put("userRequestedRoute", false);
			destinationId = "null";
		}
		else if (userDACF.equals("quitGame")){
			
		}
		/// 
		
		
		//Get a route if requested..
		if (bsv.get("routeRequested") && bsv.get("userCoordinatesAvailable") && bsv.get("cmReady") ){
			
			//if user is not navigating at all.. 
			if (!bsv.get("navigatingUser")){
				destinationId = cm.getClosestEntityIdByName(destinationName, currentUserCoor);
				destinationCoor = cm.getCoordinates(destinationId);
				destinationLoc = cm.getAdjacentStreetNames(destinationCoor);
				l.log("Finding route to " + destinationId + "," + destinationName + " on " + destinationLoc + " at " + destinationCoor);
				getRouteAndInitialize(destinationCoor);
				bsv.put("routeRequested", false);
			} 
		} // enf of if user asked for a route 
	
		

		
		//Task Navigation::: Is the user being navigated...???
		if (bsv.get("navigatingUser") && bsv.get("userCoordinatesAvailable") && bsv.get("cmReady")) {
			l.log("Destination id: " + destinationId);
			//l.log("Expected current way id: " + expectedWayOnRoute);
			
			//is the user at the expected location.. i.e. at street X or junction Y
			if(!expectedWayOnRoute.equals("null")){
				l.log("Expected current way name: " + expectedWayName);
				ArrayList<String> currentAdjacentWays = cm.getAdjacentWays(currentUserCoor);
				l.log("Current adjancent ways: " + currentAdjacentWays.toString());
				l.log("Expected way on route: " + expectedWayOnRoute);
				if(currentAdjacentWays.contains(expectedWayOnRoute) || currentAdjacentWays.contains(prevWayOnRoute)){
					l.log("User reported at expected location");
					bsv.put("userAtExpectedLocation", true);
				} else {
					l.log("User NOT reported at expected location");
					bsv.put("userAtExpectedLocation", false);
				}
			} else {
				l.log("ExpectedWayOnRoute is unknown, user assumed to be at expected location");
				expectedWayOnRoute = currentWay;
				bsv.put("userAtExpectedLocation", true);
			}
			
			// what is the distance between the current position and the next supposed node?
			currentDistanceFromNextNode = Tools.distance(currentUserCoor,nextNodeCoor) * 1000;
			l.log("Prev Distance from Next node: " + prevDistanceFromNextNode);
			l.log("Current Distance from Next node: " + currentDistanceFromNextNode);
			
			
			//if the current distance from the next node is less than 10 meters..
			if (currentDistanceFromNextNode < close2NodeDistance) {
				bsv.put("userNearNextNode", true);
			} 
			l.log("User near Next node: " + bsv.get("userNearNextNode"));
			
			//finding the next node to go..
			if (bsv.get("userNearNextNode")){
				prevWayName = expectedWayName;
				prevWayOnRoute = expectedWayOnRoute;
				prevNode = nextNode;
				prevRE = nextRE;
				
				if (bsv.get("noMoreNodes")) {
					bsv.put("destinationReached", true);
					bsv.put("userOnRoute", false);
					bsv.put("navigatingUser", false);
				} else {
					do {
						nextRE = nextNextRE;
						nextNode = nextNextNode;
						nextNodeCoor = nextNextNodeCoor;
						nextNodeLocation = cm.getAdjacentStreetNames(nextNodeCoor);
						l.log("Setting nextNode to :" + nextNode);
						expectedWayOnRoute = cm.getWayId(prevNode, nextNode);
						expectedWayName = cm.getWayName(expectedWayOnRoute);
						bsv.put("nextNodeChanged", true);
						
						
						double d2 = Tools.distance(currentUserCoor,nextNodeCoor) * 1000;
						distanceToNextMajorTurnNode = d2; 
						l.log("Distance to next node:" + distanceToNextMajorTurnNode);
						// Ignoring all nodes within 10 m distance..
						if (d2 > interNodeDistance){
							bsv.put("userNearNextNode", false);
						} else {
							l.log("Ignoring this node.." + nextNode);
							bsv.put("userNearNextNode", true);
						}
						if (routeNodes.hasNext()){
							nextNextRE = routeNodes.next();
							nextNextNode = nextNextRE.getNodeId();
							nextWayOnRoute = cm.getWayId(nextNode, nextNextNode);
							nextNextNodeCoor = cm.getCoordinates(nextNextNode); 
							l.log("Setting nextNextNode to: " + nextNextNode + "," + nextNextNodeCoor.toString());
						} else {
							bsv.put("noMoreNodes", true);
							double d3 = Tools.distance(currentUserCoor, nextNodeCoor);
							double d4 = Tools.distance(currentUserCoor, destinationCoor);
							if (d3 > d4){
								nextNodeCoor = destinationCoor;
								l.log("Setting nextNode to destinationCoor cause its closer than the last node..");
							}
							break;
						}						
					} while (routeNodes.hasNext() && bsv.get("userNearNextNode"));
					//nextWayOnRoute = cm.getWayId(nextNode,nextNextNode);
					l.log("Current Way:" + expectedWayOnRoute);
					l.log("Current Way Name: " + expectedWayName);
					//resetting prevDistanceFromNextNode when the nextNode changes.. because we are at a New Node
					currentDistanceFromNextNode = cm.getNetworkDistance(currentUserCoor,nextNodeCoor);
					prevDistanceFromNextNode = currentDistanceFromNextNode + 1; //setting it as more so that user may be seen as moving towards the next node.

					if (!expectedWayName.equals(prevWayName) && !expectedWayName.equals("null")){ 
						bsv.put("wayNameChanged", true); 
					} else {
						bsv.put("wayNameChanged", false);
					}
					
					l.log("Current Distance from New Next node: " + currentDistanceFromNextNode);
					bsv.put("userNearNextNode", false);
				}
			} // end of bsv.get("userNearNextNode")...
		
			//if the currentDist to NextNode is less than previousDist to NextNode.. then the user is moving towards the nextNode
			if (currentDistanceFromNextNode < prevDistanceFromNextNode){
				bsv.put("userMovingTowardsNextNode", true);
			} else if (currentDistanceFromNextNode >= prevDistanceFromNextNode){
				bsv.put("userMovingTowardsNextNode", false);
			} 
			// if they are equal.. user is staying still so it will be the same as last step
			
			l.log("User moving towards Next Node : " + bsv.get("userMovingTowardsNextNode"));
			
			
			if (bsv.get("nextNodeChanged") ==  true){
				nextUserOrientDel = Tools.getRelativeOrient(currentUserCoor, nextNodeCoor, currentUserOrientation);
				l.log("To reach the next node, user should turn " + nextUserOrientDel);
				expectedOrientation = Tools.orientRoundup(currentUserOrientation + nextUserOrientDel);
				bsv.put("expectedOrientationAvailable", true);
				if (nextUserOrientDel > -30 && nextUserOrientDel < 30 && !bsv.get("veryFirstInstruction")){
					l.log("Don't need a turn instruction here..");
					bsv.put("turnLessThan30", true);
				} else {
					bsv.put("turnLessThan30", false);
				}
			}
			
			l.log("Expected user orientation: " + expectedOrientation);
			
			double currentDeviation = Tools.getOrientDel(currentUserOrientation, expectedOrientation);
			if(Math.abs(currentDeviation) > 90){
				bsv.put("userInExpectedOrientation", false);
			} else {
				bsv.put("userInExpectedOrientation", true);
			}
			l.log("User in expected orientation: " + bsv.get("userInExpectedOrientation"));
		
			if (bsv.get("userAsksForDirection") && bsv.get("navigatingUser")){
				nextUserOrientDel = Tools.getRelativeOrient(currentUserCoor, nextNodeCoor, currentUserOrientation);
				l.log("User asked for directions. He needs to turn " + nextUserOrientDel);
			} 
			
			
			
			// is the current distance greater than the previous distance from the next node..?? user may be deviating if it is..
			// however in GPS error condition, it is possible that this a falsr positive .i.e. its not true in reality but reported to be so..
			// so, we add extra conditions that user should not be at expected location and the system not be expecting a user turn
			l.log("Expecting user to turn:" + bsv.get("expectingUserTurn"));
			//l.log("Deviation state: CD<PD:" + bsv.get("userMovingTowardsNextNode") + ",NNChanged:" + bsv.get("nextNodeChanged") + ",UEL:" + bsv.get("userAtExpectedLocation") + ",EUT:" +	bsv.get("expectingUserTurn"));
			if (bsv.get("userMovingTowardsNextNode") == false && bsv.get("userAtExpectedLocation") == false){  
				//if (bsv.get("userMayBeDeviatingFromRoute") && now - timeDeviationStarts > 30000){
				if ((now - lastTimeOfNoDeviation) > 30000){
					l.log("User deviating from route. Finding new route.");
					getRouteAndInitialize(destinationCoor);
					lastTimeOfNoDeviation = now;
					//bsv.put("userMayBeDeviatingFromRoute", false);
					nextUserOrientDel = Tools.getRelativeOrient(currentUserCoor, nextNodeCoor, currentUserOrientation);
					l.log("To reach the next node, user should turn " + nextUserOrientDel);					
					expectedOrientation = Tools.orientRoundup(currentUserOrientation + nextUserOrientDel);
					l.log("Expected user orientation: " + expectedOrientation);
				} /*else {
					//bsv.put("userMayBeDeviatingFromRoute", true);
					l.log("User MAY be deviating: " + bsv.get("userMayBeDeviatingFromRoute"));
				}*/
				nUserOffCourse++;
			} 	
			else {
				//bsv.put("userMayBeDeviatingFromRoute", false);
				lastTimeOfNoDeviation = now;
			} 					
			
			if (bsv.get("userInExpectedOrientation") == false){
				l.log("Warning: user may be walking in the wrong direction");
				//userDeviatingFromRoute = true;
				//nUserOffCourse++;
			}
			

			l.log("Next node: " +  nextNode + " at "+ nextNodeCoor.toString());
			
			l.log("UserWalking: " + bsv.get("userIsWalking"));

			// get any closest landmarks (for navigation)
			bsv.put("landmarkNearUser", false);
			closestLandmarkId = "null";
			if (bsv.get("userCoordinatesAvailable")) {
				ArrayList<String> landmarksNearby = new ArrayList<String>();
				landmarksNearby.addAll(cm.getSalientEntitiesWithin(currentUserCoor, 20));
				l.log("Landmarks within 20m:" + landmarksNearby.toString());
				if (landmarksNearby.size() > 0){
					closestLandmarkId = landmarksNearby.get(0);
				}
				if (closestLandmarkId.equals("null")||landmarkInformed.contains(closestLandmarkId)){
					l.log("Landmarks nearby:none");
					bsv.put("landmarkNearUser", false);
				} else {
					l.log("Landmark nearby:" + closestLandmarkId);
					bsv.put("landmarkNearUser", true);
				}
			}
		} //end of navigation...
	
		
		//
		

		// system state update
		sysDACF = "null";
		String sysUtt = "";
		sysUttType = "dm";
		
		elapsedTimeWithoutUtterances = now - lastTimeOfUtterance;
		if (elapsedTimeWithoutUtterances > 50000){
			l.log("No utterances for 50000 ms");
			bsv.put("fillInSilence", true);
		} else {
			bsv.put("fillInSilence", false);
		}
		
		// Select sysDA..based on above state variables
		if (bsv.get("userGreeted") == false){
			sysDACF = "greetUser";
			bsv.put("userGreeted", true);
			sysUttType = "setup";
		}
		else if (bsv.get("userIsWalking") ==  false && bsv.get("userInitialOrientationAvailable") == false && bsv.get("userInformedOrientationUnavailable") == false) {
			sysDACF = "startWalking";
			bsv.put("userInformedOrientationUnavailable", true);
			sysUttType = "setup";
		}
		else if (bsv.get("userRequestedHelp")){
			sysDACF = "introduceSelf";
			sysUttType = "dm";
		}
		else if (bsv.get("nextNodeChanged")){
			sysDACF = "presentRoute";
			sysUttType = "navInstruction";
		}
		else if (bsv.get("navigationHelpPromptToBeGiven")){
			sysDACF = "navigationHelpPrompt";
			bsv.put("navigationHelpPromptToBeGiven", false);
			sysUttType = "navInstruction";
		}
		else if (bsv.get("destinationReached")){
			sysDACF = "destinationReached";			
			bsv.put("destinationReached", false);
			bsv.put("destinationVisible", false);
			sysUttType = "navInstruction";
		} 
		else if (bsv.get("userRequestedRepeat")){
			l.log("prevSysDA:" + prevSysDA);
			sysDA = prevSysDA;
			sysDACF = (String) prevSysDA.get("cf");
			sysUttType = prevSysUttType;
			sysUtt = prevSysUtt;
			bsv.put("userRequestedRepeat", false);
			bsv.put("repeatingSysUtterance", true);			
		}
		else if (bsv.get("userRequestedRoute")){
			sysDACF = "acknowledgeRouteRequest";
			bsv.put("userRequestedRoute", false);
			bsv.put("routeRequested", true); //routeRequested will be true until the system starts giving the user a route
			sysUttType = "navInstruction";
		}
		else if (bsv.get("needToAcknowledgeUser")){
			sysDACF = "acknowledge";
			bsv.put("needToAcknowledgeUser", false);
			sysUttType = "dm";
		}
		else if (bsv.get("userAsksForDirection")){
			if (bsv.get("navigatingUser")){
				sysDACF = "presentRoute";
				bsv.put("userAsksForDirection", false);
				sysUttType = "navInstruction";
			} else {
				sysDACF = "null";
				sysUttType = "dm";
			}	
		} 
		else {
			sysDACF = "null";
			sysUttType = "dm";
		}	
		

		
		if (!bsv.get("repeatingSysUtterance")){
			sysDA.put("cf", sysDACF);
					
			//Adding the obvious parameters to the sysDAs
			//and other post processing..
			if (sysDACF.equals("presentRoute")){
				sysDA.put("route", currentRoute);
				sysDA.put("index", nextRE.getIndex());
			}
			else if (sysDACF.equals("destinationReached")){
				sysDA.put("entityId", destinationId);
				sysDA.put("entityName", destinationName);		
			}
			else if (sysDACF.equals("acknowledgeRouteRequest")){
				sysDA.put("entityId", destinationId);
				sysDA.put("entityName", destinationName);		
			}
			
			
			if (bsv.get("userDAavailable")){
				sysDA.put("userDA", userDA.toString());
			}
			
			if(bsv.get("userCoordinatesAvailable")) { 
				sysDA.put("currentUserCoor", currentUserCoor.toString());
				sysDA.put("currentUserOrientation", currentUserOrientation);
			}
			
			
			l.log("SysDA: " + sysDA);

			// call to NLG
			sysUtt = nlg.realize(sysDA); //call to NLG
		}
		
		
		if (!sysUtt.equals("") && !bsv.get("repeatingSysUtterance")) {
			prevSysUtt = sysUtt;
			prevSysDA = sysDA;
			prevSysUttType = sysUttType;
		}
		
		if (bsv.get("repeatingSysUtterance")){
			sysUtt = "I repeat, " + sysUtt;
			bsv.put("repeatingSysUtterance", false);
		}
	
		
		//if either the sys or the user say something.. lastTimeOfUtterance is set to now..
		if (!sysUtt.equals("") || userDA != null){
			l.log("Setting lastTimeOfUtterance to " + now);
			lastTimeOfUtterance = now;
		}
		
		if (prevUserCoor != null && !prevUserCoor.equals(currentUserCoor)){
			l.log("Setting lastTimeOfDiffCoordinates to " + now);
			lastTimeOfDiffCoor = now;
		}
		
		lastStepSize = currentStepSize;
		prevUserDA = userDA;
		prevUserPA = userPA;
		prevUserCoor = currentUserCoor;
		prevCoordinates.add(currentUserCoor);
		prevUserOrient = currentUserOrientation;
		prevDistanceFromNextNode = currentDistanceFromNextNode;
		l.log("Sys Utterance: " + sysUtt);

		
		JSONObject sResponse = new JSONObject();
		//sResponse.put("sysDA", sysDA);
		sResponse.put("utterance", sysUtt);
		sResponse.put("uttType", sysUttType);
		
		
		String sysResponse = sResponse.toString(); 
		l.log("sysResponse: " + sysResponse);
		return sysResponse;
	}
	
	
	
	
	private void resetEpisode(){
		lastUpdate = 0;
		run = 0;
		nUserOffCourse = 0;
		randomAction = 0;
		maxAction = 0;
		l = new LogWriter(systemLog);	
		
		
		bsv.put("userGreeted", false);
		
		//user position specific variables
		currentUserOrientation = 0;
		bsv.put("userInitialOrientationAvailable", false);
		pastOrientations = new LinkedList<Double>();
		last10PaceRates = new LinkedList<Double>();
		last10GPSTimestamps = new LinkedList<Long>();
		prevCoordinates = new ArrayList<Position>();
		userPA = "still";
		bsv.put("userIsWalking", false);
		prevUserPA = "still";
		nextUserOrientDel = 0;
		expectedOrientation = 0;
		
		if (streetview){
			close2NodeDistance  = 10;
			interNodeDistance = 10;
		} else {
			close2NodeDistance = 25; //25meters
			interNodeDistance = 15;
		}
		
		readyForASR = "false";
		
		userDA = null;
		lastRDUserDA = null;
		prevUserDA = null;
		prevSysUtt = "";
		prevSysUttType = "";
		
		lastStepSize = 0;
		
		
		destinationsRejected = new ArrayList<String>();
		destinationId = "null";
		destinationName = "null";
		destinationLoc = new ArrayList<String>();
		bsv.put("destinationSuggested", false);
		
		currentUserLocation = new ArrayList<String>();
		bsv.put("routeRequested", false);
		bsv.put("userOnRoute", false);
		bsv.put("navigatingUser", false);
		bsv.put("routeNotFound", false);
		bsv.put("destinationReached", false);
		bsv.put("destinationVisibleToUser", false);
		convEnded = false;
		destinationCoor = null;
		nextNodeCoor = null;
		nextNextNodeCoor = null;
		prevUserCoor = null;
		
		
		distanceToGoal = 0;
		bsv.put("distanceToGoalLT5", false);
		bsv.put("distanceToGoalLT10", false);
		
		bsv.put("distanceToNNLT10", false);
		bsv.put("distanceToNNGT10", false);
		
		
		//POI task specific variables
		landmarkInformed = new ArrayList<String>();
		landmarksCloseToNextNode = new ArrayList<String>();
		
		//max speed per sec
		expectedSpeed = 50;
		//expectedSpeed = walkingSpeed;
					
		//resolving anaphora
		lastMentionedEntityId = "null";
		entitiesInContext = new Hashtable<String,String>(); 
		lastMentionedEntityType = "null";
		
		//RL specific variables
		totalReward = 0;
		
		bsv.put("userRequestedRoute", false);
		bsv.put("userGreets", false);
		bsv.put("userRequestsHelp", false);
		bsv.put("userRequestedRepeat", false);
		
		bsv.put("userSaysThanks", false);
		bsv.put("userAsksForDirection", false);
		bsv.put("userAsksUserLocation", false);
		bsv.put("userAcknowledgedSystem", false);
		bsv.put("userAsksWhereAmI", false);
		bsv.put("userRequestsLocInfo", false);
		bsv.put("userRequestsDistanceInfo", false);
		bsv.put("userRequestsMoreInfo", false);
		bsv.put("userRequestsDescription", false);
		bsv.put("userRequestsDestination", false);	
		bsv.put("needToAcknowledgeUser", false);
		bsv.put("userRequestsNameOfEntity", false);
		bsv.put("userRequestStopTalking", false);
		
		bsv.put("destinationAccepted", false);
		bsv.put("entityClarificationSought", false);
		bsv.put("waitingForClarification", false);
		bsv.put("suggestionFound", false);
		bsv.put("entityUnderspecified", false);
		bsv.put("requestedEntityFound", false);
		bsv.put("declareReady", false);
		bsv.put("confirmationRequested", false);
		bsv.put("userInformedOrientationUnavailable", false);
		bsv.put("userToldWhereHeIsOneTime", false);
		bsv.put("navigationHelpPromptToBeGiven", false);
		resetRoute();
		userUtteranceWaitingEC = "null";
		runsWithoutOrientation = 0;
		
		long now = (new Date()).getTime();
		lastTimeOfUtterance = now;
		lastTimeOfDiffCoor = now;
		elapsedTimeWithoutUtterances = 0;
		elapsedTimeWithSameCoordinates = 0;
		
		bsv.put("expectingResponseFromUser", false);
		//ll = new LocationLog("coord");
	}
	
	private void resetTurn(){
		bsv.put("userCoordinatesAvailable", false);
		bsv.put("userCoordinatesReported", false);
		bsv.put("reportedCoordinatesAvailable", false);
		bsv.put("userOrientationAvailable", false);
		bsv.put("userPaceRateAvailable", false);
		bsv.put("userStepSizeAvailable", false);
		bsv.put("userOrientationFromPT", false);
		bsv.put("timestampNotIncreasing", false);
		bsv.put("userSpeechConfidenceLT2", false);
		bsv.put("userRequestedHelp", false);
		reportedUserCoor = null;
		reportedUserCoorAcc = 0;
		
		currentUserCoor = null;
		currentUserOrientation = 0;
		currentUserCoorAcc = 0;
		currentUserLocation  = new ArrayList<String>();
		currentStepSize = 0;
		bsv.put("userIsWalking", false);
		userPA = "null";
		bsv.put("wayNameChanged", false); 
		chooseCoordinate = "null";
		sysDA = new JSONObject();
		sysUttType = "null";
		
		bsv.put("userMovingTowardsNextNode", false);
		bsv.put("userNearNextNode", false);
		bsv.put("nextNodeChanged", false);
		bsv.put("userAtExpectedLocation", false);
		bsv.put("userInExpectedOrientation", false); 
		bsv.put("userMayHaveTurned", false);
		bsv.put("userUtteranceParsed", true);
		bsv.put("qaQuestion", false);
		bsv.put("poiNearUser", false);
		bsv.put("landmarkNearUser", false);
		bsv.put("repeatingSysUtterance", false);
		bsv.put("qaSegmentAvailable", false);
		bsv.put("moreInfoRequested", false);

		bsv.put("requireExplicitConfirmation", false);
	}
	
	private void resetRoute(){
		bsv.put("destinationReached", false);
		
		prevNode = "null";
		nextNode = "null";
		nextNextNode = "null";
		prevWayOnRoute = "null";
		expectedWayOnRoute = "null";
		expectedWayName = "null";
		prevWayName = "null";
		nextWayOnRoute = "null";
		
		currentSlope = "null";
		distanceToNextMajorTurnNode = 0.0;
		nextNodeLocation = new ArrayList<String>();
		lastTimeOfNoDeviation = 0;
		
		majorTurnNodes = null;
		nextMajorTurnNode = "null";
		bsv.put("userMayBeDeviatingFromRoute", false);
		bsv.put("expectingUserTurn", false);
		bsv.put("landmarksNearNextNode", false);
		bsv.put("turnInstructionAtNextNodeGiven", false);
		bsv.put("turnInstructionAtLastNextNodeGiven", false);		
		bsv.put("destinationReached", false);
		bsv.put("userClaimsDestinationReached", false);
		bsv.put("userClaimsDestinationVisible", false);
		bsv.put("destinationVisible", false);
		bsv.put("destinationVisibilityInformed", false);
		bsv.put("userNearDestination", false);
		bsv.put("veryFirstInstruction", true);
		bsv.put("expectedOrientationAvailable", false);

	}
	
	

	private void getRouteAndInitialize(Position d){
		resetRoute();		
		
		//ArrayList<String> positions = new ArrayList<String>();
		ArrayList<RouteElement> positions = new ArrayList<RouteElement>();
		
		if (d != null && d.getLat() != 0.0 && d.getLon() != 0.0){
			l.log("Found destination coordinates: " + d.getLat() + "," + d.getLon());
			
			// now lets find a route..
			
			// adding destination
			//positions = cm.getRoute(currentUserCoor, d);
			currentRoute = cm.getRoute(currentUserCoor, d);
			currentRoute.displayRoute();
			positions = currentRoute.getNodes();
			
			//if (positions.isEmpty()){
			if (positions.isEmpty()){
				Double distance = Tools.distance(currentUserCoor, d) * 1000;
				if (distance < 200){
					l.log("Destination seems to be nearby.. No nodes in between the user and the destination..");
					bsv.put("userNearDestination", true);
					bsv.put("navigatingUser", false);
					bsv.put("userOnRoute", false);					
				} else {
					bsv.put("userNearDestination", false);
					bsv.put("userOnRoute", false);
					bsv.put("navigatingUser", false);
					bsv.put("routeNotFound", true);
					l.log("Route not available");
				}
			} else {
				//l.log("Route: " + positions.toString());
				bsv.put("userOnRoute", true);
				bsv.put("navigatingUser", true);
				bsv.put("userNearDestination", false);
				bsv.put("routeNotFound", false);

				routeNodes = positions.iterator();
				
				// the next node to go is the where he is now..
				//nextNodeLat = currentUserLat; 
				//nextNodeLon = currentUserLon;
				expectedWayOnRoute = "null";
				ArrayList<String> currentLoc = cm.getAdjacentStreetNames(currentUserCoor);
				if (currentLoc.size() == 1){
					expectedWayName = currentLoc.get(0);
				} else {
					expectedWayName = "null"; 
				}
				
				RouteElement firstRE = routeNodes.next();
				String firstNode = (String) firstRE.getNodeId();
				l.log("First node: " + firstNode);
				Position fnodeCoor = cm.getCoordinates(firstNode);
				
				if (!routeNodes.hasNext()){
					l.log("There is only one node!");
					bsv.put("noMoreNodes",true);
					nextNode = firstNode;
					nextNodeCoor = fnodeCoor;
					nextNextNode = "null";
				} else {
					bsv.put("noMoreNodes", false);
					RouteElement secondRE = routeNodes.next();
					String secondNode = secondRE.getNodeId();
					l.log("Second node: " + secondNode);
					Position snodeCoor = cm.getCoordinates(secondNode);

					double currentPosDistance = Tools.distance(currentUserCoor, snodeCoor) * 1000;
					double fnodeDistance = Tools.distance(fnodeCoor, snodeCoor) * 1000;
					
					if (currentPosDistance < fnodeDistance){
						l.log("Current loc is close to second node");
						nextRE = secondRE;
						nextNode = secondNode;
						nextNodeCoor = snodeCoor;
						if (routeNodes.hasNext()){
								nextNextRE = routeNodes.next();
								nextNextNode = nextNextRE.getNodeId();
								nextWayOnRoute = cm.getWayId(nextNode, nextNextNode);
								nextNextNodeCoor = cm.getCoordinates(nextNextNode);
						} else {
							bsv.put("noMoreNodes",true);
							nextNextNode = "null";
						}
					} else {
						l.log("First node is close to second node");
						nextRE = firstRE;
						nextNode = firstNode;
						nextNodeCoor = fnodeCoor;
						nextNodeLocation = cm.getAdjacentStreetNames(nextNodeCoor);
						nextNextRE = secondRE;
						nextNextNode = secondNode;
						nextNextNodeCoor = snodeCoor;
						nextWayOnRoute = cm.getWayId(nextNode, nextNextNode); 
					}
				}
				l.log("Next node: " + nextNode);
				l.log("Next Next node: " + nextNextNode);
	
				prevDistanceFromNextNode = cm.getNetworkDistance(currentUserCoor,nextNodeCoor) + 1.0;
				
				//nextWayOnRoute = cm.getWayId(nextNode, nextNextNode);
				l.log("Next Way: " + nextWayOnRoute);

				String closestNode = cm.getClosestNode(currentUserCoor);
				l.log("Closest node: " + closestNode);
				
				if (!closestNode.equals(nextNode)){
					expectedWayOnRoute = cm.getWayId(closestNode, nextNode);
				} else { 
					ArrayList<String> adjWays = cm.getAdjacentWays(currentUserCoor);
					if (adjWays.size() == 1){
						expectedWayOnRoute = (String) adjWays.get(0);
					} else {
						if (adjWays.contains(nextWayOnRoute)){
							adjWays.remove(nextWayOnRoute);
							l.log("Choosing randomly");
							expectedWayOnRoute = (String) adjWays.get((new Random()).nextInt(adjWays.size()));
						}
					}
				}
				l.log("Current Way: " + expectedWayOnRoute);
				expectedWayName = cm.getWayName(expectedWayOnRoute);
				currentSlope = "null";
				prevUserCoor = currentUserCoor;
				bsv.put("nextNodeChanged", true);
				
				bsv.put("navigationHelpPromptToBeGiven", true);
				l.log("Route ready");
				//KMLwriter.printRoute(positions, "systemTrack");
				//userDeviatingFromRoute = false;
			}
		} else {
			bsv.put("userOnRoute", false);
			bsv.put("navigatingUser", false);
			bsv.put("userNearDestination", false);
			bsv.put("routeNotFound", true);
			destinationCoor = null;
			l.log("Destination not available");
		}
	}

	
	
}