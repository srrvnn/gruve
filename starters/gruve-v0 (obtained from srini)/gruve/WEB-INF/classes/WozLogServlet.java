

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import hw.macs.gruve.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

public class WoZLogServlet extends HttpServlet{
	String fileName, userId;
	BufferedReader bin;

	public static final String fsep = System.getProperty("file.separator");
	
	public void init() throws ServletException{
		
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException{ 
		String sResponse = "null";
		Hashtable<String,String> param = new Hashtable<String,String>();
		String in = req.getQueryString();
		String[] inparts = in.split("&");
		for (int i = 0; i < inparts.length; i++){
			String[] eachpart = inparts[i].split("=");
			param.put(eachpart[0], eachpart[1]);
		}	
		
		String strLine;
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		
		try {
			if (param.containsKey("fileName")){
				String tempFile = param.get("fileName");
				String state = param.get("state");
				
				if (fileName == null || !fileName.equals(tempFile) || state.equals("new")){
					String iLogsDir = ".."+ fsep + "webapps" + fsep + "gruve" + fsep + "ilogs" + fsep;
					bin = new BufferedReader(new FileReader(iLogsDir + tempFile));
					fileName = tempFile;
				}
				if (bin != null && (strLine = bin.readLine()) != null)   {
					String[] a = strLine.split("\t");
					System.out.println (strLine);
					if (a.length > 0){
						out.println(a[1]);
					} else {
						out.println("null");
					}
				} else {
					bin.close();
					out.println("null");
				}
			} else {
				out.println("null");
			}
		} catch (IOException e){
			out.println("null");
		}
	}

	public void destroy(){
		
	}
}