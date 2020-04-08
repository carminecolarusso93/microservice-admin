package data.databaseDriver;

import java.time.LocalDateTime;
import java.util.*;

import org.jboss.logging.Logger;

import org.neo4j.driver.*;
import org.neo4j.driver.exceptions.NoSuchRecordException;

import data.dataModel.*;

public class DAOAdminNeo4jImpl implements DAOAdmin {

    private String uriWrite,uriRead, user, password;
    private Driver driverRead;
    private Driver driverWrite;
    private Logger logger;
    private SessionConfig readSessionConfig;
    private SessionConfig writeSessionConfig;


    /**
     * @param uriWrite      is the bolt address to write access neo4j database.
     * @param uriRead      is the bolt address to read access neo4j database.
     * @param user     is the username to access neo4j database.
     * @param password is the password to access neo4j database.
     */
    public DAOAdminNeo4jImpl(String uriWrite, String uriRead, String user, String password) {
        logger = Logger.getLogger(DAOAdminNeo4jImpl.class);
        logger.info("DAOAdminNeo4jImpl.DAOAdminNeo4jImpl: uri = " + uriRead + ", user = " + user + ", password = " + password);
        this.uriWrite = uriWrite;
        this.uriRead = uriRead;
        this.user = user;
        this.password = password;
        this.driverRead = null;
        this.driverWrite = null;
        this.readSessionConfig = SessionConfig.builder()
                .withDefaultAccessMode(AccessMode.READ)
                .build();
        this.writeSessionConfig = SessionConfig.builder()
                .withDefaultAccessMode(AccessMode.WRITE)
                .build();

    }

    // CONNESSIONE

    @Override
    public void openConnection() {
        logger.info("DAOAdminNeo4jImpl.openConnection");
        logger.info("Opening Connection to DataBase URI-read[" + uriRead + "]");
        logger.info("Opening Connection to DataBase URI-write[" + uriWrite + "]");
        this.driverRead = GraphDatabase.driver(uriRead, AuthTokens.basic(user, password));
        this.driverWrite = GraphDatabase.driver(uriWrite, AuthTokens.basic(user, password));
    }

    @Override
    public void closeConnection() {
        logger.info("DAOAdminNeo4jImpl.closeConnection");
        logger.info("Closing Connection to DataBase URI-read[" + uriRead + "]");
        logger.info("Closing Connection to DataBase URI-write[" + uriWrite + "]");
        // Logica bloccante
        // driver.close();

        // Logica non bloccante
        driverRead.closeAsync();
        driverWrite.closeAsync();
        driverRead = null;
        driverWrite = null;
    }

    // INTERROGAZIONE

