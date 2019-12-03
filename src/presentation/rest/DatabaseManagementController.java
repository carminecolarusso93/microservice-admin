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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import data.dataModel.*;
import application.databaseManagementService.*;

@RequestScoped
@Path("/DatabaseManagement")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED })
public class DatabaseManagementController implements DatabaseManagementControllerApi {

	// @EJB (lookup =
	// "java:global/SmartCityUniversityChallenge/SCUChallenge-application/DatabaseManagementService!databaseManagement.DatabaseManagementServiceLocal")
	@EJB(lookup = "java:global/SCUChallenge-application-1/DatabaseManagementService!databaseManagementService.DatabaseManagementServiceRemote")
	DatabaseManagementServiceRemote database;

	public static final String DEFAULT_URI = "bolt://localhost:7687";
	public static final String DEFAULT_USERNAME = "neo4j";
	public static final String DEFAULT_PASSWORD = "assd";

	@Override
	public Response addIntersection(double longitude, double latitude, String highway, long osmid, String ref,
			float betweenness) {
		Intersection i = database.addIntersection(new Coordinate(longitude, latitude), highway, osmid, ref);
		return Response.ok().entity(i).build();
	}

	@Override
	public Response addStreet(String coordinatesJSON, int id, String access, String area, String bridge, long osmidStart,
			long osmidDest, String highway, String junction, int key, String arrayLanesJSON, double length,
			String maxSpeed, String name, boolean oneWay, String osmidEdgesJSON, String ref, String service,
			String tunnel, String width, int origId, double weight,double flow,double averageTravelTime) {
		
		Gson g = new Gson();
		ArrayList<Coordinate> coordinates = g.fromJson(coordinatesJSON, new TypeToken<ArrayList<Coordinate>>() {}.getType());
		ArrayList<Integer> arrayLanes = g.fromJson(arrayLanesJSON, new TypeToken<ArrayList<Integer>>() {}.getType());
		ArrayList<Long> osmidEdges = g.fromJson(osmidEdgesJSON, new TypeToken<ArrayList<Long>>() {}.getType());
		
		Street s = database.addStreet(coordinates, id, access, area, bridge, osmidStart, osmidDest, highway, junction, key,
				arrayLanes, length, maxSpeed, name, oneWay, osmidEdges, ref, service, tunnel, width, origId, weight,flow,averageTravelTime);
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
	public Response updateBetweennesExact() {
		database.updateBetweennesExact();
		return Response.ok().build();
	}

	@Override
	public Response updateBetweeennessBrandesRandom() {
		database.updateBetweeennessBrandesRandom();
		return Response.ok().build();
	}

	@Override
	public Response updateBetweeennessBrandesDegree() {
		database.updateBetweeennessBrandesDegree();
		return Response.ok().build();
	}

	@Override
	public Response updateBetweenness() {
		database.updateBetweenness();
		return Response.ok().build();
	}

}
