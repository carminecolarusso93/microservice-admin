package presentation.rest;

import java.util.ArrayList;
import java.util.HashMap;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import application.databaseManagementService.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import data.dataModel.*;
import application.trafficMonitoringService.TrafficMonitoringServiceLocal;


@RequestScoped
@Path("/otm-admin")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED })
public class DatabaseManagementController implements DatabaseManagementControllerApi {

	// @EJB (lookup =
	// "java:global/SmartCityUniversityChallenge/SCUChallenge-application/DatabaseManagementService!databaseManagement.DatabaseManagementServiceLocal")
	@EJB
	DatabaseManagementServiceLocal database;
	private TrafficMonitoringServiceLocal trafficMonitoringService;

	public static final String DEFAULT_URI = "bolt://localhost:7687";
	public static final String DEFAULT_USERNAME = "neo4j";
	public static final String DEFAULT_PASSWORD = "assd";

	@Override
	public Response test(boolean ejb) {
		String test;
		try {
			if (ejb){
				test = "EJB not injected";
				if (trafficMonitoringService != null) {
					test = trafficMonitoringService.test();
				}
			} else {
				test = "Test-string";
			}
			return ResponseBuilder.createOkResponse(test);
		} catch (Exception e) {
			e.printStackTrace();
			return Response.serverError().build();
		}

	}

	@Override
	public Response addIntersection(double longitude, double latitude, String highway, long osmid, String ref,
			float betweenness, boolean parking, boolean hospital, boolean busStop, boolean museum) {
		Intersection i = database.addIntersection(new Coordinate(longitude, latitude), highway, osmid, ref, parking,
				hospital, busStop, museum);
		return Response.ok().entity(i).build();
	}

	@Override
	public Response addStreet(String coordinatesJSON, int id, String access, String area, String bridge,
			long osmidStart, long osmidDest, String highway, String junction, int key, String arrayLanesJSON,
			double length, String maxSpeed, String name, boolean oneWay, String osmidEdgesJSON, String ref,
			boolean transportService, String tunnel, String width, int origId, double weight, double flow,
			double averageTravelTime, boolean interrupted) {

		Gson g = new Gson();
		ArrayList<Coordinate> coordinates = g.fromJson(coordinatesJSON, new TypeToken<ArrayList<Coordinate>>() {
		}.getType());
		ArrayList<Integer> arrayLanes = g.fromJson(arrayLanesJSON, new TypeToken<ArrayList<Integer>>() {
		}.getType());
		ArrayList<Long> osmidEdges = g.fromJson(osmidEdgesJSON, new TypeToken<ArrayList<Long>>() {
		}.getType());

		Street s = database.addStreet(coordinates, id, access, area, bridge, osmidStart, osmidDest, highway, junction,
				key, arrayLanes, length, maxSpeed, name, oneWay, osmidEdges, ref, transportService, tunnel, width,
				origId, weight, flow, averageTravelTime, interrupted);
		return Response.ok().entity(s).build();
	}

	@Override
	public Response setStreetWeight(int id, double weight) {
		Street s = database.setStreetWeight(id, weight);
		return Response.ok().entity(s).build();
	}

	@Override
	public Response setBetweennessIntersection(long osmid, double betweennees) {
		Intersection i = database.setBetweennessIntersection(osmid, betweennees);
		return Response.ok().entity(i).build();
	}

	@Override
	public Response getIntersection(long osmid) {
		Intersection i = database.getIntersection(osmid);
		return Response.ok().entity(i).build();
	}

	@Override
	public Response getStreetProperties(UriInfo info) {
		String id = info.getQueryParameters().getFirst("id");
		String osmidStart = info.getQueryParameters().getFirst("osmidStart");
		String osmidDest = info.getQueryParameters().getFirst("osmidDest");

		if (id != null) {
			Street s = database.getStreet(Integer.parseInt(id));
			return Response.ok().entity(s).build();
		}
		if (osmidStart != null && osmidDest != null) {
			int key = database.getLinkKey(Integer.parseInt(osmidStart), Integer.parseInt(osmidDest));
			return Response.ok().entity(key).build();
		}
		return Response.serverError().build();
	}

	@Override
	public Response getStreets(long osmid) {
		HashMap<Integer, Street> s = database.getStreets(osmid);
		return Response.ok().entity(s).build();
	}

	@Override
	public Response deleteIntersection(long osmid) {
		database.deleteIntersection(osmid);
		return Response.ok().build();

	}

