package hw.macs.gruve;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

class Geocoder {
	
	public static ArrayList<Position> geocode(String address, double mapMinLat, double mapMinLon, double mapMaxLat, double mapMaxLon) throws IOException{
		
		address = address.replaceAll(" ", "+");
		String urltext = "http://nominatim.openstreetmap.org/search?q=" + address + "&format=xml";
		// http://wiki.openstreetmap.org/wiki/Nominatim
		URL url = new URL(urltext);
		try{ 
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String inputLine;
			BufferedWriter out = new BufferedWriter(new FileWriter("logs/destFile.xml"));
			
			while ((inputLine = in.readLine()) != null) {
				// Process each line.
				out.write(inputLine);
				//System.out.println(inputLine);
			}
			in.close();
			out.close();
		} catch(IOException e){
			e.printStackTrace();
		}
		ArrayList<Position> c = parse4coor("logs/destFile.xml", mapMinLat, mapMinLon, mapMaxLat, mapMaxLon);
		return c;
	}
	
    private static ArrayList<Position> parse4coor(String f, double mapMinLat, double mapMinLon, double mapMaxLat, double mapMaxLon){
    	ArrayList<Position> clist = new ArrayList<Position>();
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse (new File(f));

            // normalize text representation
            doc.getDocumentElement ().normalize ();
            NodeList results = doc.getDocumentElement().getChildNodes();
           // System.out.print("https://maps.googleapis.com/maps/api/staticmap?zoom=15&size=1200x1000&markers=size:mid|");
            for(int i=0;i<results.getLength();i++){
            	Node topResult = results.item(i);
            	NamedNodeMap nnm = topResult.getAttributes();
            	float lat = Float.valueOf((nnm.getNamedItem("lat")).getNodeValue());
            	float lon = Float.valueOf((nnm.getNamedItem("lon")).getNodeValue());
				if (lat > mapMinLat && lat < mapMaxLat && lon > mapMinLon && lon < mapMaxLon) {
					clist.add(new Position(lat, lon));
					//System.out.print("" + lat + "," + lon + "|");
				}	
            }
			//System.out.println("&sensor=false");
        } catch (SAXParseException err) {
        System.out.println ("** Parsing error" + ", line " 
             + err.getLineNumber () + ", uri " + err.getSystemId ());
        System.out.println(" " + err.getMessage ());

        }catch (SAXException e) {
        Exception x = e.getException ();
        ((x == null) ? e : x).printStackTrace ();

        }catch (Throwable t) {
        t.printStackTrace ();
        }
        return clist;
 
    }//end of parse

    public static String reverseGeocode(String coor, String level) throws IOException{
    	
    	//get the address for a given coordinate coor at a level (e.g. road) 
    	//http://nominatim.openstreetmap.org/reverse?format=xml&lat=52.5487429714954&lon=-1.81602098644987&zoom=18&addressdetails=1
		
    	String[] temp = coor.split(",");
		String urltext = "http://nominatim.openstreetmap.org/reverse?format=xml&lat="+temp[0]+"&lon="+ temp[1]+"&zoom=18&addressdetails=1";
		URL url = new URL(urltext);
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		String inputLine;
		BufferedWriter out = new BufferedWriter(new FileWriter("logs/whereFile.xml"));
		
		while ((inputLine = in.readLine()) != null) {
			// Process each line.
			out.write(inputLine);
			//System.out.println(inputLine);
		}
		in.close();
		out.close();
		String address = parse4address("logs/whereFile.xml", level);
		return address;
	}
	

    private static String parse4address(String f, String q){
    	String address = "";
    	String[] qs = q.split(",");
    	
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse (new File(f));

            // normalize text representation
            doc.getDocumentElement ().normalize ();
            //System.out.println(doc.getDocumentElement().getNodeName());
            
            NodeList results = doc.getDocumentElement().getChildNodes();
            //System.out.println ("No. of results " + results.getLength());
            
            NodeList addressparts = results.item(1).getChildNodes();
            int l = addressparts.getLength();
            int j = 0;
            while (j < qs.length){
	            for (int i = 0; i<l; i++){
	            	Element e = (Element) addressparts.item(i);
	            	if (e.getNodeName().equals(qs[j])){
	            		address += e.getTextContent() + ", ";
	            		break;
	            	}
	            }
	            j++;
            }
            if (address.endsWith(", ")){
            	int in = address.lastIndexOf(", ");
            	address = address.substring(0, in);
            }
            if (address.equals("")){
            	address = "unknown";
            }
        } catch (SAXParseException err) {
        System.out.println ("** Parsing error" + ", line " 
             + err.getLineNumber () + ", uri " + err.getSystemId ());
        System.out.println(" " + err.getMessage ());

        }catch (SAXException e) {
        Exception x = e.getException ();
        ((x == null) ? e : x).printStackTrace ();

        }catch (Throwable t) {
        t.printStackTrace ();
        }
    return address;
 
    }//end of parse

    
}