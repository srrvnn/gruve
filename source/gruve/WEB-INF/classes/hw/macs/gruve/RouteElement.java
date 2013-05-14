package hw.macs.gruve;


public class RouteElement{
	public Integer index;
	public String edgeId;
	public String wayName;
	public Double distance;
	public String wayType;
	public String nodeId;
	public Double nextTurnAngle;
	public String nextSlope;
	
	RouteElement(){}
	
	public String toString(){
		return index + " NodeId: " + nodeId + " WayId: " + edgeId + " WayName: " + wayName + " Dis: " + distance; 
	}
	
	public String getNodeId(){ return nodeId; }
	public Integer getIndex() { return index;}
	public String getWayName(){ return wayName; }
}