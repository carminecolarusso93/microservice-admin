package data.databaseDriver;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

import data.dataModel.*;

/**
 * Interface that gives all methods that define the operations to do on neo4j
 * database to run the application.
 *
 * @author Giovanni Codianni
 * @author Carmine Colarusso
 * @author Chiara Verdone
 *
 */
public interface DAOAdmin {

    /**
     * This method instantiate a new session and runs a read query.
     *
     * @param query contains a query written in CypherQueryLanguage.
     * @return StatementResult of query execution.
     *         <p>
     *         If driver is not instantiated yet throws
     *         {@link DatabaseNotConnectException}
     *
     */
    Result databaseRead(String query);

    /**
     * This method instantiate a new session, begins a new transaction that runs a
     * query. If transaction is successful, it commits results.
     *
     * @param query contains a query written in CypherQueryLanguage.
     * @return StatementResult of query execution.
     *         <p>
     *         If driver is not instantiated yet throws
     *         {@link DatabaseNotConnectException}
     */
    List<Record> databaseWrite(String query);

    /**
     * Add an Intersection to the database with given parameters.
     *
     * @param coordinate Longitude and Latitude of Intersection in Decimal Degrees(DD).
     * @param highway    Shows the type of the intersection within the road network.
     * @param osmid      Id of the intersection in the road network.
     * @param ref
     * @param parking    Tag the location as parking.
     * @param hospital 	 Tag the location as hospital.
     * @param busStop    Tag the location as busStop.
     * @param museum     Tag the location as museum.
     * @return The Java representation of the Intersection.
     */
    Intersection addIntersection(Coordinate coordinate, String highway, long osmid, String ref, boolean parking, boolean hospital, boolean busStop, boolean museum);

    /**
     * Add a directional STREET relation to the database with given parameters.
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
     * @param length            Length in meter of the street.
     * @param maxSpeed          Speed limit of the street in km/h.
     * @param name              Name of the Street.
     * @param oneWay            Describe if street is a oneway street or not.
     * @param osmidEdges        List of OSM nodes crossed and incorporated in the street.
     * @param ref               Describes if the street has an exit with a specific number assigned to it.
     * @param transportService  Describe if some services such as bus rides are
     *                          active.
     * @param tunnel            Describes if it is present a tunnel.
     * @param width             Width of street in meters.
     * @param origId            Id used to elaborations.
     * @param weight            Weight value of links for graph elaborations.
     * @param flow              Flow value used for graph elaborations.
     * @param averageTravelTime Average travel time of street.
     * @param interrupted 		Tag the street as interrupted.
     * @return The Java representation of Street.
     */
    Street addStreet(ArrayList<Coordinate> coordinates, int id, String access, String area, String bridge,
                     long osmidStart, long osmidDest, String highway, String junction, int key, ArrayList<Integer> arrayLanes,
                     double length, String maxSpeed, String name, boolean oneWay, ArrayList<Long> osmidEdges, String ref,
                     boolean transportService, String tunnel, String width, long origId, double weight, double flow,
                     double averageTravelTime, boolean interrupted);

    /**
     * Deletes a STREET identified by given linkKey.
     *
     * @param id Id of the street to delete.
     */
    void deleteStreet(int id);

    /**
     * Update the weight value of a specific street.
     *
     * @param id     Id of the street to update.
     * @param weight Weight value to be set to the link.
     * @return The Java representation of the Street with update value.
     */
    Street setStreetWeight(int id, double weight);

    /**
     * Update the Betweenness Centrality value of a specific Intersection.
     *
     * @param osmid       Id of the Intersection to update.
     * @param betweennees Betweenness value to be set to the Intersection.
     * @return The Java representation of the Intersection with update value.
     */
    Intersection setBetweennessIntersection(long osmid, double betweennees);

    /**
     * Returns the Intersection with given Id.
     * <p>
     * Heavy interrogation of database, returns an Intersection with related
     * streets.
     *
     * @param osmid Id of the Intersection to find.
     * @return The Java representation of the searched Intersection.
     */
    Intersection getIntersection(long osmid);

