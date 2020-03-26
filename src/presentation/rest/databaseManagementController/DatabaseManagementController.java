package presentation.rest.databaseManagementController;

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
import org.jboss.logging.Logger;
import presentation.rest.ResponseBuilder;


@RequestScoped
@Path("/otm-admin")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})

public class DatabaseManagementController implements DatabaseManagementControllerApi {

    // @EJB (lookup =
    // "java:global/SmartCityUniversityChallenge/SCUChallenge-application/DatabaseManagementService!databaseManagement.DatabaseManagementServiceLocal")
    @EJB
    DatabaseManagementServiceLocal database;


    static Logger logger = Logger.getLogger(DatabaseManagementController.class);

    @Override
    public Response test(boolean ejb) {
        logger.info("DatabaseManagementController.test");
        String test;
        try {
            if (ejb) {
                test = "EJB not injected";
                if (database != null) {
                    test = database.test();
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
        logger.info("DatabaseManagementController.addIntersection: longitude = " + longitude + ", latitude = " + latitude +
                ", highway = " + highway + ", osmid = " + osmid + ", ref = " + ref + ", betweenness = " + betweenness +
                ", parking = " + parking + ", hospital = " + hospital + ", busStop = " + busStop + ", museum = " + museum);
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
        logger.info("DatabaseManagementController.addStreet: coordinatesJSON = " + coordinatesJSON + ", id = " + id +
                ", access = " + access + ", area = " + area + ", bridge = " + bridge + ", osmidStart = " + osmidStart +
                ", osmidDest = " + osmidDest + ", highway = " + highway + ", junction = " + junction + ", key = " + key +
                ", arrayLanesJSON = " + arrayLanesJSON + ", length = " + length + ", maxSpeed = " + maxSpeed + ", name = " + name +
                ", oneWay = " + oneWay + ", osmidEdgesJSON = " + osmidEdgesJSON + ", ref = " + ref + ", transportService = " + transportService +
                ", tunnel = " + tunnel + ", width = " + width + ", origId = " + origId + ", weight = " + weight + ", flow = " + flow +
                ", averageTravelTime = " + averageTravelTime + ", interrupted = " + interrupted);
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
        logger.info("DatabaseManagementController.setStreetWeight: id = " + id + ", weight = " + weight);
        Street s = database.setStreetWeight(id, weight);
        return Response.ok().entity(s).build();
    }

    @Override
    public Response setBetweennessIntersection(long osmid, double betweennees) {
        logger.info("DatabaseManagementController.setBetweennessIntersection: osmid = " + osmid + ", betweennees = " + betweennees);
        Intersection i = database.setBetweennessIntersection(osmid, betweennees);
        return Response.ok().entity(i).build();
    }

    @Override
    public Response getIntersection(long osmid) {
        logger.info("DatabaseManagementController.getIntersection: osmid = " + osmid);
        Intersection i = database.getIntersection(osmid);
        return Response.ok().entity(i).build();
    }

    @Override
    public Response getStreetProperties(UriInfo info) {
        logger.info("DatabaseManagementController.getStreetProperties: info = " + info);
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
        logger.info("DatabaseManagementController.getStreets: osmid = " + osmid);
        HashMap<Integer, Street> s = database.getStreets(osmid);
        return Response.ok().entity(s).build();
    }

    @Override
    public Response deleteIntersection(long osmid) {
        logger.info("DatabaseManagementController.deleteIntersection: osmid = " + osmid);
        database.deleteIntersection(osmid);
        return Response.ok().build();
    }

    @Override
    public Response deleteStreet(int id) {
        logger.info("DatabaseManagementController.deleteStreet: id = " + id);
        database.deleteStreet(id);
        return Response.ok().build();
    }

//    @Override
//    public Response updateBetweennes(UriInfo info) {
//        logger.info("DatabaseManagementController.updateBetweennes: info = " + info);
//        String alg = info.getQueryParameters().getFirst("alg");
//        switch (alg) {
//            case "exact":
//                database.updateBetweennesExact();
//                return Response.ok().build();
//            case "brandes-random":
//                database.updateBetweeennessBrandesRandom();
//                return Response.ok().build();
//            case "brandes-degree":
//                database.updateBetweeennessBrandesDegree();
//                return Response.ok().build();
//            case "default":
//                database.updateBetweenness();
//                return Response.ok().build();
//            default:
//                return Response.serverError().build();
//        }
//    }

    @Override
    public Response getStreet(int id) {
        logger.info("DatabaseManagementController.getStreet: id = " + id);
        return Response.ok().entity(database.getStreet(id)).build();
    }


    @Override
    public Response setStreetInterrupted(int id, boolean interrupted) {
        logger.info("DatabaseManagementController.setStreetInterrupted: id = " + id + ", interrupted = " + interrupted);
        try {
            database.setStreetInterrupted(id, interrupted);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
        return Response.ok().build();
    }

}
