package data.dataModel;

import java.util.ArrayList;

public class Street {

	private ArrayList<Coordinate> coordinates;
	private int id;
	private String access, area, bridge;
	private long osmidStart, osmidDest;
	private String highway, junction;
	private int key;
	private ArrayList<Integer> arrayLanes;
	private double lenght;
	private String maxSpeed, name;
	private boolean oneWay;
	private ArrayList<Long> osmidEdges;
	private String ref, service, tunnel, width;
	private int origId;
	private double weight;
	private double flow, averageTravelTime;

	/**
	 * 
	 * @param coordinates       Describes Geometry of the street.
	 * @param id                Local id of the Street.
	 * @param access            Describes access condition of the street.
	 * @param area
	 * @param bridge            Describes if it is present a bridge or a viaduct.
	 * @param osmidStart        OpenStreetMap id of the starting intersection.
	 * @param osmidDest         OpenStreetMap id of the destination intersection.
	 * @param highway           Shows the type of the street within the road network
	 * @param junction          Describes junction type in road network
	 * @param key
	 * @param arrayLanes        Lanes number
	 * @param lenght            Lenght in meter of the street.
	 * @param maxSpeed          Speed limit of the street in km/h.
	 * @param name              Name of the Street.
	 * @param oneWay            Describe if street is a oneway street or not.
	 * @param osmidEdges        List of OSM nodes crossed and incorporated in the
	 *                          street.
	 * @param ref               Describes if the street has an exit with a specific number assigned to it.
	 * @param service           Describe if some services such as bus rides are
	 *                          active.
	 * @param tunnel            Describes if it is present a tunnel.
	 * @param width             Width of street in meters.
	 * @param origId            Id used to elaborations.
	 * @param weight            Weight value of links for graph elaborations.
	 * @param flow              Flow value used for graph elaborations.
	 * @param averageTravelTime Average travel time of street.
	 */
	public Street(ArrayList<Coordinate> coordinates, int id, String access, String area, String bridge, long osmidStart,
			long osmidDest, String highway, String junction, int key, ArrayList<Integer> arrayLanes, double lenght,
			String maxSpeed, String name, boolean oneWay, ArrayList<Long> osmidEdges, String ref, String service,
			String tunnel, String width, int origId, double weight, double flow, double averageTravelTime) {
		super();
		this.coordinates = coordinates;
		this.id = id;
		this.access = access;
		this.area = area;
		this.bridge = bridge;
		this.osmidStart = osmidStart;
		this.osmidDest = osmidDest;
		this.highway = highway;
		this.junction = junction;
		this.key = key;
		this.arrayLanes = arrayLanes;
		this.lenght = lenght;
		this.maxSpeed = maxSpeed;
		this.name = name;
		this.oneWay = oneWay;
		this.osmidEdges = osmidEdges;
		this.ref = ref;
		this.service = service;
		this.tunnel = tunnel;
		this.width = width;
		this.origId = origId;
		this.weight = weight;
		this.flow = flow;
		this.averageTravelTime = averageTravelTime;
	}