    /**
     * Returns the Intersection with given Id.
     * <p>
     * Lightweight interrogation of database, unlike getIntersection() method it
     * returns an Intersection without related streets.
     *
     * @param osmid Id of the Intersection to find.
     * @return The Java representation of the searched Intersection.
     */
    Intersection getIntersectionLight(long osmid);

    /**
     * Returns the Street with given Id.
     *
     * @param id Id of the Street to find.
     * @return The Java representation of the searched Street.
     */
    Street getStreet(int id);

    /**
     * Returns the Street with between two different intersection identified by
     * given osmids.
     *
     * @param osmidStart Id of the starting intersection in the road network.
     * @param osmidDest  Id of the destination intersection in the road network.
     * @return The Java representation of the searched Street.
     */
    Street getStreet(long osmidStart, long osmidDest);

    /**
     * Returns all streets starting from the Intersection with the given Id.
     *
     * @param osmid Id of the Intersection to find.
     * @return An HashMap of the street starting from the Intersection, the Key is
     *         the id of the Street and the Value is the Street with the
     *         corresponding Id.
     */
    HashMap<Integer, Street> getStreets(long osmid);

    /**
     * Returns the geometry of a Street in Coordinate
     *
     * @param osmidStart Id of the starting Intersection in the road network.
     * @param osmidDest  Id of the destination Intersection in the road network.
     * @return an ordered ArrayList of Coordinate that identifies Intersections that
     *         belong to the street.
     */
    ArrayList<Coordinate> getStreetGeometry(long osmidStart, long osmidDest);

    /**
     * Deletes an Intersection identified by given vertexKey.
     *
     * @param osmid Id of the Intersection to delete.
     */
    void deleteIntersection(long osmid);

    /**
     * This method close the open connection and delete driver.
     */
    void closeConnection();

    /**
     * This method instantiate a new neo4j driver and starts a connection with
     * credentials user and password given by constructor.
     */
    void openConnection();

    /**
     * Returns linkKey of the street between two different intersection identified
     * by given vertexKeys.
     *
     * @param osmidStart Id of the starting intersection in the road network.
     * @param osmidDest  Id of the destination intersection in the road network.
     * @return id of street.
     */
    int getLinkKey(long osmidStart, long osmidDest);

    /**
     * Update the value of BetweennessCentrality of all Intersection in database
     * with exact algorithm.
     * <p>
     * For large graphs, exact centrality computation isn’t practical. The algorithm
     * requires at least O(nm) time for unweighted graphs, where n is the number of
     * nodes and m is the number of relationships.
     *
     */
    void updateBetweennesExact();


    /**
     * Update the BetweennessCentrality's value of all Intersection in database with
     * Brandes random algorithm.
     * <p>
     * Rather than calculating the shortest path between every pair of nodes, the
     * RA-Brandes algorithm considers only a subset of nodes. Nodes are selected
     * uniformly, at random, with defined probability of selection. The probability
     * is log10(N) / e^2, were N is the number of nodes in graph.
     */
    void updateBetweeennessBrandesRandom();

    /**
     * Update the BetweennessCentrality's value of all Intersection in database with
     * Brandes random algorithm.
     * <p>
     * First, the mean degree of the nodes is calculated, and then only the nodes
     * whose degree is higher than the mean are visited (i.e. only dense nodes are
     * visited).
     */
    void updateBetweeennessBrandesDegree();

    /**
     * Update the BetweennessCentrality's value of all Intersection in database with
     * default algorithm.
     */
    void updateBetweenness();

    /**
     * Querys the database in order to know the timestamp of last update.
     *
     * @return Timestamp of last update.
     */
    LocalDateTime getLastModified();

    void setLastModified();

    /**
     * Update the interrupted's value of the street in database with the given id.
     * @param id Id of the street to update.
     * @param interrupted New value of interrupted to set.
     */
    void setStreetInterrupted(int id, boolean interrupted) throws Exception;

}