package presentation.rest.databaseManagementController;


import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

public interface DatabaseManagementControllerApi {

	@POST
	@Path("/intersections")
	Response addIntersection(@FormParam("longitude") double longitude, @FormParam("latitude") double latitude,
							 @FormParam("highway") String highway, @FormParam("osmid") long osmid, @FormParam("ref") String ref,
							 @FormParam("betweenness") float betweenness, @FormParam("parking") boolean parking,
							 @FormParam("hospital") boolean hospital, @FormParam("busStop") boolean busStop,
							 @FormParam("museum") boolean museum);

	@POST
	@Path("/streets")
	Response addStreet(@FormParam("coordinatesJSON") String coordinatesJSON, @FormParam("id") int id,
					   @FormParam("access") String access, @FormParam("area") String area, @FormParam("bridge") String bridge,
					   @FormParam("osmidStart") long osmidStart, @FormParam("osmidDest") long osmidDest,
					   @FormParam("highway") String highway, @FormParam("junction") String junction, @FormParam("key") int key,
					   @FormParam("arrayLanesJSON") String arrayLanes, @FormParam("length") double length,
					   @FormParam("maxSpeed") String maxSpeed, @FormParam("name") String name, @FormParam("oneWay") boolean oneWay,
					   @FormParam("osmidEdgesJSON") String osmidEdges, @FormParam("ref") String ref,
					   @FormParam("transportService") boolean transportService, @FormParam("tunnel") String tunnel,
					   @FormParam("width") String width, @FormParam("origId") int origId, @FormParam("weight") double weight,
					   @FormParam("flow") double flow, @FormParam("averageTravelTime") double averageTravelTime,
					   @FormParam("interrupted") boolean interrupted);

	@PUT
	@Path("/streets/{id}")
	Response setStreetWeight(@PathParam("id") int id, @FormParam("weight") double weight);

	@PUT
	@Path("/intersections/{osmid}")
	Response setBetweennessIntersection(@PathParam("osmid") long osmid,
										@FormParam("betweennees") double betweennees);

	@GET
	@Path("/intersections/{osmid}")
	Response getIntersection(@PathParam("osmid") long osmid);

	@GET
	@Path("/streets")
	Response getStreetProperties(@Context UriInfo info);

	@GET
	@Path("/intersections/{osmid}/streets")
	Response getStreets(@PathParam("osmid") long osmid);

	@DELETE
	@Path("/intersections/{osmid}")
	Response deleteIntersection(@PathParam("osmid") long osmid);

	@DELETE
	@Path("/streets/{id}")
	Response deleteStreet(@PathParam("id") int id);

	@PUT
	@Path("/criticalNodes")
	Response updateBetweennes(@Context UriInfo info);

	@GET
	@Path("/streets/{id}")
	Response getStreet(@PathParam("id") int id);

	@PUT
	@Path("/streets/interruptions/{id}")
	Response setStreetInterrupted(@PathParam("id") int id, @FormParam("interrupted") boolean interrupted);

	@GET
	@Path("/test")
	Response test(@QueryParam("ejb") boolean ejb);

}
