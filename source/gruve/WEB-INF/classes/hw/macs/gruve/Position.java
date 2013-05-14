package hw.macs.gruve;

public class Position {
	protected double lat;
	protected double lon;

	public Position(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;
	}
	
	public Position(String s) {
		String[] t = s.split(",");
		this.lat = Double.valueOf(t[0]);
		this.lon = Double.valueOf(t[1]);
	}
	
	public Position() {
		this.lat = 0;
		this.lon = 0;
	}
	
	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLat(double lat1) {
		this.lat = lat1;
	}

	public void setLon(double lon1) {
		this.lon = lon1;
	}
	
	public void setPosition(double lat1, double lon1) {
		try{
			this.lat = lat1;
			this.lon = lon1;
		} catch (NumberFormatException e){
			e.printStackTrace();
		}
	}

	public void setPosition(String s) {
		String[] t = s.split(",");
		this.lat = Double.valueOf(t[0]);
		this.lon = Double.valueOf(t[1]);
	}
	public boolean equals(Position p2){
		if (this.lat == p2.getLat() && this.lon == p2.getLon()){
			return true;
		} else {
			return false;
		}
	}
	
	public String toString(){
		return "" + this.lat + "," + this.lon;
	}

}