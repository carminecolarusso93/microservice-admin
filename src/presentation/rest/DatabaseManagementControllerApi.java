package presentation.rest;


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
	public Response addIntersection(@FormParam("longitude") double longitude, @FormParam("latitude") double latitude,
                                    @FormParam("highway") String highway, @FormParam("osmid") long osmid, @FormParam("ref") String ref,
                                    @FormParam("betweenness") float betweenness, @FormParam("parking") boolean parking,
                                    @FormParam("hospital") boolean hospital, @FormParam("busStop") boolean busStop,
                                    @FormParam("museum") boolean museum);

	@POST
	@Path("/streets")
	public Response addStreet(@FormParam("coordinatesJSON") String coordinatesJSON, @FormParam("id") int id,
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
	public Response setStreetWeight(@PathParam("id") int id, @FormParam("weight") double weight);

	@PUT
	@Path("/intersections/{osmid}")
	public Response setBetweennessIntersection(@PathParam("osmid") long osmid,
                                               @FormParam("betweennees") double betweennees);

	@GET
	@Path("/intersections/{osmid}")
	public Response getIntersection(@PathParam("osmid") long osmid);

	@GET
	@Path("/streets")
	public Response getStreetProperties(@Context UriInfo info);

	@GET
	@Path("/intersections/{osmid}/streets")
	public Response getStreets(@PathParam("osmid") long osmid);

	@DELETE
	@Path("/intersections/{osmid}")
	public Response deleteIntersection(@PathParam("osmid") long osmid);

	@DELETE
	@Path("/streets/{id}")
	public Response deleteStreet(@PathParam("id") int id);

	@PUT
	@Path("/criticalNodes")
	public Response updateBetweennes(@Context UriInfo info);

	@GET
	@Path("/streets/{id}")
	public Response getStreet(@PathParam("id") int id);

	@PUT
	@Path("/streets/event/{id}")
	public Response setStreetWeight_integrationPlants(@PathParam("id") int id, @FormParam("value") float weight);

	@GET
	@Path("/shortestPaths/integrationCC")
	public Response shortestPath_integrationCC(@QueryParam("sourceLongitude") double sourceLongitude,
                                               @QueryParam("sourceLatitude") double sourceLatitude,
                                               @QueryParam("destinationLongitude") double destinationLongitude,
                                               @QueryParam("destinationLatitude") double destinationLatitude,
                                               @DefaultValue("Coordinate") @QueryParam("type") String type);
	
	@GET
	@Path("/shortestPaths/integrationEzBus")
	public Response shortestPath_integrationEzBus(@QueryParam("sourceLongitude") double sourceLongitude,
                                                  @QueryParam("sourceLatitude") double sourceLatitude,
                                                  @QueryParam("destinationLongitude") double destinationLongitude,
                                                  @QueryParam("destinationLatitude") double destinationLatitude,
                                                  @DefaultValue("Coordinate") @QueryParam("type") String type);
	
	@PUT
	@Path("/streets/integrationESB/{id}")
	public Response setStreetInterrupted(@PathParam("id") int id, @FormParam("interrupted") boolean interrupted);

	@GET
	@Path("/waitingTime")
	public Response getTravelLength(@QueryParam("sourceLongitude") double sourceLongitude,
                                    @QueryParam("sourceLatitude") double sourceLatitude,
                                    @QueryParam("destinationLongitude") double destinationLongitude,
                                    @QueryParam("destinationLatitude") double destinationLatitude);
	
	@GET 
	@Path("/intersections/hospitals")
	public Response getAllHospitals();
	
	@GET 
	@Path("/intersections/parkings")
	public Response getAllParkings();

	@GET
	@Path("/test")
	public Response test(@QueryParam("ejb") boolean ejb);

}
