package application.databaseManagementService;

import data.dataModel.Coordinate;
import data.dataModel.Intersection;
import data.dataModel.Street;
import data.databaseDriver.DAOAdmin;
import data.databaseDriver.DAOAdminNeo4jImpl;
import org.jboss.logging.Logger;
import util.ServerUtilities;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Session Bean implementation class DatabaseManagementService.
 * <p>
 * Encapsulates the driver to access the traffic management database with
 * administration role.
 */

@Stateless
public class DatabaseManagementService implements DatabaseManagementServiceRemote, DatabaseManagementServiceLocal {

    private final Logger logger;
    DAOAdmin database;
    protected String databeseWriteURI = null;
    protected String databeseReadURI = null;
    protected String databaseUser = null;
    protected String databasePass = null;

    /**
     * Default constructor.
     * <p>
     * Instantiate a driver to access the Neo4j Graph Database for common user with default bolt uri and credentials.
     *
     * @throws FileNotFoundException
     */
    public DatabaseManagementService() {
        logger = Logger.getLogger(DatabaseManagementService.class);
        logger.info("DatabaseManagementService.DatabaseManagementService");
        try {
            ServerUtilities serverUtilities = new ServerUtilities();
            this.databeseWriteURI = serverUtilities.getDatabaseCoreUri();
            this.databeseReadURI = serverUtilities.getDatabaseReplicaUri();
            this.databaseUser = serverUtilities.getDatabaseCoreUser();
            this.databasePass = serverUtilities.getDatabaseCorePass();
            database = new DAOAdminNeo4jImpl(databeseWriteURI, databeseReadURI, databaseUser, databasePass);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * Called after the EJB construction. Open the connection to the database.
     */
    @PostConstruct
    public void connect() {
        logger.info("DatabaseManagementService.connect");
        database.openConnection();
    }

    /**
     * Called before the EJB destruction. Close the connection to the database.
     */
    @PreDestroy
    public void preDestroy() {
        logger.info("DatabaseManagementService.preDestroy");
        database.closeConnection();
    }

    @Override
    public Intersection addIntersection(Coordinate c, String highway, long osmid, String ref, boolean parking, boolean hospital, boolean busStop, boolean museum) {
        logger.info("DatabaseManagementService.addIntersection: c = " + c + ", highway = " + highway + 
				", osmid = " + osmid + ", ref = " + ref + ", parking = " + parking + ", hospital = " + hospital + 
				", busStop = " + busStop + ", museum = " + museum);
        Intersection i = database.addIntersection(c, highway, osmid, ref, parking, hospital, busStop, museum);
        return i;
    }

    @Override
    public Street addStreet(ArrayList<Coordinate> coordinates, int id, String access, String area, String bridge, long osmidStart,
                            long osmidDest, String highway, String junction, int key, ArrayList<Integer> arrayLanes, double length,
                            String maxSpeed, String name, boolean oneWay, ArrayList<Long> osmidEdges, String ref, boolean transportService,
                            String tunnel, String width, int origId, double weight, double flow, double averageTravelTime, boolean interrupted) {
        logger.info("DatabaseManagementService.addStreet: coordinates = " + coordinates + ", id = " + id + ", access = " + access + 
				", area = " + area + ", bridge = " + bridge + ", osmidStart = " + osmidStart + ", osmidDest = " + osmidDest + 
				", highway = " + highway + ", junction = " + junction + ", key = " + key + ", arrayLanes = " + arrayLanes + 
				", length = " + length + ", maxSpeed = " + maxSpeed + ", name = " + name + ", oneWay = " + oneWay + 
				", osmidEdges = " + osmidEdges + ", ref = " + ref + ", transportService = " + transportService + ", tunnel = " + tunnel + 
				", width = " + width + ", origId = " + origId + ", weight = " + weight + ", flow = " + flow + 
				", averageTravelTime = " + averageTravelTime + ", interrupted = " + interrupted);
        Street s = database.addStreet(coordinates, id, access, area, bridge, osmidStart, osmidDest, highway, junction, key,
                arrayLanes, length, maxSpeed, name, oneWay, osmidEdges, ref, transportService, tunnel, width, origId, weight, flow, averageTravelTime, interrupted);
//        database.updateBetweenness();
        return s;
    }


    @Override
    public Street setStreetWeight(int id, double weight) {
		logger.info("DatabaseManagementService.setStreetWeight: id = " + id + ", weight = " + weight);
    	return database.setStreetWeight(id, weight);
    }

    @Override
    public Intersection setBetweennessIntersection(long osmid, double betweennees) {
		logger.info("DatabaseManagementService.setBetweennessIntersection: osmid = " + osmid + ", betweennees = " + betweennees);
        return database.setBetweennessIntersection(osmid, betweennees);
    }

    @Override
    public Intersection getIntersection(long osmid) {
		logger.info("DatabaseManagementService.getIntersection: osmid = " + osmid);
    	return database.getIntersection(osmid);
    }

    @Override
    public Street getStreet(int id) {
		logger.info("DatabaseManagementService.getStreet: id = " + id);
        return database.getStreet(id);
    }


    @Override
    public HashMap<Integer, Street> getStreets(long osmid) {
		logger.info("DatabaseManagementService.getStreets: osmid = " + osmid);
        return database.getStreets(osmid);
    }

    @Override
    public Street getStreet(long osmidStart, long osmidDest) {
		logger.info("DatabaseManagementService.getStreet: osmidStart = " + osmidStart + ", osmidDest = " + osmidDest);
        return database.getStreet(osmidStart, osmidDest);
    }

    @Override
    public void deleteIntersection(long osmid) {
		logger.info("DatabaseManagementService.deleteIntersection: osmid = " + osmid);
        database.deleteIntersection(osmid);
    }

    @Override
    public void deleteStreet(int id) {
		logger.info("DatabaseManagementService.deleteStreet: id = " + id);
        database.deleteStreet(id);
    }


    @Override
    public int getLinkKey(long osmidStart, long osmidDest) {
		logger.info("DatabaseManagementService.getLinkKey: osmidStart = " + osmidStart + ", osmidDest = " + osmidDest);
        return database.getLinkKey(osmidStart, osmidDest);
    }

    @Override
    public void updateBetweennesExact() {
		logger.info("DatabaseManagementService.updateBetweennesExact");
        database.updateBetweennesExact();
    }

    @Override
    public void updateBetweeennessBrandesRandom() {
		logger.info("DatabaseManagementService.updateBetweeennessBrandesRandom");
    	database.updateBetweeennessBrandesRandom();
    }

    @Override
    public void updateBetweeennessBrandesDegree() {
		logger.info("DatabaseManagementService.updateBetweeennessBrandesDegree");
        database.updateBetweeennessBrandesDegree();
    }

    @Override
    public void updateBetweenness() {
		logger.info("DatabaseManagementService.updateBetweenness");
        database.updateBetweenness();
    }

    @Override
    public LocalDateTime getLastModified() {
		logger.info("DatabaseManagementService.getLastModified");
    	return database.getLastModified();
    }

    @Override
    public void setStreetInterrupted(int id, boolean interrupted) throws Exception {
		logger.info("DatabaseManagementService.setStreetInterrupted: id = " + id + ", interrupted = " + interrupted);
        database.setStreetInterrupted(id, interrupted);
    }

    public String test() {
		logger.info("DatabaseManagementService.test");
        return "Test-String-Admin";
    }

}
