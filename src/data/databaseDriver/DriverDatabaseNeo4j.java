package data.databaseDriver;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.jboss.logging.Logger;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.exceptions.NoSuchRecordException;

import data.dataModel.*;

public class DriverDatabaseNeo4j implements DriverDatabase {

	private String uri, user, password;
	private Driver driver;
	private Logger logger;

	/**
	 * @param uri      is the bolt address to access neo4j database.
	 * @param user     is the username to access neo4j database.
	 * @param password is the password to access neo4j database.
	 */
	public DriverDatabaseNeo4j(String uri, String user, String password) {
		this.uri = uri;
		this.user = user;
		this.password = password;
		this.driver = null;
		logger = Logger.getLogger(DriverDatabaseNeo4j.class);
	}

	// CONNESSIONE

	@Override
	public void openConnection() {
		logger.info("Opening Connection to DataBase");
		this.driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
	}

	@Override
	public void closeConnection() {

		logger.info("Closing Connection to DataBase");
		// Logica bloccante
		// driver.close();

		// Logica non bloccante
		driver.closeAsync();
		driver = null;
	}

	// INTERROGAZIONE

	@Override
	public StatementResult interrogation(String query) {
		logger.info("interrogation: " + query);
		try {
			if (driver == null)
				throw new DatabaseNotConnectException("Database Non Connesso");
			Session session = driver.session();
			return session.run(query);
		} catch (DatabaseNotConnectException e) {
			e.printStackTrace();
			return null;
		}
	}

