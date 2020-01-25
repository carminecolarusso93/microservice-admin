package util;

import data.dataModel.Intersection;
import data.databaseDriver.DriverDatabaseNeo4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Test {

    public static void main(String[] args) throws Exception {
       /* File f;

        File f1 = new File(ServerUtilities.CONF_FILE_NAME);
        File f2 = new File("/opt/app-root/src/" + ServerUtilities.CONF_FILE_NAME);
        if (f1.exists()) {
            f = f1;
        } else if (f2.exists()) {
            f = f2;

        } else {
            throw new FileNotFoundException("File \'" + ServerUtilities.CONF_FILE_NAME + "\' not found");
        }
        String ip = ConfigurationParser.readElementFromFileXml(f, "neo4j-core", "bolt-ip");
        String port = ConfigurationParser.readElementFromFileXml(f, "neo4j-core", "bolt-port");
        System.out.println( "bolt://" + ip + ":" + port);
        */
//
//        DriverDatabaseNeo4j driverDatabaseNeo4j = new DriverDatabaseNeo4j("bolt://localhost:7687", "neo4j", "password");
//        driverDatabaseNeo4j.openConnection();
//
////        driverDatabaseNeo4j.setStreetWeight(2,10);
////       ArrayList<Intersection> topNodes= driverDatabaseNeo4j.getTopCriticalNodes(10);
////       System.out.println("topNodes = " + topNodes);
////
////       driverDatabaseNeo4j.setStreetInterrupted(1, true);
//        driverDatabaseNeo4j.getStreetGeometry(89374, 117971);
//
//
//
//       driverDatabaseNeo4j.closeConnection();
    }


}
