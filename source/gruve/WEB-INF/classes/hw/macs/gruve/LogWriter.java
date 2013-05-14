package hw.macs.gruve;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogWriter{
	private BufferedWriter out;
        //XKL		
        private DateFormat timeStampFormat;	
	public LogWriter(String name){
		//startTime.replaceAll(" ", "-");
		try{
			  out = new BufferedWriter(new FileWriter(name + ".txt"));
			  // System.out.println("Log created with "+ name);
		} catch (IOException e){
			System.err.println("IO Exception: Cannot create file " + name + ".txt");
		}
                //XKL
                timeStampFormat = new SimpleDateFormat("ddMMyyyy_HH.mm.ss.SSSZ");
                //timeStampFormat = new SimpleDateFormat("ddMMyyyy_HH.mm.ss.SSS");                
	}
	
	public void log(String message){
               //XKL 
                String crntTimeStamp = timeStampFormat.format(new Date());                
		try{						
			out.write(crntTimeStamp + "\t" + message + "\n");
                        out.flush(); // XKL added.
			// System.out.println(message);
		} 	
		catch (IOException e){
			System.out.println(e.getMessage());
		}
	}
	
	public void close(){
		try {
			out.close();
		} catch (IOException e){
			System.out.println("IO Exception");
		}		
	}
}