package hw.macs.gruve;


import java.util.*;
import java.io.*;
import org.json.simple.JSONObject;

public class UserModel{
	Hashtable<String, String> allRoutes;
	Hashtable<String, Integer> nodeVisits;
	String userEmail;
	String umfile;
	ArrayList<String> currentUserRoute;
	String sessionId; 
	
	public UserModel(String dir, String sessionId, String userEmail){
		System.out.println("Loading user model for: " + userEmail);
		this.userEmail = userEmail;
		this.sessionId = sessionId;
		userEmail = userEmail.replace("@", "-at-");
		userEmail = userEmail.replace(".", "-dot-");
		umfile = dir + "um-" + userEmail +".txt";
		nodeVisits = new Hashtable<String, Integer>();
		allRoutes = new Hashtable<String, String>();
		currentUserRoute = new ArrayList<String>();
		loadUMfile(umfile);
	}
	
	public void addToUserRoute(String nodeId){
		int size = currentUserRoute.size();
		System.out.println("UM: Size: " + size);
		if (size == 0){
			currentUserRoute.add(nodeId);
			System.out.println("UM: New route. Added " + nodeId + " to user route");
		} else if (!currentUserRoute.get(size - 1).equals(nodeId)){
			currentUserRoute.add(nodeId);
			System.out.println("UM: Added" + nodeId + " to user route");
		} 
		updateUMfile();
		System.out.println("UM: Current user route: " + currentUserRoute.toString());
	}
	
	
	
	public Integer getNodeVisits(String nodeId){
		if (nodeVisits.containsKey(nodeId)){
			return (Integer) nodeVisits.get(nodeId);
		} else {
			return 0;
		}		
	}
	
	public void setNodeVisits(String nodeId, Integer i){
		nodeVisits.put(nodeId, i);
	}
	
	public void incrementNodeVisit(String nodeId){
		if (nodeVisits.containsKey(nodeId)){
			Integer t = getNodeVisits(nodeId);
			setNodeVisits(nodeId, t+1);
		} else {
			setNodeVisits(nodeId, 1);
		}
		System.out.println("Node : " + nodeId + " visited: " + getNodeVisits(nodeId));
		
	}
	
	private void loadUMfile(String filename){
		
		try{
			if ((new File(filename)).exists()){
			
				FileInputStream fstream = new FileInputStream(filename);
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String strLine;
				while ((strLine = br.readLine()) != null){
					if (!strLine.equals("")){
						System.out.println("Read: " + strLine);
						String[] t = strLine.split(":");
						allRoutes.put(t[0], t[1]);
						String[] temp = t[1].split(",");
						for (int i = 0; i < temp.length; i++){
							incrementNodeVisit(temp[i]);
						}					
					}
				}
				in.close();
			} else {
				BufferedWriter out = new BufferedWriter(new FileWriter(umfile));
				out.close();
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		  
	}
	
	private void updateUMfile(){
		try{
			BufferedWriter out = new BufferedWriter(new FileWriter(umfile));
			System.out.println("Log created");
			Enumeration e  = allRoutes.keys();
			while (e.hasMoreElements()){
				String t = (String) e.nextElement(); 
				out.write(t + ":" + allRoutes.get(t) + "\n");
			}
			
			String route = "";
			for (String s : currentUserRoute)
				route += s + ",";
			route = route.substring(0, route.length()-1); //removing the last comma
			System.out.println("Adding current route: " + route);
			out.write(sessionId + ":" + route + "\n");
            out.flush(); 
			out.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
}