	@Override
	public Response deleteStreet(int id) {
		database.deleteStreet(id);
		return Response.ok().build();
	}

	@Override
	public Response updateBetweennes(UriInfo info) {
		String alg = info.getQueryParameters().getFirst("alg");
		switch (alg) {
		case "exact":
			database.updateBetweennesExact();
			return Response.ok().build();
		case "brandes-random":
			database.updateBetweeennessBrandesRandom();
			return Response.ok().build();
		case "brandes-degree":
			database.updateBetweeennessBrandesDegree();
			return Response.ok().build();
		case "default":
			database.updateBetweenness();
			return Response.ok().build();
		default:
			return Response.serverError().build();
		}
	}

	@Override
	public Response getStreet(int id) {
		return Response.ok().entity(database.getStreet(id)).build();
	}

	@Override
	public Response setStreetWeight_integrationPlants(int id, float weight) {
		database.setStreetWeight(id, weight);
		return Response.ok().build();
	}

	@Override
	public Response shortestPath_integrationCC(double sourceLongitude, double sourceLatitude, double destinationLongitude,
			double destinationLatitude, String type) {
		Coordinate source = new Coordinate(sourceLongitude, sourceLatitude);
		Coordinate destination = new Coordinate(destinationLongitude, destinationLatitude);

		long osmidS = database.getNearestIntersection(source).getOsmid();
		long osmidD = database.getNearestIntersection(destination).getOsmid();

		if (osmidS != 0 && osmidD != 0) {
			if (type.equals("Coordinate")) {
				ArrayList<Coordinate> coords = trafficMonitoringService.shortestPathCoordinate(osmidS, osmidD);
				return ResponseBuilder.createOkResponse(coords);
			} else if (type.equals("Intersection")) {
				ArrayList<Long> osmids = trafficMonitoringService.shortestPath(osmidS, osmidD);
				ArrayList<Intersection> inters = new ArrayList<>();
				for (Long l : osmids) {
					inters.add(trafficMonitoringService.getIntersection(l));
				}
				return ResponseBuilder.createOkResponse(inters);
			}
		}
		return null;
	}

	@Override
	public Response shortestPath_integrationEzBus(double sourceLongitude, double sourceLatitude,
			double destinationLongitude, double destinationLatitude, String type) {
		Coordinate source = new Coordinate(sourceLongitude, sourceLatitude);
		Coordinate destination = new Coordinate(destinationLongitude, destinationLatitude);

		long osmidS = database.getNearestIntersection(source).getOsmid();
		long osmidD = database.getNearestIntersection(destination).getOsmid();

		if (osmidS != 0 && osmidD != 0) {
			if (type.equals("Coordinate")) {
				ArrayList<Coordinate> coords = trafficMonitoringService.shortestPathCoordinate(osmidS, osmidD);
				return ResponseBuilder.createOkResponse(coords);
			} else if (type.equals("Intersection")) {
				ArrayList<Long> osmids = trafficMonitoringService.shortestPath(osmidS, osmidD);
				ArrayList<Intersection> inters = new ArrayList<>();
				for (Long l : osmids) {
					inters.add(trafficMonitoringService.getIntersection(l));
				}
				return ResponseBuilder.createOkResponse(inters);
			}
		}		return null;
	}

	@Override
	public Response setStreetInterrupted(int id, boolean interrupted) {
		database.setStreetInterrupted(id, interrupted);
		return Response.ok().build();
	}

	@Override
	public Response getTravelLength(double sourceLongitude, double sourceLatitude, double destinationLongitude,
			double destinationLatitude) {
		Coordinate source = new Coordinate(sourceLongitude, sourceLatitude);
		Coordinate destination = new Coordinate(destinationLongitude, destinationLatitude);

		long osmidS = database.getNearestIntersection(source).getOsmid();
		long osmidD = database.getNearestIntersection(destination).getOsmid();
		
		double distance = database.distanceShortestPathBus(osmidS, osmidD);
		
		double time = distance / 40;
		
		return ResponseBuilder.createOkResponse(time);
	}

	@Override
	public Response getAllHospitals() {
		ArrayList<Intersection> resp = database.getAllHospitals(); 
		return ResponseBuilder.createOkResponse(resp);
	}

	@Override
	public Response getAllParkings() {
		ArrayList<Intersection> resp = database.getAllParkings(); 
		return ResponseBuilder.createOkResponse(resp);
	}

}
