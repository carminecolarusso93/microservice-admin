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

import data.dataModel.*;



public class DriverDatabaseNeo4j implements DriverDatabase {
	public static final String JBOSS_SERVER_DATA_DIR = "jboss.server.data.dir";
	public static final String CONF_FILE_NAME = "database.conf";

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

	//TODO versione da environmet variable
//public DriverDatabaseNeo4j() throws FileNotFoundException {
//
//      String ip = System.getenv("ip");
//      String port = System.getenv("port");
//      uri = "bolt://" + ip + ":" + port;
//      user = System.getenv("user");
//      password = System.getenv("pass");
//      logger = Logger.getLogger(DriverDatabaseNeo4j.class);
//  }

	 public DriverDatabaseNeo4j() throws FileNotFoundException {
		File f =  new File(System.getProperty(JBOSS_SERVER_DATA_DIR), CONF_FILE_NAME);
		//System.out.println("path: "+new File("").getAbsolutePath());
		if(f.exists()) {
			Scanner s = new Scanner(f);
			
			String ip = s.nextLine().split("IP:")[1].trim();
			String port = s.nextLine().split("PORT:")[1].trim();
			uri = "bolt://" + ip + ":" + port;
			user = s.nextLine().split("USER:")[1].trim();
			password = s.nextLine().split("PASS:")[1].trim();
			logger = Logger.getLogger(DriverDatabaseNeo4j.class);
			
			s.close();
		}else {
			throw new FileNotFoundException("File \'"+ f.getPath() +"\' not found");
		}
			
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
		// logger.info("transaction: " + query);
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
	public Intersection addIntersection(Coordinate c, String highway, long osmid, String ref) {
		String query = "MERGE (a:Intersection {longitude: " + c.getLongitude() + ", latitude: " + c.getLatitude()
				+ ", highway: \"" + highway + "\", osmid: " + osmid + ", ref: \"" + ref + "\",  betweenness: 0})"
				+ " RETURN a.osmid";
		StatementResult result = transaction(query);
		if (result.single().get("a.osmid").asLong() == osmid) {
			return new Intersection(c, highway, osmid, ref);
		}
		return null;

	}

	// ADD

	public Intersection addIntersectionInit(Coordinate c, String highway, long osmid, String ref, double betweenness) {

		String query = "CREATE (a:Intersection {longitude: " + c.getLongitude() + ", latitude: " + c.getLatitude()
				+ ", highway: \"" + highway + "\", osmid: " + osmid + ", ref: \"" + ref + "\", betweenness: "
				+ betweenness + "})" + " RETURN a.osmid";
		StatementResult result = transaction(query);
		if (result.single().get("a.osmid").asLong() == osmid) {
			return new Intersection(c, highway, osmid, ref, betweenness);
		}
		return null;

	}

	public Street addStreetInit(ArrayList<Coordinate> coordinates, int id, String access, String area, String bridge,
			long osmidStart, long osmidDest, String highway, String junction, int key, ArrayList<Integer> arrayLanes,
			double length, String maxSpeed, String name, boolean oneWay, ArrayList<Long> osmidEdges, String ref,
			String service, String tunnel, String width, int origId, double weight, double flow,
			double averageTravelTime) {

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
				+ ", service : \"" + service + "\"" + ", tunnel : \"" + tunnel + "\"" + ", width : \"" + width + "\""
				+ ", origId : " + origId + ", weight : " + weight + ", flow : " + flow + ", averageTravelTime : "
				+ averageTravelTime + "}]->(b) " + "RETURN r.id";

		// System.out.println("query: "+query);
		StatementResult result = transaction(query);

		
			return new Street(coordinates, id, access, area, bridge, osmidStart, osmidDest, highway, junction, key,
					arrayLanes, length, maxSpeed, name, oneWay, osmidEdges, ref, service, tunnel, width, origId, weight,
					flow, averageTravelTime);
		

		//return null;
	}

	@Override
	public Street addStreet(ArrayList<Coordinate> coordinates, int id, String access, String area, String bridge,
			long osmidStart, long osmidDest, String highway, String junction, int key, ArrayList<Integer> arrayLanes,
			double length, String maxSpeed, String name, boolean oneWay, ArrayList<Long> osmidEdges, String ref,
			String service, String tunnel, String width, int origId, double weight, double flow,
			double averageTravelTime) {

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
				+ ", service : \"" + service + "\"" + ", tunnel : \"" + tunnel + "\"" + ", width : \"" + width + "\""
				+ ", origId : " + origId + ", weight : " + weight + ", flow : " + flow + ", averageTravelTime : "
				+ averageTravelTime + "}]->(b) " + "RETURN r.id";

		// System.out.println("query: "+query);
		StatementResult result = transaction(query);

		if (result.single().get("r.id").asInt() == id) {
			return new Street(coordinates, id, access, area, bridge, osmidStart, osmidDest, highway, junction, key,
					arrayLanes, length, maxSpeed, name, oneWay, osmidEdges, ref, service, tunnel, width, origId, weight,
					flow, averageTravelTime);
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

		//System.out.println(query);
		StatementResult result = transaction(query);

		Value r = result.single().get("properties(a)");
		// String nome = r.get("name").asString();

		Coordinate c = new Coordinate(r.get("longitude").asDouble(), r.get("latitude").asDouble());
		String highway = r.get("highway").asString();
		long id = r.get("osmid").asLong();
		String ref = r.get("ref").asString();

		return new Intersection(c, highway, id, ref, betweenness);

	}

	// GET
	@Override
	public Intersection getIntersection(long osmid) {
		String query = "MATCH (a:Intersection {osmid:" + osmid + "}) RETURN properties(a)";

		// System.out.println(query);
		StatementResult result = transaction(query);

		Value r = result.single().get("properties(a)");
		// String nome = r.get("name").asString();
		Coordinate c = new Coordinate(r.get("longitude").asDouble(), r.get("latitude").asDouble());

		String highway = r.get("highway").asString();
		long id = r.get("osmid").asLong();
		String ref = r.get("ref").asString();

		double betweenness = r.get("betweenness").asDouble();

		return new Intersection(c, highway, id, ref, betweenness, getStreets(osmid));

	}

	@Override
	public Intersection getIntersectionLight(long osmid) {
		String query = "MATCH (a:Intersection {osmid:" + osmid + "}) RETURN properties(a)";

		// System.out.println(query);
		StatementResult result = transaction(query);

		Value r = result.single().get("properties(a)");
		// String nome = r.get("name").asString();

		Coordinate c = new Coordinate(r.get("longitude").asDouble(), r.get("latitude").asDouble());

		String highway = r.get("highway").asString();
		long id = r.get("osmid").asLong();
		String ref = r.get("ref").asString();

		double betweenness = r.get("betweenness").asDouble();

		return new Intersection(c, highway, id, ref, betweenness);

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
		String service = v.get("service").asString();
		String tunnel = v.get("tunnel").asString();
		String width = v.get("width").asString();
		int origId = v.get("origId").asInt();
		double weight = v.get("weight").asDouble();
		double flow = v.get("flow").asDouble();
		double averageTravelTime = v.get("averageTravelTime").asDouble();

		return new Street(coordinates, ids, access, area, bridge, osmidStart, osmidDest, highway, junction, key,
				arrayLanes, length, maxSpeed, name, oneWay, osmidEdges, ref, service, tunnel, width, origId, weight,
				flow, averageTravelTime);

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
		String service = v.get("service").asString();
		String tunnel = v.get("tunnel").asString();
		String width = v.get("width").asString();
		int origId = v.get("origId").asInt();
		double weight = v.get("weight").asDouble();
		double flow = v.get("flow").asDouble();
		double averageTravelTime = v.get("averageTravelTime").asDouble();

		return new Street(coordinates, ids, access, area, bridge, osmidStart, osmidDest, highway, junction, key,
				arrayLanes, length, maxSpeed, name, oneWay, osmidEdges, ref, service, tunnel, width, origId, weight,
				flow, averageTravelTime);

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
				+ "})\r\n" + "CALL algo.shortestPath.stream(start, end, 'weight',{direction:'OUTGOING'})\r\n"
				+ "YIELD nodeId, cost\r\n" + "RETURN algo.asNode(nodeId).osmid as vertexKeys";

		StatementResult result = interrogation(query);
		ArrayList<Long> shortestPath = new ArrayList<>();
		Record r;
		long vertexKey;
		while (result.hasNext()) {
			r = result.next();
			vertexKey = r.get("vertexKeys").asLong();

			shortestPath.add(vertexKey);
		}
		return shortestPath;
	}

	@Override
	public ArrayList<Intersection> getTopCriticalNodes(int top) {
		String query = "MATCH (a:Intersection) RETURN properties(a) ORDER BY a.betweenness DESC LIMIT " + top;
		StatementResult result = interrogation(query);
		ArrayList<Intersection> criticalNodes = new ArrayList<>();
		Record r;
		Value v;
		while (result.hasNext()) {
			r = result.next();
			v = r.get("properties(a)");
			Coordinate c = new Coordinate(v.get("longitude").asDouble(), v.get("latitude").asDouble());

			String highway = v.get("highway").asString();
			long id = v.get("osmid").asLong();
			String ref = v.get("ref").asString();
			double betweenness = v.get("betweenness").asDouble();

			criticalNodes.add(new Intersection(c, highway, id, ref, betweenness));
		}
		return criticalNodes;
	}

	@Override
	public ArrayList<Intersection> getThresholdCriticalNodes(double threshold) {
		String query = "	MATCH (a:Intersection) WHERE a.betweenness > " + threshold + " RETURN properties(a)";
		StatementResult result = interrogation(query);
		ArrayList<Intersection> criticalNodes = new ArrayList<>();
		Record r;
		Value v;
		while (result.hasNext()) {
			r = result.next();
			v = r.get("properties(a)");
			Coordinate c = new Coordinate(v.get("longitude").asDouble(), v.get("latitude").asDouble());

			String highway = v.get("highway").asString();
			long id = v.get("osmid").asLong();
			String ref = v.get("ref").asString();
			double betweenness = v.get("betweenness").asDouble();

			criticalNodes.add(new Intersection(c, highway, id, ref, betweenness));
		}
		return criticalNodes;

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
	public ArrayList<Coordinate> shortestPathCoordinate(long osmid1, long osmid2) {
		String query = "MATCH (start:Intersection{osmid:" + osmid1 + "}), (end:Intersection{osmid:" + osmid2 + "})\r\n"
				+ "CALL algo.shortestPath.stream(start, end, 'weight',{direction:'OUTGOING'})\r\n"
				+ "YIELD nodeId, cost\r\n" + "RETURN algo.asNode(nodeId).osmid as vertexKeys";

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
		transaction("MATCH (a:Control)  SET a.timestamp = localdatetime() WITH a CALL streams.publish('test', a.timestamp) Return *");
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

		String query = "MATCH ()-[r:STREET{id:" + lk + "}]->() SET r.weight= " + weight ;
		
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

//	public LocalDateTime setLastModified() {
//		StatementResult result = interrogation(
//				"MATCH (a:Control) SET a.timestamp = localdatetime() Return a.timestamp");
//		Record r = result.single();
//		LocalDateTime ldt = r.get("a.timestamp").asLocalDateTime();
//		return ldt;
//	}

}