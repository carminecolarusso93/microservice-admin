package data.dataModel;

import java.util.HashMap;



public class Intersection {

	private Coordinate coordinate;
	private String highway;
	private long osmid;
	private String ref;
	private double betweenness;
	private HashMap<Integer, Street> streets;

	/**
	 * @param coordinate  Longitude and Latitude of Intersection in Decimal Degrees
	 *                    (DD).
	 * @param highway     Shows the type of the intersection within the road
	 *                    network.
	 * @param osmid       OpenStreetMap Id of the intersection in the road network
	 * @param ref
	 * @param betweenness The start value of Betweenness Centrality of the
	 *                    Intersection
	 */
	public Intersection(Coordinate coordinate, String highway, long osmid, String ref, double betweenness) {
		super();
		this.coordinate = coordinate;
		this.highway = highway;
		this.osmid = osmid;
		this.ref = ref;
		this.betweenness = betweenness;
		this.streets = null;
	}

	/**
	 * 
	 * @param coordinate  Longitude and Latitude of Intersection in Decimal Degrees
	 *                    (DD).
	 * @param highway     Shows the type of the intersection within the road
	 *                    network.
	 * @param osmid       OpenStreetMap Id of the intersection in the road network
	 * @param ref
	 * @param betweenness The start value of Betweenness Centrality of the
	 *                    Intersection
	 * @param streets     Streets coming out of the intersection.
	 */
	public Intersection(Coordinate coordinate, String highway, long osmid, String ref, double betweenness,
			HashMap<Integer, Street> streets) {
		super();
		this.coordinate = coordinate;
		this.highway = highway;
		this.osmid = osmid;
		this.ref = ref;
		this.betweenness = betweenness;
		this.streets = streets;
	}

	public Intersection(Coordinate coordinate, String highway, long osmid, String ref) {
		super();
		this.coordinate = coordinate;
		this.highway = highway;
		this.osmid = osmid;
		this.ref = ref;
		this.betweenness = 0;
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}

	public String getHighway() {
		return highway;
	}

	public void setHighway(String highway) {
		this.highway = highway;
	}

	public long getOsmid() {
		return osmid;
	}

	public void setOsmid(long osmid) {
		this.osmid = osmid;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public double getBetweenness() {
		return betweenness;
	}

	public void setBetweenness(double beetweeness) {
		this.betweenness = beetweeness;
	}

	public HashMap<Integer, Street> getStreets() {
		return streets;
	}

	public void setStreets(HashMap<Integer, Street> streets) {
		this.streets = streets;
	}

	@Override
	public String toString() {
		return "Intersection [coordinate=" + coordinate + ", highway=" + highway + ", osmid=" + osmid + ", ref=" + ref
				+ ", betweenness=" + betweenness + ", streets=" + streets + "]";
	}

}