	/**
	 * Consturctor with default (0) weight value 
	 * 
	 * @param coordinates       Describes Geometry of the street.
	 * @param id                Local id of the Street.
	 * @param access            Describes access condition of the street.
	 * @param area
	 * @param bridge            Describes if it is present a bridge or a viaduct.
	 * @param osmidStart        OpenStreetMap id of the starting intersection.
	 * @param osmidDest         OpenStreetMap id of the destination intersection.
	 * @param highway           Shows the type of the street within the road network
	 * @param junction          Describes junction type in road network
	 * @param key
	 * @param arrayLanes        Lanes number
	 * @param lenght            Lenght in meter of the street.
	 * @param maxSpeed          Speed limit of the street in km/h.
	 * @param name              Name of the Street.
	 * @param oneWay            Describe if street is a oneway street or not.
	 * @param osmidEdges        List of OSM nodes crossed and incorporated in the
	 *                          street.
	 * @param ref               Describes if the exit has a specific number assigned
	 *                          to it.
	 * @param service           Describe if some services such as bus rides are
	 *                          active.
	 * @param tunnel            Describes if it is present a tunnel.
	 * @param width             Width of street in meters.
	 * @param origId            Id used to elaborations.
	 * @param flow              Flow value used for graph elaborations.
	 * @param averageTravelTime Average travel time of street.
	 */
	public Street(ArrayList<Coordinate> coordinates, int id, String access, String area, String bridge, long osmidStart,
			long osmidDest, String highway, String junction, int key, ArrayList<Integer> arrayLanes, double lenght,
			String maxSpeed, String name, boolean oneWay, ArrayList<Long> osmidEdges, String ref, String service,
			String tunnel, String width, int origId, double flow, double averageTravelTime) {
		super();
		this.coordinates = coordinates;
		this.id = id;
		this.access = access;
		this.area = area;
		this.bridge = bridge;
		this.osmidStart = osmidStart;
		this.osmidDest = osmidDest;
		this.highway = highway;
		this.junction = junction;
		this.key = key;
		this.arrayLanes = arrayLanes;
		this.lenght = lenght;
		this.maxSpeed = maxSpeed;
		this.name = name;
		this.oneWay = oneWay;
		this.osmidEdges = osmidEdges;
		this.ref = ref;
		this.service = service;
		this.tunnel = tunnel;
		this.width = width;
		this.origId = origId;
		this.weight = 0;
		this.flow = flow;
		this.averageTravelTime = averageTravelTime;
	}

	public ArrayList<Coordinate> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(ArrayList<Coordinate> coordinates) {
		this.coordinates = coordinates;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAccess() {
		return access;
	}

	public void setAccess(String access) {
		this.access = access;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getBridge() {
		return bridge;
	}

	public void setBridge(String bridge) {
		this.bridge = bridge;
	}

	public long getOsmidStart() {
		return osmidStart;
	}

	public void setOsmidStart(long osmidStart) {
		this.osmidStart = osmidStart;
	}

	public long getOsmidDest() {
		return osmidDest;
	}

	public void setOsmidDest(long osmidDest) {
		this.osmidDest = osmidDest;
	}

	public String getHighway() {
		return highway;
	}

	public void setHighway(String highway) {
		this.highway = highway;
	}

	public String getJunction() {
		return junction;
	}

	public void setJunction(String junction) {
		this.junction = junction;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public ArrayList<Integer> getArrayLanes() {
		return arrayLanes;
	}

	public void setArrayLanes(ArrayList<Integer> arrayLanes) {
		this.arrayLanes = arrayLanes;
	}

	public double getLenght() {
		return lenght;
	}

	public void setLenght(double lenght) {
		this.lenght = lenght;
	}

	public String getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(String maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isOneWay() {
		return oneWay;
	}

	public void setOneWay(boolean oneWay) {
		this.oneWay = oneWay;
	}

	public ArrayList<Long> getOsmidEdges() {
		return osmidEdges;
	}

	public void setOsmidEdges(ArrayList<Long> osmidEdges) {
		this.osmidEdges = osmidEdges;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getTunnel() {
		return tunnel;
	}

	public void setTunnel(String tunnel) {
		this.tunnel = tunnel;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public int getOrigId() {
		return origId;
	}

	public void setOrigId(int origId) {
		this.origId = origId;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getFlow() {
		return flow;
	}

	public void setFlow(double flow) {
		this.flow = flow;
	}

	public double getAverageTravelTime() {
		return averageTravelTime;
	}

	public void setAverageTravelTime(double averageTravelTime) {
		this.averageTravelTime = averageTravelTime;
	}

	@Override
	public String toString() {
		return "Street [coordinates=" + coordinates + ", id=" + id + ", access=" + access + ", area=" + area
				+ ", bridge=" + bridge + ", osmidStart=" + osmidStart + ", osmidDest=" + osmidDest + ", highway="
				+ highway + ", junction=" + junction + ", key=" + key + ", arrayLanes=" + arrayLanes + ", lenght="
				+ lenght + ", maxSpeed=" + maxSpeed + ", name=" + name + ", oneWay=" + oneWay + ", osmidEdges="
				+ osmidEdges + ", ref=" + ref + ", service=" + service + ", tunnel=" + tunnel + ", width=" + width
				+ ", origId=" + origId + ", weight=" + weight + ", flow=" + flow + ", averageTravelTime="
				+ averageTravelTime + "]";
	}

}