	public StatementResult transaction(String query) {
		logger.info("transaction: " + query);
		try {
			if (driver == null)
				throw new DatabaseNotConnectException("Database Non Connesso");
			Session session = driver.session();
			Transaction tx = session.beginTransaction();
			StatementResult result = tx.run(query);
			tx.commitAsync();
			return result;
		} catch (DatabaseNotConnectException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// ADD
	@Override
	public Intersection addIntersection(Coordinate c, String highway, long osmid, String ref, boolean parking,
			boolean hospital, boolean busStop, boolean museum) {
		String query = "MERGE (a:Intersection {longitude: " + c.getLongitude() + ", latitude: " + c.getLatitude()
				+ ", highway: \"" + highway + "\", osmid: " + osmid + ", ref: \"" + ref
				+ "\",  betweenness: 0, parking: " + parking + ", hospital: " + hospital + ", busStop: " + busStop
				+ ", museum: " + museum + "})" + " RETURN a.osmid";
		StatementResult result = transaction(query);
		if (result.single().get("a.osmid").asLong() == osmid) {
			return new Intersection(c, highway, osmid, ref, parking, hospital, busStop, museum);
		}
		return null;

	}

	// ADD

	public Intersection addIntersectionInit(Coordinate c, String highway, long osmid, String ref, double betweenness,
			boolean parking, boolean hospital, boolean busStop, boolean museum) {

		String query = "CREATE (a:Intersection {longitude: " + c.getLongitude() + ", latitude: " + c.getLatitude()
				+ ", highway: \"" + highway + "\", osmid: " + osmid + ", ref: \"" + ref + "\", betweenness: "
				+ betweenness + ", parking: " + parking + ", hospital: " + hospital + ", busStop: " + busStop
				+ ", museum: " + museum + "})" + " RETURN a.osmid";
		StatementResult result = transaction(query);
		if (result.single().get("a.osmid").asLong() == osmid) {
			return new Intersection(c, highway, osmid, ref, betweenness, parking, hospital, busStop, museum);
		}
		return null;

	}

	public Street addStreetInit(ArrayList<Coordinate> coordinates, int id, String access, String area, String bridge,
			long osmidStart, long osmidDest, String highway, String junction, int key, ArrayList<Integer> arrayLanes,
			double length, String maxSpeed, String name, boolean oneWay, ArrayList<Long> osmidEdges, String ref,
			boolean transportService, String tunnel, String width, long origId, double weight, double flow,
			double averageTravelTime, boolean interrupted) {

		// System.out.println(arrayLanes.size());
		StringBuilder sbLanes = new StringBuilder();
		StringBuilder sbEdges = new StringBuilder();
		StringBuilder sbCoordinates = new StringBuilder();

		int i = 0;
		if (arrayLanes.isEmpty()) {
			sbLanes.append("");
		} else {
			if (arrayLanes.isEmpty()) {
				sbLanes.append("");
			}
			for (; i < arrayLanes.size() - 1; i++) {
				sbLanes.append(arrayLanes.get(i) + ", ");
			}
			sbLanes.append(arrayLanes.get(i));
		}

		if (osmidEdges.isEmpty()) {
			sbEdges.append("");
		} else {
			for (i = 0; i < osmidEdges.size() - 1; i++) {
				sbEdges.append(osmidEdges.get(i) + ", ");
			}
			sbEdges.append(osmidEdges.get(i));
		}

		if (coordinates.isEmpty()) {
			sbCoordinates.append("");
		} else {
			for (i = 0; i < coordinates.size() - 1; i++) {
				sbCoordinates.append(coordinates.get(i) + ", ");
			}
			sbCoordinates.append(coordinates.get(i));
		}
		String query = "MATCH (a:Intersection {osmid: " + osmidStart + "}), (b:Intersection {osmid: " + osmidDest
				+ "}) " + "CREATE (a)-[r:STREET {id: " + id + ", coordinates : \"" + sbCoordinates + "\""
				+ ", access : \"" + access + "\"" + ", area : \"" + area + "\"" + ", bridge : \"" + bridge + "\""
				+ ", osmidStart : " + osmidStart + ", osmidDest : " + osmidDest + ", highway : \"" + highway + "\""
				+ ", junction : \"" + junction + "\"" + ", key : " + key + ", arrayLanes : [" + sbLanes.toString() + "]"
				+ ", length : " + length + ", maxSpeed : \"" + maxSpeed + "\"" + ", name : \"" + name + "\""
				+ ", oneWay : " + oneWay + ", osmidEdges : [" + sbEdges.toString() + "]" + ", ref : \"" + ref + "\""
				+ ", transportService : \"" + transportService + "\"" + ", tunnel : \"" + tunnel + "\"" + ", width : \""
				+ width + "\"" + ", origId : " + origId + ", weight : " + weight + ", flow : " + flow
				+ ", averageTravelTime : " + averageTravelTime + ", interrupted : " + interrupted + "}]->(b) "
				+ "RETURN r.id";

		// System.out.println("query: "+query);
		StatementResult result = transaction(query);

		return new Street(coordinates, id, access, area, bridge, osmidStart, osmidDest, highway, junction, key,
				arrayLanes, length, maxSpeed, name, oneWay, osmidEdges, ref, transportService, tunnel, width, origId,
				weight, flow, averageTravelTime, interrupted);

		// return null;
	}

	@Override
	public Street addStreet(ArrayList<Coordinate> coordinates, int id, String access, String area, String bridge,
			long osmidStart, long osmidDest, String highway, String junction, int key, ArrayList<Integer> arrayLanes,
			double length, String maxSpeed, String name, boolean oneWay, ArrayList<Long> osmidEdges, String ref,
			boolean transportService, String tunnel, String width, long origId, double weight, double flow,
			double averageTravelTime, boolean interrupted) {

		// System.out.println(arrayLanes.size());
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

		// System.out.println("query: "+query);
		StatementResult result = transaction(query);

		if (result.single().get("r.id").asInt() == id) {
			return new Street(coordinates, id, access, area, bridge, osmidStart, osmidDest, highway, junction, key,
					arrayLanes, length, maxSpeed, name, oneWay, osmidEdges, ref, transportService, tunnel, width,
					origId, weight, flow, averageTravelTime, interrupted);
		}

		return null;
	}

	@Override
	public Street setStreetWeight(int id, double weight) {
		String query = "MATCH (a:Intersection)-[r:STREET]->(b:Intersection) WHERE r.id = " + id + " SET r.weight= "
				+ weight + " RETURN r.id";
		StatementResult result = transaction(query);

		Record r = result.single();

		if (r.get("r.id").asInt() == id) {
			return getStreet(id);
		} else {
			System.err.println("errore");
			return null;
		}
	}

	@Override
	public Intersection setBetweennessIntersection(long osmid, double betweenness) {
		String query = "MATCH (a:Intersection {osmid:" + osmid + "}) SET a.betweenness=" + betweenness
				+ " RETURN properties(a)";
		// String query = "MATCH (a:Intersection {osmid:" + osmid + "}) RETURN
		// properties(a)";

		// System.out.println(query);
		StatementResult result = transaction(query);

		Value r = result.single().get("properties(a)");
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

	// GET
	@Override
	public Intersection getIntersection(long osmid) {
		String query = "MATCH (a:Intersection {osmid:" + osmid + "}) RETURN properties(a)";

		// System.out.println(query);
		StatementResult result = transaction(query);
		Value v = result.single().get("properties(a)");

		return convertIntersection(v);

	}

	@Override
	public Intersection getIntersectionLight(long osmid) {
		String query = "MATCH (a:Intersection {osmid:" + osmid + "}) RETURN properties(a)";

		// System.out.println(query);
		StatementResult result = transaction(query);
		try {
			Value v = result.single().get("properties(a)");

			return convertIntersection(v);
		} catch (NoSuchRecordException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Street getStreet(long osmidS, long osmidD) {

		// TEST NODES 13445152 3991897787
		String query = "MATCH (a:Intersection{osmid:" + osmidS + "})-[r:STREET]->(b:Intersection{osmid:" + osmidD
				+ "}) RETURN properties(r)";

		StatementResult result = interrogation(query);
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

	@Override
	public ArrayList<Coordinate> getStreetGeometry(long osmidS, long osmidD) {
		String query = "MATCH (a:Intersection{osmid:" + osmidS + "})-[r:STREET]->(b:Intersection{osmid:" + osmidD
				+ "}) RETURN properties(r)";

		StatementResult result = interrogation(query);
		Record r = result.next();

		Value v = r.get("properties(r)");

		String sCord = v.get("coordinates").asString();

		ArrayList<Coordinate> coordinates = getCoordinateList(sCord);
		return coordinates;
	}

	@Override
	public Street getStreet(int id) {
		String query = "MATCH (a:Intersection)-[r:STREET]->(b:Intersection) WHERE r.id=" + id + " RETURN properties(r)";
		// System.out.println(query);
		StatementResult result = interrogation(query);
		Record r = result.single();
		Value v = r.get("properties(r)");

		// String wkt = v.get("wkt").asString();
//		List<Object> listCoord = v.get("coordinates").asList();
//		ArrayList<Coordinate> coordinates = new ArrayList<>();
//		for (Object o : listCoord) {
//			coordinates.add((Coordinate) o);
//		}
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
		// TODO
		// for (Object o : listLanes) {
		// arrayLanes.add(((Long) o).intValue());
		// }

		double length = v.get("length").asDouble();
		String maxSpeed = v.get("maxSpeed").asString();
		String name = v.get("name").asString();
		boolean oneWay = v.get("oneWay").asBoolean();
		List<Object> listEdges = v.get("osmidEdges").asList();
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

	@Override
	public HashMap<Integer, Street> getStreets(long osmid) {
		String query = "MATCH (a:Intersection {osmid:" + osmid + "})-[r:STREET]->(b:Intersection)"
				+ " RETURN collect(r.id) as ids";
		StatementResult result = interrogation(query);

		HashMap<Integer, Street> strade = new HashMap<>();
		List<Object> ids = result.single().get("ids").asList();
		for (Object o : ids) {
			Integer id = ((Long) o).intValue();
			Street s = getStreet(id);
			strade.put(id, s);
		}
		return strade;
	}

	// DELETE
	@Override
	public void deleteIntersection(long osmid) {
		String query = "MATCH (a:Intersection {osmid:" + osmid + "}) DETACH DELETE a";
		transaction(query);
	}

	@Override
	public void deleteStreet(int id) {
		String query = "MATCH ()-[r:STREET]->() WHERE r.id = " + id + " DELETE r";
		transaction(query);
	}

	// SERVICE METHODS
	@Override
	public ArrayList<Long> shortestPath(long osmidStart, long osmidDest) {
		String query = "MATCH (start:Intersection{osmid:" + osmidStart + "}), (end:Intersection{osmid:" + osmidDest
				+ "})\r\n"
				+ "CALL algo.shortestPath.stream(start, end, 'weight',{direction:'OUTGOING', nodeQuery:'MATCH(i:Intersection) RETURN id(i) as id',\r\n"
				+ "relationshipQuery:'MATCH(a:Intersection)-[s:STREET{interrupted:false}]->(b:Intersection) RETURN id(a) as source, id(b) as target, s.weight as weight', graph:'cypher'})"
				+ "YIELD nodeId\r\n" + "RETURN algo.asNode(nodeId).osmid as vertexKeys";

		StatementResult result = interrogation(query);
		ArrayList<Long> shortestPath = new ArrayList<>();
		Record r;
		long vertexKey;
		while (result.hasNext()) {
			r = result.next();
			vertexKey = r.get("vertexKeys").asLong();

			shortestPath.add(vertexKey);
			// System.out.println("vert: "+vertexKey);
		}
		return shortestPath;
	}

	@Override
	public ArrayList<Coordinate> shortestPathCoordinate(long osmid1, long osmid2) {
		String query = "MATCH (start:Intersection{osmid:" + osmid1 + "}), (end:Intersection{osmid:" + osmid2 + "})\r\n"
				+ "CALL algo.shortestPath.stream(start, end, 'weight',{direction:'OUTGOING', nodeQuery:'MATCH(i:Intersection) RETURN id(i) as id',\r\n"
				+ "relationshipQuery:'MATCH(a:Intersection)-[s:STREET{interrupted:false}]->(b:Intersection) RETURN id(a) as source, id(b) as target, s.weight as weight', graph:'cypher'})"
				+ "YIELD nodeId\r\n" + "RETURN algo.asNode(nodeId).osmid as vertexKeys";

		StatementResult result = interrogation(query);
		ArrayList<Long> shortestPath = new ArrayList<>();
		Record r;
		long vertexKey;
		while (result.hasNext()) {
			r = result.next();
			vertexKey = r.get("vertexKeys").asLong();

			shortestPath.add(vertexKey);
		}
		ArrayList<Coordinate> coordstmp = new ArrayList<>();
		ArrayList<Coordinate> coords = new ArrayList<>();

		for (int i = 0; i < shortestPath.size() - 1; i++) {
			coordstmp = getStreetGeometry(shortestPath.get(i), shortestPath.get(i + 1));
			coords.addAll(coordstmp);
		}
		return coords;
	}

	@Override
	public ArrayList<Intersection> getTopCriticalNodes(int top) {
		String query = "MATCH (i:Intersection) RETURN properties(i) ORDER BY i.betweenness DESC LIMIT " + top;
		StatementResult result = interrogation(query);
		return convertToIntersectionArrayList(result, "i");
	}

	@Override
	public ArrayList<Intersection> getThresholdCriticalNodes(double threshold) {
		String query = "	MATCH (i:Intersection) WHERE i.betweenness > " + threshold + " RETURN properties(i)";
		StatementResult result = interrogation(query);
		return convertToIntersectionArrayList(result, "i");

	}

	@Override
	public double nodeFlow(long osmid) {
		// SOMMA NODI ARCHI USCENTI
		// String query="MATCH
		// (a:Intersection{vertexKey:"+vertexKey+"})-[r:STREET]->(b:Intersection) RETURN
		// SUM (r.weight) as flow";
		String query = "MATCH (a:Intersection {osmid:" + osmid + "}) RETURN a.betweenness as flow";
		StatementResult result = interrogation(query);

		return result.single().get("flow").asDouble();
	}

	@Override
	public int getLinkKey(long osmidStart, long osmidDest) {
		String query = "MATCH (a:Intersection{osmid:" + osmidStart + "})-[r:STREET]->(b:Intersection{osmid:" + osmidDest
				+ "}) RETURN r.id";
		StatementResult result = interrogation(query);

		return result.single().get("r.id").asInt();
	}

	@Override
	public void updateBetweennesExact() {
		String query = "CALL algo.betweenness('Intersection','STREET', {direction:'out',write:true, writeProperty:'betweenness', weightProperty:'weight'})";
		transaction(query);
		setLastModified();
	}

//	@Override
//	public double updateBetweennesExact(long osmid) {
//		String query = "CALL algo.betweenness('Intersection','STREET', {direction:'out',write:true, writeProperty:'betweenness', weightProperty:'weight'})";
//		StatementResult r = transaction(query);
//		query = "MATCH (i {osmid:" + osmid + "}) RETURN i.betweenness as bet";
//		StatementResult result = transaction(query);
//		return result.single().get("bet").asDouble();
//	}

	@Override
	public void updateBetweeennessBrandesRandom() {
		String query = "CALL algo.betweenness.sampled('Intersection','STREET', {strategy:'random', direction: \"out\", writeProperty:'betweenness',probability: 1, maxDepth: 4, weightProperty:'weight'})";
		transaction(query);
		setLastModified();
	}

//	@Override
//	public double updateBetweeennessBrandesRandom(long osmid) {
//		String query = "CALL algo.betweenness.sampled.stream('Intersection','STREET', {strategy:'random', direction: \"out\", writeProperty:'betweenness',probability: 1, maxDepth: 4, weightProperty:'weight'})";
//		StatementResult r = transaction(query);
//		query = "MATCH (i {osmid:" + osmid + "}) RETURN i.betweenness as bet";
//		StatementResult result = transaction(query);
//		return result.single().get("bet").asDouble();
//	}

	@Override
	public void updateBetweeennessBrandesDegree() {
		System.out.println("sono in bcUpdate");
		String query = "CALL algo.betweenness.sampled('Intersection','STREET', {strategy:'degree', direction: \"out\", writeProperty:'betweenness',probability: 1, maxDepth: 4, weightProperty:'weight'})";

		transaction(query);
		setLastModified();
		System.out.println("ho fatto");
	}

//	@Override
//	public double updateBetweeennessBrandesDegree(long osmid) {
//		String query = "CALL algo.betweenness.sampled.stream('Intersection','STREET', {strategy:'degree', direction: \"out\", writeProperty:'betweenness',probability: 1, maxDepth: 4, weightProperty:'weight'})";
//		StatementResult r = transaction(query);
//		query = "MATCH (i {osmid:" + osmid + "}) RETURN i.betweenness as bet";
//		StatementResult result = transaction(query);
//		return result.single().get("bet").asDouble();
//	}

	@Override
	public void updateBetweenness() {
		this.updateBetweeennessBrandesDegree();
	}

	@Override
	public LocalDateTime getLastModified() {
		StatementResult result = interrogation("MATCH (a:Control) Return a.timestamp");
		Record r = result.single();
		LocalDateTime ldt = r.get("a.timestamp").asLocalDateTime();
		return ldt;
	}

//	public LocalDateTime setLastModified() {
//		StatementResult result = interrogation("MATCH (a:Control) SET a.timestamp = localdatetime() Return a.timestamp");
//		Record r = result.single();	
//		LocalDateTime ldt = r.get("a.timestamp").asLocalDateTime();
//		return ldt;
//	}

	public void setLastModified() {
		transaction(
				"MATCH (a:Control)  SET a.timestamp = localdatetime() WITH a CALL streams.publish('test', a.timestamp) Return *");
	}

	public ArrayList<Integer> getLinkKeys() {
		String query = "MATCH ()-[r:STREET]->() RETURN r.id as linkKeys";
		StatementResult result = interrogation(query);
		ArrayList<Integer> linkKeys = new ArrayList<>();
		Record r;
		int linkKey;

		while (result.hasNext()) {
			r = result.next();
			linkKey = r.get("linkKeys").asInt();

			linkKeys.add(linkKey);
		}
		return linkKeys;
	}

	public ArrayList<Long> getIntesectionOsmids() {
		String query = "MATCH (a:Intersection) RETURN a.osmid as osmid";
		StatementResult result = interrogation(query);
		ArrayList<Long> osmids = new ArrayList<>();
		Record r;
		long osmid;

		while (result.hasNext()) {
			r = result.next();
			osmid = r.get("linkKeys").asInt();

			osmids.add(osmid);
		}
		return osmids;
	}

	public void setWeightRandom(ArrayList<Integer> linkKeys, double weight) {

		Random random = new Random();
		int lk = linkKeys.get(random.nextInt(linkKeys.size()));

		String query = "MATCH ()-[r:STREET{id:" + lk + "}]->() SET r.weight= " + weight;

		transaction(query);
		setLastModified();
	}

	public ArrayList<Long> getIntersectionOsmids() {
		String query = "MATCH (a:Intersection) RETURN a.osmid as osmid";
		StatementResult result = interrogation(query);
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

	public void setBCRandom(ArrayList<Long> osmids, double BC) {
		Random random = new Random();
		long osmid = osmids.get(random.nextInt(osmids.size()));

		String query = "match(n:Intersection{osmid:" + osmid + "}) SET n.betweenness = " + BC;
		interrogation(query);
		setLastModified();
	}

	@Override
	public void setStreetInterrupted(int id, boolean interrupted) {
		String query = "MATCH ()-[s:STREET{id:" + id + "}]->() SET s.interruption= " + interrupted + " return *";
		transaction(query);
		// TODO gestire valore di ritorno con try/catch e gestione errore

	}

	@Override
	public boolean setStreetInterrupted(long osmidStart, long osmidDest, boolean interrupted) {

		return false;
	}

//	public LocalDateTime setLastModified() {
//		StatementResult result = interrogation(
//				"MATCH (a:Control) SET a.timestamp = localdatetime() Return a.timestamp");
//		Record r = result.single();
//		LocalDateTime ldt = r.get("a.timestamp").asLocalDateTime();
//		return ldt;
//	}

	public Intersection getNearestIntersection(Coordinate position) {
		// String query= "MATCH (t:Intersection)-[:STREET]->(s:Intersection)\r\n" +
		// "WITH point({ longitude: "+position.getLongitude()+", latitude:
		// "+position.getLatitude()+"}) AS startPoint, point({ longitude: s.lon,
		// latitude: s.lat }) AS destPoint\r\n" +
		// "RETURN round(distance(trainPoint, officePoint)) AS travelDistance,
		// destPoint";

		String query = "MATCH (t:Intersection)  " + "WITH t , distance(point({ longitude: " + position.getLongitude()
				+ ", latitude: " + position.getLatitude()
				+ " }), point({ longitude: t.longitude, latitude: t.latitude })) AS distance " + "order by distance "
				+ "return properties(t), distance  LIMIT 1";

		/*
		 * carmine String query = "with " + position.getLatitude() + " as lat , " +
		 * position.getLongitude() + " as lon, 0.7 as range " +
		 * "match (t:Intersection) " +
		 * "with (lon - t.longitude) as lenX, (lat - t.latitude) as lenY, t.osmid as osmid, t "
		 * + "where lenX < range and lenX > -range and lenY < range and lenY > -range "
		 * + "with sqrt((lenX*lenX)+(lenY*lenY)) as distance, lenX, lenY, osmid, t  " +
		 * "order by distance " + "return  osmid, distance, properties(t) limit 1";
		 */
		StatementResult result = interrogation(query);
		Record r = result.single();

		double dist = r.get("distance").asDouble();
		// System.out.println("dist: "+dist);
		if (dist > 10000) {
			return null; // TODO verifica
		}
		Value v = r.get("properties(t)");

		return convertIntersection(v);
	}

	public Intersection getNearestParking(Coordinate position) {
		// String query= "MATCH (t:Intersection)-[:STREET]->(s:Intersection)\r\n" +
		// "WITH point({ longitude: "+position.getLongitude()+", latitude:
		// "+position.getLatitude()+"}) AS startPoint, point({ longitude: s.lon,
		// latitude: s.lat }) AS destPoint\r\n" +
		// "RETURN round(distance(trainPoint, officePoint)) AS travelDistance,
		// destPoint";

		/*
		 * Chiara String query = "MATCH (t:Intersection{t.parking:true}) \r\n" +
		 * "WITH t as nodo, distance(point({ longitude: " + position.getLongitude() +
		 * ", latitude: " + position.getLatitude() +
		 * " }), point({ longitude: t.longitude, latitude: t.latitude }) ) AS dist\r\n"
		 * + "return nodo.osmid, dist order by dist LIMIT 1";
		 */

		String query = "with " + position.getLatitude() + " as lat , " + position.getLongitude()
				+ " as lon, 0.7 as range " + "match (t:Intersection {parking : true}) "
				+ "with (lon - t.longitude) as lenX, (lat - t.latitude) as lenY, t.osmid as osmid, t "
				// + "where lenX < range and lenX > -range and lenY < range and lenY > -range "
				+ "with sqrt((lenX*lenX)+(lenY*lenY)) as distance, lenX, lenY, osmid, t  " + "order by distance "
				+ "return  osmid, distance, properties(t) limit 1";
		StatementResult result = interrogation(query);
		Record r = result.single();

		double dist = r.get("distance").asDouble();

		if (dist > 1000) {
			return null; // TODO verifica
		}
		Value v = r.get("properties(t)");

		return convertIntersection(v);
	}

	public Intersection getNearestHospital(Coordinate position) {
		// String query= "MATCH (t:Intersection)-[:STREET]->(s:Intersection)\r\n" +
		// "WITH point({ longitude: "+position.getLongitude()+", latitude:
		// "+position.getLatitude()+"}) AS startPoint, point({ longitude: s.lon,
		// latitude: s.lat }) AS destPoint\r\n" +
		// "RETURN round(distance(trainPoint, officePoint)) AS travelDistance,
		// destPoint";

		/*
		 * Chiara String query = "MATCH (t:Intersection{t.hospital:true}) \r\n" +
		 * "WITH t as nodo, distance(point({ longitude: " + position.getLongitude() +
		 * ", latitude: " + position.getLatitude() +
		 * " }), point({ longitude: t.longitude, latitude: t.latitude }) ) AS dist\r\n"
		 * + "return nodo.osmid, dist order by dist LIMIT 1";
		 */

		String query = "with " + position.getLatitude() + " as lat , " + position.getLongitude()
				+ " as lon, 0.7 as range " + "match (t:Intersection {hospital : true}) "
				+ "with (lon - t.longitude) as lenX, (lat - t.latitude) as lenY, t.osmid as osmid, t "
				// + "where lenX < range and lenX > -range and lenY < range and lenY > -range "
				+ "with sqrt((lenX*lenX)+(lenY*lenY)) as distance, lenX, lenY, osmid, t  " + "order by distance "
				+ "return  osmid, distance, properties(t) limit 1";

		StatementResult result = interrogation(query);
		Record r = result.single();

		double dist = r.get("distance").asDouble();
		/*
		 * if (dist > 1000) { return null; // TODO verifica }
		 */
		Value v = r.get("properties(t)");

		return convertIntersection(v);

		// return getIntersection(r.get("osmid").asLong());
	}

	@Override
	public ArrayList<Intersection> getAllParkings() {
		String query = "match (i:Intersection {parking:true}) return properties(i)";
		StatementResult result = interrogation(query);
		return convertToIntersectionArrayList(result, "i");
	}

	@Override
	public ArrayList<Intersection> getAllHospitals() {
		String query = "match (i:Intersection {hospital:true}) return properties(i)";
		StatementResult result = interrogation(query);
		return convertToIntersectionArrayList(result, "i");
	}

	private ArrayList<Intersection> convertToIntersectionArrayList(StatementResult result, String nodeName) {
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

	@Override
	public double distanceShortestPathBus(long osmidStart, long osmidDest) {
		String query = "MATCH (start:Intersection{osmid:" + osmidStart + "}), (end:Intersection{osmid:" + osmidDest
				+ "})\r\n"
				+ "CALL algo.shortestPath.stream(start, end, 'weight',{direction:'OUTGOING', nodeQuery:'MATCH(i:Intersection) RETURN id(i) as id',\r\n"
				+ "relationshipQuery:'MATCH(a:Intersection)-[s:STREET{interrupted:false}]->(b:Intersection) RETURN id(a) as source, id(b) as target, s.weight as weight', graph:'cypher'})"
				+ "YIELD nodeId, cost\r\n" + "RETURN algo.asNode(nodeId).osmid as vertexKeys, cost order by cost desc limit 1";

		StatementResult result = interrogation(query);
		
		Record r = result.single();
		return r.get("cost").asDouble();			
	}
}