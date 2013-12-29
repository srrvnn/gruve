import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import hw.macs.gruve.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

public class GruveServlet extends HttpServlet{
	Hashtable<String, GruveIM> allIMs;
	Hashtable<String, LogWriter> allLWs;
	Hashtable<String, UserModel> allUMs;
	public static final String fsep = System.getProperty("file.separator");
	
	public void init() throws ServletException{
		allIMs = new Hashtable<String, GruveIM>();
		allLWs = new Hashtable<String, LogWriter>();
		allUMs = new Hashtable<String, UserModel>();
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
		
		JSONObject uResponse = new JSONObject();
		//uResponse.put("userReward", 0.0);
		String currentUserEmail = "null";
		if (param.containsKey("userEmail")) {
			currentUserEmail = param.get("userEmail");
			uResponse.put("userEmail", currentUserEmail);
		}
		String currentSessionId = "null";
		if (param.containsKey("sessionId")) {
			currentSessionId = param.get("sessionId");
			uResponse.put("sessionId", currentSessionId);
		}
		String currentUserPos = "null";
		String currentUserUtt, currentUserDA;
		
		if (param.containsKey("userPosition")) {
			currentUserPos = param.get("userPosition");
			uResponse.put("userPosition", currentUserPos);
		}
		if (param.containsKey("userUtterance")) {
			currentUserUtt = param.get("userUtterance");
			currentUserUtt = currentUserUtt.replaceAll("%20", " ");
			if (currentUserUtt.equals("null")){	currentUserUtt = "";}
			uResponse.put("userUtterance", currentUserUtt);
		}		
		if (param.containsKey("userDA")) {
			currentUserDA = param.get("userDA");
			currentUserDA = currentUserDA.replaceAll("%22", "\"");
			currentUserDA = currentUserDA.replaceAll("%20", " ");
			uResponse.put("userDA", currentUserDA);
		}
		uResponse.put("userType", "user");
		
		
		HWCityModel cm;
		
		String currentDir = "C:\\Program Files\\Apache Software Foundation\\Tomcat 8.0" + fsep + "webapps" + fsep + "gruve" + fsep + "WEB-INF" + fsep;
		// String currentDir = ".."+ fsep + "webapps" + fsep + "gruve" + fsep + "WEB-INF" + fsep;
		
		//String currentDir = "..\\webapps\\gruve\\WEB-INF\\";
		String classesDir = currentDir + "classes" + fsep;
		
		cm = new HWCityModel(new File(classesDir + "mymap.osm"));
		
		
		String userModelsDir = classesDir + "usermodels" + fsep;
		UserModel currentUM;
		
		
		GruveIM currentIM;
		LogWriter currentLW;
		
		if (!allIMs.containsKey(currentSessionId) && !currentSessionId.equals("null")){
			System.out.println("Creating new SB for " + currentSessionId);
			currentIM = new GruveIM(currentSessionId, currentUserEmail);
			allIMs.put(currentSessionId, currentIM);
			String iLogsDir = "C:\\Program Files\\Apache Software Foundation\\Tomcat 8.0" + fsep + "webapps" + fsep + "gruve" + fsep + "ilogs" + fsep;
			currentLW = new LogWriter(iLogsDir + "im-" + currentSessionId);
			allLWs.put(currentSessionId, currentLW);	
			currentUM = new UserModel(userModelsDir, currentSessionId, currentUserEmail);
			allUMs.put(currentSessionId, currentUM);
		} else {
			currentIM = (GruveIM) allIMs.get(currentSessionId);
			currentLW = (LogWriter) allLWs.get(currentSessionId);
			currentUM = (UserModel) allUMs.get(currentSessionId);
		}
		
		if (!currentUserPos.equals("null")){
			currentUserPos = currentUserPos.replace(";", ",");
			String closestNodeId = cm.getClosestNode(new Position(currentUserPos));
			System.out.println("Closest node to the user: " + closestNodeId);
			currentUM.addToUserRoute(closestNodeId);
		}
		
		currentLW.log(uResponse.toString());
		sResponse = currentIM.run(uResponse.toString());
		String sysUtterance = "";
		JSONObject sysResponse = (JSONObject) JSONValue.parse(sResponse);
		sysResponse.put("userType", "im");
		if(sysResponse.containsKey("utterance")){
			sysUtterance = (String) sysResponse.get("utterance");	
		}
		currentLW.log(sysResponse.toString());
		System.out.println("SysUtt (from servlet):" + sysUtterance);
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		out.println(sysUtterance);
		
		
		
		
	}

	public void destroy(){
		
	}
}