package application.databaseManagementService;

import data.dataModel.*;
import data.databaseDriver.*;
import util.ServerUtilities;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import javax.ejb.Stateless;



/**
 * Session Bean implementation class DatabaseManagementService.
 * <p>
 * Encapsulates the driver to access the traffic management database with
 * administration role.
 */

@Stateless
public class DatabaseManagementService implements DatabaseManagementServiceRemote, DatabaseManagementServiceLocal {

	DriverDatabase database;
	protected String databeseURI = null;
	protected String databaseUser = null;
	protected String databasePass = null;
	/**
	 * Default constructor.
	 * <p>
	 * Instantiate a driver to access the Neo4j Graph Database for admin user with
	 * default bolt uri and credentials.
	 * @throws FileNotFoundException 
	 */
	public DatabaseManagementService() throws FileNotFoundException {
		try {
			ServerUtilities serverUtilities = new ServerUtilities();
			this.databeseURI = serverUtilities.getDatabaseUri();
			this.databaseUser = serverUtilities.getDatabaseUser();
			this.databasePass = serverUtilities.getDatabasePass();
			database = new DriverDatabaseNeo4j(databeseURI, databaseUser, databasePass);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Called after the EJB construction. Open the connection to the database.
	 */
	@PostConstruct
	public void connect() {
		database.openConnection();
	}

	/**
	 * Called before the EJB destruction. Close the connection to the database.
	 */
	@PreDestroy
	public void preDestroy() {
		database.closeConnection();
	}

	@Override
	public String test() {
		try {
			ServerUtilities serverUtilities = new ServerUtilities();
			return serverUtilities.getDatabaseUri();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Intersection addIntersection(Coordinate c, String highway, long osmid, String ref) {
		
		Intersection i = database.addIntersection(c, highway, osmid, ref);
//		double bet = database.updateBetweeennessBrandesDegree(osmid);
//		i.setBeetweeness(bet);
		return i;
	}
	
	@Override
	public Street addStreet(ArrayList<Coordinate>coordinates, int id, String access, String area, String bridge, long osmidStart,
			long osmidDest, String highway, String junction, int key, ArrayList<Integer> arrayLanes, double length,
			String maxSpeed, String name, boolean oneWay, ArrayList<Long> osmidEdges, String ref, String service,
			String tunnel, String width, int origId, double weight,double flow,double averageTravelTime) {
		Street s = database.addStreet(coordinates, id, access, area, bridge, osmidStart, osmidDest, highway, junction, key,
				arrayLanes, length, maxSpeed, name, oneWay, osmidEdges, ref, service, tunnel, width, origId, weight,flow,averageTravelTime);
		database.updateBetweenness();
		return s;
	}

//	@Override
//	public Intersection setIntersection(int vertexKey, String name, float lat, float lon, float betweenness) {
//		return database.setIntersection(vertexKey, name, lat, lon, betweenness);
//	}

	@Override
	public Street setStreetWeight(int id, double weight) {
		return database.setStreetWeight(id, weight);
	}

	@Override
	public Intersection setBetweennessIntersection(long osmid, double betweennees) {
		return database.setBetweennessIntersection(osmid, betweennees);
	}

	@Override
	public Intersection getIntersection(long osmid) {
		return database.getIntersection(osmid);
	}

	@Override
	public Street getStreet(int id) {
		return database.getStreet(id);
	}

	
	@Override
	public HashMap<Integer, Street> getStreets(long osmid) {
		return database.getStreets(osmid);
	}

	@Override
	public Street getStreet(long osmidStart, long osmidDest) {
		return database.getStreet(osmidStart, osmidDest);
	}
	@Override
	public void deleteIntersection(long osmid) {
		database.deleteIntersection(osmid);

	}

	@Override
	public void deleteStreet(int id) {
		database.deleteStreet(id);

	}

	@Override
	public double nodeFlow(long osmid) {
		return database.nodeFlow(osmid);
	}

	@Override
	public int getLinkKey(long osmidStart, long osmidDest) {
		return database.getLinkKey(osmidStart, osmidDest);
	}

	@Override
	public void updateBetweennesExact() {
		database.updateBetweennesExact();
		
	}

//	@Override
//	public double updateBetweennesExact(long osmid) {
//		return database.updateBetweennesExact(osmid);
//	}

	@Override
	public void updateBetweeennessBrandesRandom() {
		database.updateBetweeennessBrandesRandom();
	}

//	@Override
//	public double updateBetweeennessBrandesRandom(long osmid) {
//		return database.updateBetweeennessBrandesRandom(osmid);
//	}

	@Override
	public void updateBetweeennessBrandesDegree() {
		database.updateBetweeennessBrandesDegree();
		
	}

//	@Override
//	public double updateBetweeennessBrandesDegree(long osmid) {
//		return database.updateBetweeennessBrandesDegree(osmid);
//	}

	

	@Override
	public void updateBetweenness() {
		database.updateBetweenness();
		
	}

	@Override
	public LocalDateTime getLastModified() {
		return database.getLastModified();
	}

	
}
