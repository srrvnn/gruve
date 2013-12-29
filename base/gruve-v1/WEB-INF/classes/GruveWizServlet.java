
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import hw.macs.gruve.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

public class GruveWizServlet extends HttpServlet{
	JSONObject currentUserInput, currentWizInput;
	LogWriter l;
	UserModel um;
	String lastUser;
	public static final String fsep = System.getProperty("file.separator");

	// set the absolute path for your application here : 
	public static final String root = "C:\\Program Files\\Apache Software Foundation\\Tomcat 7.0\\webapps\\gruve";
	
	public void init() throws ServletException{
		currentUserInput = new JSONObject();
		currentWizInput = new JSONObject();
	}
	
	@SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException{ 
		String sResponse = "null";
		Hashtable<String,String> param = new Hashtable<String,String>();
		String in = req.getQueryString();
		String[] inparts = in.split("&");
		for (int i = 0; i < inparts.length; i++){
			String[] eachpart = inparts[i].split("=");
			param.put(eachpart[0], eachpart[1]);
		}	
		
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		if (param.containsKey("userType")){
			String userType = param.get("userType");
			if (userType.equals("wizard")){
				
				//step1: update wizard state
				System.out.println("Wizard:" + param.toString());
				if (param.containsKey("wizUtterance")){
					currentWizInput.put("userType", param.get("userType"));
					currentWizInput.put("utterance", param.get("wizUtterance"));
					if (l != null){
						//l.log(param.toString());
						l.log(currentWizInput.toString());
					}
				}
				//step2: return user state
				out.println(currentUserInput.toString());
			}
			else {
				String sessionId = param.get("sessionId");
				HWCityModel cm;
				String currentDir = root + fsep + "WEB-INF" + fsep;	
				String classesDir = currentDir + "classes" + fsep;
		
				cm = new HWCityModel(new File(classesDir + "mymap.osm"));
				
				if ((lastUser != null && lastUser.equals(sessionId))||(lastUser == null)){
					String userEmail = param.get("userEmail");
					if (lastUser == null){
						String iLogsDir = root + fsep + "ilogs" + fsep;
				
						l = new LogWriter(iLogsDir+ "woz-" + sessionId);
						String userModelsDir = classesDir + "usermodels" + fsep;
						um = new UserModel(userModelsDir, sessionId, userEmail);
					}
					String currentUserPos = param.get("userPosition");
					if (!currentUserPos.equals("null")){
						currentUserPos = currentUserPos.replace(";", ",");
						String closestNodeId = cm.getClosestNode(new Position(currentUserPos));
						System.out.println("Closest node to the user: " + closestNodeId);
						um.addToUserRoute(closestNodeId);
					}
					
					//step1: update user state
					System.out.println("WozUser:" + param.toString());
					//l.log(param.toString());
					currentUserInput.put("userType", param.get("userType"));
					currentUserInput.put("userDA", param.get("userDA"));
					//currentUserInput.put("userUtterance", param.get("userUtterance"));
					currentUserInput.put("userPosition", param.get("userPosition"));
					currentUserInput.put("userId", param.get("userId"));
					l.log(currentUserInput.toString());
					//step2: return wizard state
					out.println(currentWizInput.get("utterance"));
					lastUser = sessionId;
				} else {
					l.close();
					lastUser = null;
				}
			}
		}
	}

	public void destroy(){
		
	}
}