    @Override
    public Result databaseRead(String query) {
        logger.info("DAOAdminNeo4jImpl.databaseRead:query = " + query);
        try {
            if (driverRead == null)
                throw new DatabaseNotConnectException("Database Non Connesso");
            Session session = driverRead.session(readSessionConfig);
            return session.run(query);
        } catch (DatabaseNotConnectException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Record> databaseWrite(String query) {
        logger.info("DAOAdminNeo4jImpl.databaseRead:query = " + query);

        try {
            if (driverWrite == null)
                throw new DatabaseNotConnectException("Database Non Connesso");

            Session session = driverWrite.session(writeSessionConfig);
            Transaction tx = session.beginTransaction();

            List<Record> result = tx.run(query).list();
            tx.commit();

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Executed Query <br>
     * "MERGE (a:Intersection {longitude: c.getLongitude(), latitude:+ c.getLatitude(), highway: highway, osmid: osmid, ref: ref, betweenness: 0, parking: parking, hospital: hospital, busStop: busStop, museum: museum}) RETURN a.osmid"
     */
    @Override
    public Intersection addIntersection(Coordinate c, String highway, long osmid, String ref, boolean parking,
                                        boolean hospital, boolean busStop, boolean museum) {
        logger.info("DAOAdminNeo4jImpl.addIntersection: c = " + c + ", highway = " + highway + ", osmid = " + osmid + ", ref = " + ref + ", parking = " + parking + ", hospital = " + hospital + ", busStop = " + busStop + ", museum = " + museum);

        Intersection result = null;
        String query = "MERGE (a:Intersection {longitude: " + c.getLongitude() + ", latitude: " + c.getLatitude()
                + ", highway: \"" + highway + "\", osmid: " + osmid + ", ref: \"" + ref
                + "\",  betweenness: 0, parking: " + parking + ", hospital: " + hospital + ", busStop: " + busStop
                + ", museum: " + museum + "})" + " RETURN a.osmid";
        Record resultRecord = databaseWrite(query).get(0);
        if (resultRecord.get("a.osmid").asLong() == osmid) {
            result = new Intersection(c, highway, osmid, ref, parking, hospital, busStop, museum);
        }

        return result;
    }

    /**
     * Executed Query <br>
     * MATCH (a:Intersection {osmid: osmidStart}), (b:Intersection {osmid: osmidDest}) <br>
     * MERGE (a)-[r:STREET {id: id + ", coordinates :[coordinates], access: access, area: area, bridge :bridge,
     * osmidStart: osmidStart, osmidDest: osmidDest, highway: highway, junction: junction, key: key,
     * arrayLanes: [arrayLanes], length: length, maxSpeed: maxSpeed, name: name, oneWay: oneWay,
     * osmidEdges : [osmidEdges], ref: ref, transportService: transportService, tunnel: tunnel, width: width,
     * origId: origId, weight: weight, flow: flow, averageTravelTime :averageTravelTime, interrupted: interrupted}]->(b)<br>
     * RETURN r.id
     */
    @Override
    public Street addStreet(ArrayList<Coordinate> coordinates, int id, String access, String area, String bridge,
                            long osmidStart, long osmidDest, String highway, String junction, int key, ArrayList<Integer> arrayLanes,
                            double length, String maxSpeed, String name, boolean oneWay, ArrayList<Long> osmidEdges, String ref,
                            boolean transportService, String tunnel, String width, long origId, double weight, double flow,
                            double averageTravelTime, boolean interrupted) {
        logger.info("DAOAdminNeo4jImpl.addStreet: coordinates = " + coordinates + ", id = " + id + ", access = " + access + ", area = " + area + ", bridge = " + bridge + ", osmidStart = " + osmidStart + ", osmidDest = " + osmidDest + ", highway = " + highway + ", junction = " + junction + ", key = " + key + ", arrayLanes = " + arrayLanes + ", length = " + length + ", maxSpeed = " + maxSpeed + ", name = " + name + ", oneWay = " + oneWay + ", osmidEdges = " + osmidEdges + ", ref = " + ref + ", transportService = " + transportService + ", tunnel = " + tunnel + ", width = " + width + ", origId = " + origId + ", weight = " + weight + ", flow = " + flow + ", averageTravelTime = " + averageTravelTime + ", interrupted = " + interrupted);

        StringBuilder sbLanes = new StringBuilder();
        StringBuilder sbEdges = new StringBuilder();
        StringBuilder sbCoordinates = new StringBuilder();

        int i = 0;

        if (arrayLanes == null) {
            sbLanes.append("");
        } else if (arrayLanes.isEmpty()) {
            sbLanes.append("");
        } else {
            for (; i < arrayLanes.size() - 1; i++) {
                sbLanes.append(arrayLanes.get(i) + ", ");
            }
            sbLanes.append(arrayLanes.get(i));
        }

        if (osmidEdges == null) {
            sbEdges.append("");
        } else if (osmidEdges.isEmpty()) {
            sbEdges.append("");
        } else {
            for (i = 0; i < osmidEdges.size() - 1; i++) {
                sbEdges.append(osmidEdges.get(i) + ", ");
            }
            sbEdges.append(osmidEdges.get(i));
        }

        if (coordinates == null) {
            sbCoordinates.append("");
        } else if (coordinates.isEmpty()) {
            sbCoordinates.append("");
        } else {
            for (i = 0; i < coordinates.size() - 1; i++) {
                sbCoordinates.append(coordinates.get(i) + ", ");
            }
            sbCoordinates.append(coordinates.get(i));
        }
        String query = "MATCH (a:Intersection {osmid: " + osmidStart + "}), (b:Intersection {osmid: " + osmidDest
                + "}) " + "MERGE (a)-[r:STREET {id: " + id + ", coordinates : \"" + sbCoordinates + "\""
                + ", access : \"" + access + "\"" + ", area : \"" + area + "\"" + ", bridge : \"" + bridge + "\""
                + ", osmidStart : " + osmidStart + ", osmidDest : " + osmidDest + ", highway : \"" + highway + "\""
                + ", junction : \"" + junction + "\"" + ", key : " + key + ", arrayLanes : [" + sbLanes.toString() + "]"
                + ", length : " + length + ", maxSpeed : \"" + maxSpeed + "\"" + ", name : \"" + name + "\""
                + ", oneWay : " + oneWay + ", osmidEdges : [" + sbEdges.toString() + "]" + ", ref : \"" + ref + "\""
                + ", transportService : " + transportService + ", tunnel : \"" + tunnel + "\"" + ", width : \"" + width
                + "\"" + ", origId : " + origId + ", weight : " + weight + ", flow : " + flow + ", averageTravelTime : "
                + averageTravelTime + ", interrupted : " + interrupted + "}]->(b) " + "RETURN r.id";

        Record resultRecord = databaseWrite(query).get(0);
        if (resultRecord.get("r.id").asLong() == id) {
            return new Street(coordinates, id, access, area, bridge, osmidStart, osmidDest, highway, junction, key,
                    arrayLanes, length, maxSpeed, name, oneWay, osmidEdges, ref, transportService, tunnel, width, origId,
                    weight, flow, averageTravelTime, interrupted);
        }
        return null;
    }

    /**
     * Executed Query <br>
     * MATCH (a:Intersection)-[r:STREET]->(b:Intersection) WHERE r.id = id SET r.weight=weight RETURN r.id
     */
    @Override
    public Street setStreetWeight(int id, double weight) {
        logger.info("DAOAdminNeo4jImpl.setStreetWeight: id = " + id + ", weight = " + weight);
        String query = "MATCH (a:Intersection)-[r:STREET]->(b:Intersection) WHERE r.id = " + id + " SET r.weight= "
                + weight + " RETURN r.id";
        Record resultRecord = databaseWrite(query).get(0);

        //Record r = result.single();

        if (resultRecord.get("r.id").asInt() == id) {
            return getStreet(id);
        } else {
            logger.error("Error in setStreetWeight()");
            return null;
        }
    }

    /**
     * Executed Query <br>
     * MATCH (a:Intersection {osmid: osmid}) SET a.betweenness = betweenness RETURN properties(a)
     */
    @Override
    public Intersection setBetweennessIntersection(long osmid, double betweenness) {
        logger.info("DAOAdminNeo4jImpl.setBetweennessIntersection: osmid = " + osmid + ", betweenness = " + betweenness);

        String query = "MATCH (a:Intersection {osmid:" + osmid + "}) SET a.betweenness =" + betweenness
                + " RETURN properties(a)";

        Record resultRecord = databaseWrite(query).get(0);

        Value r = resultRecord.get("properties(a)");
        // String nome = r.get("name").asString();

        Coordinate c = new Coordinate(r.get("longitude").asDouble(), r.get("latitude").asDouble());
        String highway = r.get("highway").asString();
        long id = r.get("osmid").asLong();
        String ref = r.get("ref").asString();
        boolean parking = r.get("parking").asBoolean();
        boolean hospital = r.get("hospital").asBoolean();
        boolean busStop = r.get("busStop").asBoolean();
        boolean museum = r.get("museum").asBoolean();


        return new Intersection(c, highway, id, ref, betweenness, parking, hospital, busStop, museum);
    }

    /**
     * Executed Query <br>
     * MATCH (a:Intersection {osmid: osmid}) RETURN properties(a)
     */
    @Override
    public Intersection getIntersection(long osmid) {
        logger.info("DAOAdminNeo4jImpl.getIntersection: osmid = " + osmid);

        String query = "MATCH (a:Intersection {osmid:" + osmid + "}) RETURN properties(a)";


        Record resultRecord = databaseRead(query).single();
        Value v = resultRecord.get("properties(a)");

        return convertIntersection(v);

    }

    /**
     * Executed Query <br>
     * MATCH (a:Intersection {osmid: osmid}) RETURN properties(a)
     */
    @Override
    public Intersection getIntersectionLight(long osmid) {
        logger.info("DAOAdminNeo4jImpl.getIntersectionLight: osmid = " + osmid);

        String query = "MATCH (a:Intersection {osmid:" + osmid + "}) RETURN properties(a)";


        Record resultRecord = databaseRead(query).single();
        try {
            Value v = resultRecord.get("properties(a)");

            return convertIntersection(v);
        } catch (NoSuchRecordException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Executed Query <br>
     * MATCH (a:Intersection{osmid: osmidS})-[r:STREET]->(b:Intersection{osmid: osmidD}) RETURN properties(r)
     */
    @Override
    public Street getStreet(long osmidS, long osmidD) {
        logger.info("DAOAdminNeo4jImpl.getStreet: osmidS = " + osmidS + ", osmidD = " + osmidD);
        // TEST NODES 13445152 3991897787
        String query = "MATCH (a:Intersection{osmid:" + osmidS + "})-[r:STREET]->(b:Intersection{osmid:" + osmidD
                + "}) RETURN properties(r)";

        Result result = databaseRead(query);
        Record r = result.next();

        Value v = r.get("properties(r)");

        String sCord = v.get("coordinates").asString();

        ArrayList<Coordinate> coordinates = getCoordinateList(sCord);

        int ids = v.get("id").asInt();
        String access = v.get("access").asString();
        String area = v.get("area").asString();
        String bridge = v.get("bridge").asString();
        long osmidStart = v.get("osmidStart").asLong();
        long osmidDest = v.get("osmidDest").asLong();
        String highway = v.get("highway").asString();
        String junction = v.get("junction").asString();
        int key = v.get("key").asInt();
        List<Object> listLanes = v.get("arrayLanes").asList();
        ArrayList<Integer> arrayLanes = new ArrayList<>();
        for (Object o : listLanes) {
            arrayLanes.add(((Long) o).intValue());
        }

        double length = v.get("length").asDouble();
        String maxSpeed = v.get("maxSpeed").asString();
        String name = v.get("name").asString();
        boolean oneWay = v.get("oneWay").asBoolean();
        List<Object> listEdges = v.get("osmidEdges").asList();
        ArrayList<Long> osmidEdges = new ArrayList<>();
        for (Object o : listEdges) {
            osmidEdges.add((Long) o);
        }
        String ref = v.get("ref").asString();
        boolean transportService = v.get("transportService").asBoolean();
        String tunnel = v.get("tunnel").asString();
        String width = v.get("width").asString();
        long origId = v.get("origId").asLong();
        double weight = v.get("weight").asDouble();
        double flow = v.get("flow").asDouble();
        double averageTravelTime = v.get("averageTravelTime").asDouble();
        boolean interrupted = v.get("interrupted").asBoolean();

        return new Street(coordinates, ids, access, area, bridge, osmidStart, osmidDest, highway, junction, key,
                arrayLanes, length, maxSpeed, name, oneWay, osmidEdges, ref, transportService, tunnel, width, origId,
                weight, flow, averageTravelTime, interrupted);

    }


    /**
     * Executed Query <br>
     * MATCH (a:Intersection{osmid: osmidS})-[r:STREET]->(b:Intersection{osmid: osmidD}) RETURN r.coordinates
     */
    @Override
    public ArrayList<Coordinate> getStreetGeometry(long osmidS, long osmidD) {
        logger.info("DAOAdminNeo4jImpl.getStreetGeometry: osmidS = " + osmidS + ", osmidD = " + osmidD);
        String query = "MATCH (a:Intersection{osmid:" + osmidS + "})-[r:STREET]->(b:Intersection{osmid:" + osmidD
                + "}) RETURN r.coordinates";

        String sCord = databaseRead(query).list().get(0).get("r.coordinates").asString();
        return getCoordinateList(sCord);
    }

    /**
     * Executed Query <br>
     * MATCH (a:Intersection{osmid: osmidS})-[r:STREET]->(b:Intersection{osmid: osmidD}) RETURN properties(r)
     */
    @Override
    public Street getStreet(int id) {
        logger.info("DAOAdminNeo4jImpl.getStreet: id = " + id);

        String query = "MATCH (a:Intersection)-[r:STREET]->(b:Intersection) WHERE r.id=" + id + " RETURN properties(r)";
        Result result = databaseRead(query);
        Record r = result.single();
        Value v = r.get("properties(r)");

        String sCord = v.get("coordinates").asString();

        ArrayList<Coordinate> coordinates = getCoordinateList(sCord);

        int ids = v.get("id").asInt();
        String access = v.get("access").asString();
        String area = v.get("area").asString();
        String bridge = v.get("bridge").asString();
        long osmidStart = v.get("osmidStart").asLong();
        long osmidDest = v.get("osmidDest").asLong();
        String highway = v.get("highway").asString();
        String junction = v.get("junction").asString();
        int key = v.get("key").asInt();
//		List<Object> listLanes = v.get("arrayLanes").asList();
        ArrayList<Integer> arrayLanes = new ArrayList<>();
        // TODO
        // for (Object o : listLanes) {
        // arrayLanes.add(((Long) o).intValue());
        // }

        double length = v.get("length").asDouble();
        String maxSpeed = v.get("maxSpeed").asString();
        String name = v.get("name").asString();
        boolean oneWay = v.get("oneWay").asBoolean();
//		List<Object> listEdges = v.get("osmidEdges").asList();
        // TODO
        ArrayList<Long> osmidEdges = new ArrayList<>();
//		for (Object o : listEdges) {
//			osmidEdges.add((Long) o);
//		}
        String ref = v.get("ref").asString();
        boolean transportService = v.get("transportService").asBoolean();
        String tunnel = v.get("tunnel").asString();
        String width = v.get("width").asString();
        long origId = v.get("origId").asLong();
        double weight = v.get("weight").asDouble();
        double flow = v.get("flow").asDouble();
        double averageTravelTime = v.get("averageTravelTime").asDouble();
        boolean interrupted = v.get("interrupted").asBoolean();

        return new Street(coordinates, ids, access, area, bridge, osmidStart, osmidDest, highway, junction, key,
                arrayLanes, length, maxSpeed, name, oneWay, osmidEdges, ref, transportService, tunnel, width, origId,
                weight, flow, averageTravelTime, interrupted);

    }

    private ArrayList<Coordinate> getCoordinateList(String sCord) {
        logger.info("DAOAdminNeo4jImpl.getCoordinateList: sCord = " + sCord);

        sCord = sCord.replaceAll("Coordinate ", "");
        sCord = sCord.replaceAll("\\[longitude\\=", "");
        sCord = sCord.replaceAll("latitude\\=", "");
        sCord = sCord.replaceAll("\\],", ";");
        sCord = sCord.replace(" ", "");
        sCord = sCord.replaceAll("\\]", "");

        String[] split = sCord.split(";");
        String[] split2;
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        Coordinate c;
        for (String s : split) {
            split2 = s.split(",");
            c = new Coordinate(Double.parseDouble(split2[0]), Double.parseDouble(split2[1]));
            coordinates.add(c);
        }
        return coordinates;
    }

    /**
     * Executed Query <br>
     * MATCH (a:Intersection{osmid: osmidS})-[r:STREET]->(b:Intersection{osmid: osmidD}) RETURN collect(r.id) as ids
     */
    @Override
    public HashMap<Integer, Street> getStreets(long osmid) {
        logger.info("DAOAdminNeo4jImpl.getStreets: osmid = " + osmid);

        String query = "MATCH (a:Intersection {osmid:" + osmid + "})-[r:STREET]->(b:Intersection)"
                + " RETURN collect(r.id) as ids";
        Result result = databaseRead(query);

        HashMap<Integer, Street> strade = new HashMap<>();
        List<Object> ids = result.single().get("ids").asList();
        for (Object o : ids) {
            int id = ((Long) o).intValue();
            Street s = getStreet(id);
            strade.put(id, s);
        }
        return strade;
    }

    /**
     * Executed Query <br>
     * MATCH (a:Intersection {osmid: osmid}) DETACH DELETE a
     */
    @Override
    public void deleteIntersection(long osmid) {
        logger.info("DAOAdminNeo4jImpl.deleteIntersection: osmid = " + osmid);
        String query = "MATCH (a:Intersection {osmid:" + osmid + "}) DETACH DELETE a";
        databaseWrite(query);
    }

    /**
     * Executed Query <br>
     * MATCH ()-[r:STREET]->() WHERE r.id = id DELETE r
     */
    @Override
    public void deleteStreet(int id) {
        logger.info("DAOAdminNeo4jImpl.deleteStreet: id = " + id);
        String query = "MATCH ()-[r:STREET]->() WHERE r.id = " + id + " DELETE r";
        databaseWrite(query);
    }

    private ArrayList<Intersection> extractIntersectionArrayList(Result result) {
        logger.info("DAOAdminNeo4jImpl.extractIntersectionArrayList: result = " + result);

        ArrayList<Intersection> shortestPath = new ArrayList<>();
        for (Record r : result.list()) {
            Map<String, Object> mapValues = r.get("intersections").asMap();
            long osmid = (long) mapValues.get("osmid");
            double betweenness = (double) mapValues.get("betweenness");
            double latitude = (double) mapValues.get("latitude");
            double longitude = (double) mapValues.get("longitude");
            boolean parking = (boolean) mapValues.get("parking");
            boolean busStop = (boolean) mapValues.get("busStop");
            boolean hospital = (boolean) mapValues.get("hospital");
            boolean museum = (boolean) mapValues.get("museum");
            String highway = (String) mapValues.get("highway");
            String ref = (String) mapValues.get("ref");
            Intersection i = new Intersection(new Coordinate(longitude, latitude), highway, osmid, ref, betweenness, parking, hospital, busStop, museum);
            shortestPath.add(i);
        }
        return shortestPath;
    }

    @Override
    public int getLinkKey(long osmidStart, long osmidDest) {
        logger.info("DAOAdminNeo4jImpl.getLinkKey: osmidStart = " + osmidStart + ", osmidDest = " + osmidDest);
        String query = "MATCH (a:Intersection{osmid:" + osmidStart + "})-[r:STREET]->(b:Intersection{osmid:" + osmidDest
                + "}) RETURN r.id";
        Result result = databaseRead(query);

        return result.single().get("r.id").asInt();
    }

    @Override
    public void updateBetweennesExact() {
        logger.info("DAOAdminNeo4jImpl.updateBetweennesExact");
        String query = "CALL algo.betweenness('Intersection','STREET', {direction:'out',write:true, writeProperty:'betweenness', weightProperty:'weight'})";
        databaseWrite(query);
        setLastModified();
    }

    @Override
    public void updateBetweeennessBrandesRandom() {
        logger.info("DAOAdminNeo4jImpl.updateBetweeennessBrandesRandom");
        String query = "CALL algo.betweenness.sampled('Intersection','STREET', {strategy:'random', direction: \"out\", writeProperty:'betweenness',probability: 1, maxDepth: 4, weightProperty:'weight'})";
        databaseWrite(query);
        setLastModified();
    }

    @Override
    public void updateBetweeennessBrandesDegree() {
        logger.info("DAOAdminNeo4jImpl.updateBetweeennessBrandesDegree");
        String query = "CALL algo.betweenness.sampled('Intersection','STREET', {strategy:'degree', direction: \"out\", writeProperty:'betweenness',probability: 1, maxDepth: 4, weightProperty:'weight'})";
        databaseWrite(query);
        setLastModified();
    }

    @Override
    public void updateBetweenness() {
        logger.info("DAOAdminNeo4jImpl.updateBetweenness");
        this.updateBetweeennessBrandesDegree();
    }

    @Override
    public LocalDateTime getLastModified() {
        logger.info("DAOAdminNeo4jImpl.getLastModified");
        Result result = databaseRead("MATCH (a:Control) Return a.timestamp");
        Record r = result.single();
        LocalDateTime ldt = r.get("a.timestamp").asLocalDateTime();
        return ldt;
    }

    @Override
    public void setLastModified() {
        logger.info("DAOAdminNeo4jImpl.setLastModified");
        //todo deploy kafka
//        String query = "MATCH (a:Control)  SET a.timestamp = localdatetime() WITH a CALL streams.publish('test', a.timestamp) Return *";
        String query = "MATCH (a:Control)  SET a.timestamp = localdatetime()";
        databaseWrite(query);
    }


    @Override
    public void setStreetInterrupted(int id, boolean interrupted) throws Exception {
        logger.info("DAOAdminNeo4jImpl.setStreetInterrupted: id = " + id + ", interrupted = " + interrupted);
        String query = "MATCH ()-[s:STREET{id:" + id + "}]->() SET s.interrupted= " + interrupted + " return s.id";

        Record resultRecord = databaseWrite(query).get(0);

        if (resultRecord.get("s.id").asInt() != id) {
            throw new Exception("setStreetInterrupted(" + id + ") Error");
        }

    }

    private ArrayList<Intersection> convertToIntersectionArrayList(Result result, String nodeName) {
        logger.info("DAOAdminNeo4jImpl.convertToIntersectionArrayList: result = " + result + ", nodeName = " + nodeName);
        ArrayList<Intersection> intersections = new ArrayList<>();
        Record r;
        Value v;
        while (result.hasNext()) {
            r = result.next();
            v = r.get("properties(" + nodeName + ")");
            Coordinate c = new Coordinate(v.get("longitude").asDouble(), v.get("latitude").asDouble());

            String highway = v.get("highway").asString();
            long id = v.get("osmid").asLong();
            String ref = v.get("ref").asString();
            double betweenness = v.get("betweenness").asDouble();
            boolean parking = v.get("parking").asBoolean();
            boolean hospital = v.get("hospital").asBoolean();
            boolean busStop = v.get("busStop").asBoolean();
            boolean museum = v.get("museum").asBoolean();

            intersections.add(new Intersection(c, highway, id, ref, betweenness, parking, hospital, busStop, museum));
        }
        return intersections;
    }

    private Intersection convertIntersection(Value r) {
        logger.info("DAOAdminNeo4jImpl.convertIntersection: r = " + r);

        try {

            // String nome = r.get("name").asString();
            Coordinate c = new Coordinate(r.get("longitude").asDouble(), r.get("latitude").asDouble());

            String highway = r.get("highway").asString();
            long id = r.get("osmid").asLong();
            String ref = r.get("ref").asString();

            double betweenness = r.get("betweenness").asDouble();

            boolean parking = r.get("parking").asBoolean();
            boolean hospital = r.get("hospital").asBoolean();
            boolean busStop = r.get("busStop").asBoolean();
            boolean museum = r.get("museum").asBoolean();

            return new Intersection(c, highway, id, ref, betweenness, parking, hospital, busStop, museum);
        } catch (NoSuchRecordException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<Long> getIntersectionOsmids() {
        String query = "MATCH (a:Intersection) RETURN a.osmid as osmid";
        Result result = databaseRead(query);
        ArrayList<Long> osmids = new ArrayList<>();
        Record r;
        long osmid;

        while (result.hasNext()) {
            r = result.next();
            osmid = r.get("osmid").asLong();

            osmids.add(osmid);
        }
        return osmids;
    }
}