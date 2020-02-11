package util;

import data.databaseDriver.DAOAdmin;
import data.databaseDriver.DAOAdminNeo4jImpl;

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
        DAOAdmin driverDatabaseNeo4j = new DAOAdminNeo4jImpl("bolt://172.18.10.145:32000", "neo4j", "password");
        driverDatabaseNeo4j.openConnection();
//
////        driverDatabaseNeo4j.setStreetWeight(2,10);
////       ArrayList<Intersection> topNodes= driverDatabaseNeo4j.getTopCriticalNodes(10);
////       System.out.println("topNodes = " + topNodes);
////
////       driverDatabaseNeo4j.setStreetInterrupted(1, true);
//        driverDatabaseNeo4j.getStreetGeometry(89374, 117971);

//        driverDatabaseNeo4j.shortestPath(1, 1000);
       driverDatabaseNeo4j.closeConnection();
    }


}
