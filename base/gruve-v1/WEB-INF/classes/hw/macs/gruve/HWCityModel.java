package hw.macs.gruve;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class HWCityModel {
	ArrayList<Node> allElements;
	Hashtable<String,String> allNodes, allWays;
	Hashtable<String,Hashtable<String,String>> allEntities;
	
	public static final String fsep = System.getProperty("file.separator");
	
	public static void main(String[] arg){
		String currentDir = ".."+ fsep + "webapps" + fsep + "gruve" + fsep + "WEB-INF" + fsep;
		
		//String currentDir = "..\\webapps\\gruve\\WEB-INF\\";
		String classesDir = currentDir + "classes" + fsep;
		HWCityModel r = new HWCityModel(new File(classesDir + "mymap.osm"));
		
		String id = r.getClosestEntityIdByName("South College street", new Position(55.945722,-3.1870830000000296));
		System.out.println("closest entity: " + id);
		
	}
	
	public HWCityModel(File f){
		allElements = loadFromFile(f);
		allNodes = loadAllNodes(allElements);
		allWays = loadAllWays(allElements);
		allEntities = loadAllEntities(allElements);
		//System.out.println(allNodes.size() + "," + allWays.size());
	}
	
		
	
	/**
	 * Get street network distance between two Position objects
	 */
	public double getNetworkDistance(Position fromPos, Position toPos){
		String fromClose = getClosestNode(fromPos);
		String toClose = getClosestNode(toPos);
		double d1 = Tools.distance(fromPos, getCoordinates(fromClose)) * 1000;
		//System.out.println("d1: " + d1);
		double d2 = 0;
		//System.out.println("d2: " + d2);

		Route r = getRoute(fromClose, toClose);
		ArrayList<RouteElement> nodes = r.getNodes();
		//System.out.println("Nodes: " + nodes);
		Iterator<RouteElement> i = nodes.iterator();
		Position source = getCoordinates(fromClose);
		while (i.hasNext()){
			Position next = getCoordinates(i.next().getNodeId());
			d2 += Tools.distance(source,next) * 1000;
			//System.out.println("d2: " + d2);
			source = next;
		}
		
		double d3 = Tools.distance(toPos, getCoordinates(toClose)) * 1000;
		//System.out.println("d3: " + d3);
		double sum = d1+d2+d3;
		return sum;
	}
	
	/**
	* Get route between two Position objects 
	*/
	public Route getRoute(Position fromPos, Position toPos){
		String from = getClosestNode(fromPos);
		String to = getClosestNode(toPos);
		return getRoute(from, to);
	}
	
	/** 
	* Get route between two nodes 
	* from - nodeId
	* to - node Id
	*/
	public Route getRoute(String from, String to){
		//pseudocode: http://en.wikipedia.org/wiki/A*_search_algorithm#Pseudocode
		//http://www.policyalmanac.org/games/aStarTutorial.htm

		Route r = new Route();
		//ArrayList<String> temp = new ArrayList<String>();
		ArrayList<String> open = new ArrayList<String>();
		ArrayList<String> closed = new ArrayList<String>();
		Hashtable<String,Double> gScore = new Hashtable<String,Double>();
		Hashtable<String,Double> hScore = new Hashtable<String,Double>();
		Hashtable<String,String> parent = new Hashtable<String,String>();
		
		open.add(from);
		gScore.put(from, 0.0);
		hScore.put(from, getDistance(from,to));
		
		while(!open.isEmpty() && !closed.contains(to)){
			//System.out.println("OPEN: " + open.toString());
			//System.out.println("CLOSED: " + closed.toString());

			//step1; get the node in open list that has the lowest fscore
			Iterator<String> i = open.iterator();
			double minF = 1000;
			String minFNode = ""; 
			while(i.hasNext()){
				String n = (String) i.next();
				double g = (Double) gScore.get(n);
				double h = (Double) hScore.get(n);
				double f = g + h;    
				if (f < minF){
					minFNode = n;
					minF = f;
				}
			}
			//System.out.println("MINF: " + minFNode);
			
			//step2: moving minFnode from open to close
			open.remove(minFNode);
			closed.add(minFNode);
			if (closed.contains(to)){
				break;
			}
			
			//step3: adding nearest nodes to minFnode to open list 
			ArrayList<String> nearest = getReachableNodes(minFNode);
			//System.out.println("Nearest: " + nearest.toString());
			Iterator<String> j = nearest.iterator();
			boolean better = true;
			while(j.hasNext()){
				String n = (String) j.next(); 
				if (!closed.contains(n)) {
					double temp_gn = (Double) gScore.get(minFNode) + getDistance(minFNode,n);
					
					if(!open.contains(n)){
						open.add(n);
						better = true;
					} 
					else if (gScore.contains(n) && (Double) gScore.get(n) > temp_gn){
						better = true;
						gScore.remove(n);
					} 
					else {
						better = false;
					}
					if (better == true){
						gScore.put(n, temp_gn);
						if (parent.contains(n)){
							parent.remove(n);
						}
						parent.put(n, minFNode);
						hScore.put(n, getDistance(n,to));
					}
				}
			}
			//System.out.println("PARENTS: " + parent.toString());
		}
		//System.out.println("PARENTS: " + parent.toString());
		String pathReverse = "";
		if(closed.contains(to)){
			//System.out.println("path found");
			pathReverse = to;
			String tempNode = to;
			while(tempNode != from){
				tempNode = (String) parent.get(tempNode);
				//System.out.println(tempNode);
				pathReverse = tempNode + ","+ pathReverse;
			}
			//System.out.println("PATH:" + pathReverse);
			String[] path = pathReverse.split(",");
			for (int i=0; i < path.length - 1; i++){
				//temp.add(path[i]);
				r.add(path[i], path[i+1]);
			}	
			r.add(path[path.length - 1], "null");
		}		
		//return temp;
		return r;
	}


	/** 
	 * Get amenity of a given type nearest to Position p
	 */
	 
	public String getClosestAmenity(Position p, String type) {
		// TODO Auto-generated method stub
		String entityId = "null";
		ArrayList<String> pEntityIds = new ArrayList<String>();
		double distance = 0.001;
		do {
			pEntityIds = getClosestEntitiesWithin(type, p, distance);
			distance += 0.001;
		} while (pEntityIds.size() == 0);
		
		if (pEntityIds.size() == 1){
			entityId = (String) pEntityIds.get(0);
		} else if (pEntityIds.size() > 1) {
			entityId = (String) pEntityIds.get((new Random()).nextInt(pEntityIds.size()));
		}
		return entityId;
	}
	
	
	/** 
	* Get salient entities (such as ChainStores) within given distance limit from position p
	*/
	public ArrayList<String> getSalientEntitiesWithin(Position pos, double limit){
		return getClosestEntitiesWithin("chainStores", pos, limit);
	}
		
	/** 
	* Get the closest entities of give type within given distance limit from position p
	*/
	public ArrayList<String> getClosestEntitiesWithin(String type, Position p, double limit){
			
		    ArrayList<String> entitiesNearby = new ArrayList<String>();
			Enumeration<String> e = allEntities.keys();
			double d = 0;
			while(e.hasMoreElements()){
				String temp = (String)e.nextElement();
				//System.out.println("Checking " + temp);
				Hashtable<String,String> t = ((Hashtable<String,String>) allEntities.get(temp));
				//String name = t[1];
				if (t.size() > 0){
					Position tpos = new Position((String) t.get("position"));
					String eType = (String) t.get("type");
					d = Tools.distance(tpos,p) * 1000;
					if (d < limit && eType.equals(type)){
						entitiesNearby.add(temp);
					}
				}
		  	}
			return entitiesNearby;
	}
	
	/** 
	* Get the name of the entity with the given id
	*/
	public String getEntityName(String id){
		String name = "null";
		if (!id.equals("null") && allEntities.containsKey(id)){
			Hashtable<String,String> t = ((Hashtable<String,String>) allEntities.get(id));
			if (t.containsKey("name")){
				name = (String) t.get("name");
			}
		}
		return name;
	}
	
	/** 
	* Get all nodes within the given distance limit from position p
	*/
	public ArrayList<String> getClosestNodesWithin(Position pos, double limit){
		ArrayList<String> cnodes = new ArrayList<String>();
		Enumeration<String> e = allNodes.keys();
		double d = 0;
		while(e.hasMoreElements()){
			String temp = (String)e.nextElement();
			//System.out.println("Checking " + temp);
			Position tpos = new Position((String) allNodes.get(temp));
			d = Tools.distance(tpos,pos);
			if (d < limit){
				cnodes.add(temp);
			}
	  	}
		return cnodes;
	}
	
	/** 
	* Get the closest node to the given position p 
	* Caution: It does not verify if there is any WAY to get to the closest node from the given point.
	*/
	public String getClosestNode(Position pos){
		Enumeration<String> e = allNodes.keys();
		double d = 0;
		double min = 100;
		String closestNode = "null";
		while(e.hasMoreElements()){
			String temp = (String)e.nextElement();
			//System.out.println("Checking " + temp);
			Position tpos = new Position();
			tpos.setPosition((String) allNodes.get(temp));
			d = Tools.distance(tpos,pos);
			if (d < min){
				min = d;
				closestNode = temp;
			}
	  	}
		return closestNode;
	}
	
	/**
	* Get coordinates of a node
	*/
	public Position getCoordinates(String n){
		Position p = new Position();
		if (!n.equals("null")){
			if (allNodes.containsKey(n)){
				p.setPosition((String) allNodes.get(n));
			} else if (allEntities.containsKey(n)){
				Hashtable<String,String> t = ((Hashtable<String,String>) allEntities.get(n));
				p.setPosition((String) t.get("position"));
			} 
		} 
		return p;
	}
	
	/** 
	* Get the id of the way given the endnodes that make up a way
	*/
	public String getWayId(String node1, String node2){
		String streetId = "null";
		Enumeration<String> e = allWays.keys();
		while(e.hasMoreElements()){
			String temp1 = (String)e.nextElement();
			String[] nodes = ((String) allWays.get(temp1)).split(",");
			if (nodes[0].equals(node1) && nodes[1].equals(node2)){
				streetId = temp1;
				break;
			} else if (nodes[0].equals(node2) && nodes[1].equals(node1)){
				streetId = temp1;
				break;
			}				
		}
		return streetId;
	}
	
	/**
	* Get the ids of the Ways on which the given position p is.
	*/
	public ArrayList<String> getAdjacentWays(Position pos){
		ArrayList<String> temp = new ArrayList<String>();
		Enumeration<String> e = allWays.keys();
		while(e.hasMoreElements()){
			String temp1 = (String)e.nextElement();
			String[] nodes = ((String) allWays.get(temp1)).split(",");
			//System.out.println("" + temp1 + "," + nodes[0] + "," + nodes[1]);
			
			if(!allNodes.containsKey(nodes[0])||!allNodes.containsKey(nodes[1])){
				continue;
			}
			Position pos1 = new Position((String) allNodes.get(nodes[0]));
			Position pos2 = new Position((String) allNodes.get(nodes[1]));
			double distance = Tools.distance(pos1, pos2);
			
			//distance between node 1 and the point
			double distance1 = Tools.distance(pos1, pos);
			//distance between node 2 and the point
			double distance2 = Tools.distance(pos2, pos);
			double td = distance1 + distance2;
			
			double width = 0.02; //20 meters = width of the road (one side)
			double maxDistance = width + Math.sqrt(width*width + distance*distance);
			
			//System.out.println("Checking: " + nodes[0] + "," + nodes[1] + "," + maxDistance);
			//System.out.println("Orient: " + orient + ", Orient1: " + orient1);
			
			if(td < maxDistance){
				temp.add(getWayId(nodes[0],nodes[1]));				
			}
	  	}		
		
		return temp;
	}
	
	/** 
	* Get the location of the coordinate in terms of street names 
	* Returns a string of format street:XYZ or junction:X,Y,Z
	*/
	public ArrayList<String> getAdjacentStreetNames(Position p){
		ArrayList<String> adjWays = getAdjacentWays(p);
		ArrayList<String> wNames = new ArrayList<String>();
		//if there is only one way in the list..
		Iterator<String> i = adjWays.iterator();
		while (i.hasNext()){
			String temp = getWayName((String) i.next());
			if (!wNames.contains(temp)){ wNames.add(temp);}
		}
		return wNames;
	}
	
	
	/** 
	* Get all entities of given type and properties p
	*/
	public ArrayList<String> getAllEntitiesByType(String type, Hashtable<String,String> prop){
		ArrayList<String> entities = new ArrayList<String>();
		Enumeration<String> e = allEntities.keys();
		while(e.hasMoreElements()){
			boolean add = true;
			String temp = (String)e.nextElement();
			//System.out.println("Checking " + temp);
			Hashtable<String,String> t = ((Hashtable<String,String>) allEntities.get(temp));
			if (t.size() > 0){
				String eType = (String) t.get("type");
				if (eType.equals(type)){
					Enumeration<String> ekeys = prop.keys();
					while (ekeys.hasMoreElements()){
						String tempEKey = (String) ekeys.nextElement();
						if (t.containsKey(tempEKey)){
							if (((String) t.get(tempEKey)).equals((String) prop.get(tempEKey))){
								add = true;
							} else {
								add = false;
								break;
							}
						}
					}
					if (add == true){
						entities.add(temp);
					}
				}
			}
			
	  	}
		return entities;
	}
	
	/** 
	* Get a random entity given its type
	*/
	public String getRandomEntityByType(String type, Hashtable<String,String> prop){
		ArrayList<String> entities = getAllEntitiesByType(type, prop);
		int r = 0;
		String entityId = "null";
		if (entities.size() > 0) {
			r = (new Random()).nextInt(entities.size());
			entityId = entities.get(r); 
		}
		return entityId;
	}
	
	/** 
	* Get all entities that matches a given name
	*/
	public ArrayList<String> getEntityIdByName(String name){
		ArrayList<String> p = new ArrayList<String>();
		Enumeration<String> e = allEntities.keys();
		double d = 0;
		double limit = 5;
		while(e.hasMoreElements()){
			String temp = (String) e.nextElement();
			//System.out.println("Checking " + temp);
			Hashtable<String,String> t = ((Hashtable<String,String>) allEntities.get(temp));
			String tname = ((String) t.get("name")).toLowerCase();
			if (t.size() > 0){
				d = Tools.computeLevenshteinDistance(name, tname);
				if (d < limit){
					p.add(temp);
				}
			}
	  	}
		
		e = allWays.keys();
		while(e.hasMoreElements()){
			String temp1 = (String) e.nextElement();
			String[] n = ((String) allWays.get(temp1)).split(",");
			String tname = n[2].toLowerCase();
			d = Tools.computeLevenshteinDistance(name, tname);
			if (d < limit){
					p.add(n[0]);
					p.add(n[1]);
					//System.out.println(temp1 + "," + n[2]);
			}				
		}
		
		return p;
	}
	
	/** 
	* Get a coordinate of a street given the name
	*/
	public ArrayList<Position> geocode(String name){
		ArrayList<Position> p = new ArrayList<Position>();
		Enumeration<String> e = allEntities.keys();
		double d = 0;
		double limit = 5;
		while(e.hasMoreElements()){
			String temp = (String) e.nextElement();
			//System.out.println("Checking " + temp);
			Hashtable<String,String> t = ((Hashtable<String,String>) allEntities.get(temp));
			String tname = ((String) t.get("name")).toLowerCase();
			if (t.size() > 0){
				d = Tools.computeLevenshteinDistance(name, tname);
				if (d < limit){
					p.add(new Position((String)t.get("position")));
				}
			}
	  	}
		
		e = allWays.keys();
		while(e.hasMoreElements()){
			String temp1 = (String) e.nextElement();
			String[] n = ((String) allWays.get(temp1)).split(",");
			String tname = n[2].toLowerCase();
			d = Tools.computeLevenshteinDistance(name, tname);
			if (d < limit){
					p.add(getCoordinates(n[0]));
					p.add(getCoordinates(n[1]));
					System.out.println(temp1 + "," + n[2]);
			}				
		}
		
		return p;
	}
	
	//get the coordinates of any entity from its name
	/**public ArrayList<Position> geocodeGoogle(String name){
		double mapMinLat = 55.9332000;
		double mapMaxLat = 55.9635000;
		double mapMinLon = -3.2419000;
		double mapMaxLon = -3.1435000;
		
		ArrayList<Position> p = new ArrayList<Position>() ;
		try {
			String searchName = name + ", Edinburgh";
			p = Geocoder.geocode(searchName, mapMinLat, mapMinLon, mapMaxLat, mapMaxLon);
			if (p.size() == 0){
				ArrayList<String> nodes = getAllNodesOfStreet(name);
				if (nodes.size() > 0){
					Iterator<String> i = nodes.iterator();
					while(i.hasNext()){
						p.add(getCoordinates(i.next()));
					}
				} else {
					System.out.println("Destination not found");					
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return p;
	}*/
	
	/** 
	* Get all nodeids of a street
	*/
	public ArrayList<String> getAllNodesOfStreet(String sName){
		ArrayList<String> nIds = new ArrayList<String>();
		ArrayList<String> a = getWayId(sName);
		Iterator<String> it = a.iterator();
		while(it.hasNext()){
			ArrayList<String> b = getNodesOfWay((String) it.next());
			Iterator<String> it2 = b.iterator();
			while(it2.hasNext()){
				String n = (String) it2.next();
				if (!nIds.contains(n)){
					nIds.add(n);
				}
			}
		}
		return nIds;
	}

	/**
	* Get all neighbouring nodes of the given node
	*/
	public ArrayList<String> getReachableNodes(String FromNode){
		ArrayList<String> temp = new ArrayList<String>();
		
		Enumeration<String> e = allWays.keys();
		while(e.hasMoreElements()){
			String[] nodes = ((String) allWays.get(e.nextElement())).split(",");
			if(nodes[0].equals(FromNode)){
				temp.add(nodes[1]);
				//System.out.println(nodes[1]);
			} else if (nodes[1].equals(FromNode)){
				temp.add(nodes[0]);
				//System.out.println(nodes[0]);
			}
	  	}		
		return temp;		
	}
	
	/** 
	* Get all ways from the given node
	*/
	public ArrayList<String> getPossibleWaysFromNode(String Node){
		ArrayList<String> temp = new ArrayList<String>();
		
		Enumeration<String> e = allWays.keys();
		while(e.hasMoreElements()){
			String wayId = (String) e.nextElement();
			String[] nodes = ((String) allWays.get(wayId)).split(",");
			if(nodes[0].equals(Node) || nodes[1].equals(Node)){
				temp.add(wayId);
			} 
	  	}		
		return temp;
	}
	
	/**
	* Get the distance (as the crow flies) between 2 nodes
	*/
	public double getDistance(String From, String To){
		double d = 0;
		Position pos1 = new Position((String) allNodes.get(From));
		Position pos2 = new Position((String) allNodes.get(To));
		d = Tools.distance(pos1, pos2);
		return d;
	}
	
	/** 
	* Get the name of the way given its id
	*/
	public String getWayName(String wayId){
		if (!wayId.equals("null")){
			String[] temp = ((String) allWays.get(wayId)).split(",");
			return temp[2];
		} else {
			return "null";
		}		
	}
	
	/** 
	* Get all the way ids of the given street
	*/
	public ArrayList<String> getWayId(String wayName){
		ArrayList<String> wIds = new ArrayList<String>();
		Enumeration<String> e = allWays.keys();
		while(e.hasMoreElements()){
			String tempId = (String) e.nextElement();
			String[] nodes = ((String) allWays.get(tempId)).split(",");
			if(nodes[2].equals(wayName)){
				wIds.add(tempId);
			} 
	  	}
		return wIds;
	}
	
	/** 
	* Get the street names on which the given coordinate p is on 
	*/
	public ArrayList<String> getWayNameList(Position p){
		ArrayList<String> sNames = new ArrayList<String>();
		ArrayList<String> sids = getAdjacentWays(p);
		Iterator<String> i = sids.iterator();
		if(i.hasNext()){
			sNames.add(getWayName((String) i.next()));
		}
		return sNames;
	}
	
	/** 
	* Get the nodes connected by the given way
	*/
	public ArrayList<String> getNodesOfWay(String wayId){
		ArrayList<String> temp = new ArrayList<String>();
		if (allWays.containsKey(wayId)){
			String[] temp2 = ((String) allWays.get(wayId)).split(",");
			temp.add(temp2[0]);
			temp.add(temp2[1]);
		}
		return temp;
	}
	

	private Hashtable<String,Hashtable<String,String>> loadAllEntities(ArrayList<Node> ae){
		Hashtable<String,Hashtable<String,String>> temp = new Hashtable<String,Hashtable<String,String>>();
		Iterator<Node> an = ae.iterator();
        while(an.hasNext()){
        	Node a = (Node) an.next();
        	if (a.getNodeName().equals("entity")){
        		Hashtable<String,String> temp2 = new Hashtable<String,String>();
        		NamedNodeMap nnm = a.getAttributes();
        		Node ntype = nnm.getNamedItem("type");
        		Node cuisine_type = nnm.getNamedItem("cuisine_type");
        		Node nid = nnm.getNamedItem("id");
        		Node nlat = nnm.getNamedItem("lat");
	            Node nlon = nnm.getNamedItem("lon");
	            Node nname = nnm.getNamedItem("name");
	            String did = nid.getNodeValue();
	            temp2.put("position", nlat.getNodeValue() + "," + nlon.getNodeValue());
	            temp2.put("name", nname.getNodeValue());
	            temp2.put("type", ntype.getNodeValue());
	            if (cuisine_type != null){
	            	temp2.put("cuisine_type", cuisine_type.getNodeValue());
	            }
	            temp.put(did, temp2);
	        }
        }
        return temp;
	} 
	
	

	private Hashtable<String,String> loadAllNodes(ArrayList<Node> ae){
		Hashtable<String,String> temp = new Hashtable<String,String>();
		Iterator<Node> an = ae.iterator();
        while(an.hasNext()){
        	Node a = (Node) an.next();
        	if (a.getNodeName().equals("node")){
        		NamedNodeMap nnm = a.getAttributes();
	            Node nid = nnm.getNamedItem("id");
        		Node nlat = nnm.getNamedItem("lat");
	            Node nlon = nnm.getNamedItem("lon");
	            String did = nid.getNodeValue();
	            String pos = "" + nlat.getNodeValue() + "," + nlon.getNodeValue();
	            temp.put(did, pos);
	            //System.out.println("writing " + did + "," + pos);
        	}
        }
        return temp;
	} 
	
	private Hashtable<String,String> loadAllWays(ArrayList<Node> ae){
		Hashtable<String,String> temp = new Hashtable<String,String>();
		Iterator<Node> an = ae.iterator();
        while(an.hasNext()){
        	Node a = (Node) an.next();
        	if (a.getNodeName().equals("way")){
        		NamedNodeMap nnm = a.getAttributes();
	            Node nid = nnm.getNamedItem("id");
	            String did = nid.getNodeValue();
	            String dnid = "";
	            NodeList wnlist = a.getChildNodes();
	            for (int p=0; p < wnlist.getLength(); p++){
	            	Node b = wnlist.item(p);
	            	if (b.getNodeName().equals("nd")){
	            		//if (!dnid.equals("")){dnid += ",";}
    	            	Node wnid = (b.getAttributes()).getNamedItem("ref");
    	            	dnid += wnid.getNodeValue() + ",";
    	            }
	            	else if (b.getNodeName().equals("tag")){
	            		if((b.getAttributes()).getNamedItem("k").getNodeValue().equals("name")){
	            			String streetName = (b.getAttributes()).getNamedItem("v").getNodeValue();
	            			dnid += streetName;
	            		}
	            	}
	            }
	            temp.put(did, dnid);
	            //System.out.println("writing " + did + "," + dnid);
        	}
        }
        return temp;
	} 
	
	private ArrayList<Node> loadFromFile(File f){
		ArrayList<Node> an = new ArrayList<Node>();
		try {
		    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		    Document doc = docBuilder.parse(f);
		    // normalize text representation
	        doc.getDocumentElement ().normalize ();
	        //System.out.println ("Root element of the doc is " + doc.getDocumentElement().getNodeName());
	
	        // copy the contents of the OSM file into an arraylist "allNodes" so that we reduce time to access it..
	        NodeList results = doc.getDocumentElement().getChildNodes();
	        for(int s=0; s<results.getLength() ; s++){
	        	an.add(results.item(s));
	        }
	        
		} catch (SAXParseException err) {
			 System.out.println ("** Parsing error" + ", line " 
			 + err.getLineNumber () + ", uri " + err.getSystemId ());
			 System.out.println(" " + err.getMessage ());
		
		} catch (SAXException e) {
			Exception x = e.getException ();
			((x == null) ? e : x).printStackTrace ();
		} catch (Throwable t) {
			t.printStackTrace ();
		}
		return an;
	}

	/** 
	* Get the closest entity given the name of the entity and a position 
	*/
	public String getClosestEntityIdByName(String destinationName, Position currentUserCoor) {
		System.out.println("Desitnation name:" + destinationName);
		Double min = 99999.0;
		String closestEntity = "null"; 
		Iterator<String> allIds = getEntityIdByName(destinationName).iterator();
		if (allIds.hasNext()){
			String temp = allIds.next();
			Double d = Tools.distance(getCoordinates(temp), currentUserCoor);
			if (d < min){
				min = d;
				closestEntity = temp; 
			}
		}
		return closestEntity;
	}
	
	
}