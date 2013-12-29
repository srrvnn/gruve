
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class WoZLogListServlet extends HttpServlet{
	String fileName;
	BufferedReader bin;
	public static final String fsep = System.getProperty("file.separator");
	
	public void init() throws ServletException{
		
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException{ 
		String sResponse = "null";
		Hashtable<String,String> param = new Hashtable<String,String>();
		String in = req.getQueryString();
		/*String[] inparts = in.split("&");
		for (int i = 0; i < inparts.length; i++){
			String[] eachpart = inparts[i].split("=");
			param.put(eachpart[0], eachpart[1]);
		}*/
		
		String strLine;
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		String iLogsDir = ".."+ fsep + "webapps" + fsep + "gruve" + fsep + "ilogs" + fsep;
		File dir = new File(iLogsDir);

		String[] children = dir.list();
		if (children == null) {
		    // Either dir does not exist or is not a directory
		} else {
		    for (int i=0; i<children.length; i++) {
		        // Get filename of file or directory
		        String filename = children[i];
		        //System.out.println(filename);
				out.println(filename);
		    }
		}
	}
	
	public void destroy(){
		
	}
}