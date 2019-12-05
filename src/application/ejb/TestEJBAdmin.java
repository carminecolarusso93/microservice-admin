package application.ejb;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import data.dataModel.Intersection;
import data.databaseDriver.DriverDatabase;
import data.databaseDriver.DriverDatabaseNeo4j;
import util.ServerUtilities;

/**
 * Session Bean implementation class TestEJB
 */
@Stateless
@LocalBean
public class TestEJBAdmin implements TestEJBRemoteAdmin, TestEJBLocalAdmin {

	DriverDatabase driverDatabase;
	protected String databeseURI = null;
	protected String databaseUser = null;
	protected String databasePass = null; 
    /**
     * Default constructor. 
     */
    public TestEJBAdmin() {
		try {
			ServerUtilities serverUtilities = new ServerUtilities();
			this.databeseURI = serverUtilities.getDatabaseCoreUri();
			this.databaseUser = serverUtilities.getDatabaseCoreUser();
			this.databasePass = serverUtilities.getDatabaseCorePass();
			driverDatabase = new DriverDatabaseNeo4j(databeseURI, databaseUser, databasePass);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}    }

	
	/**
	 * Called after the EJB construction.
	 * Open the connection to the database.
	 */
	@PostConstruct
	public void connect() {
		driverDatabase.openConnection();
	}
	
	/**
	 * Called before the EJB destruction.
	 * Close the connection to the database.
	 */
	@PreDestroy
	public void preDestroy() {
		driverDatabase.closeConnection();
	}
    
	@Override
	public ArrayList<Intersection> getTopCriticalNodes(int top) {
		return driverDatabase.getTopCriticalNodes(top);
	}
	
	@Override
	public String test() {
		try {
			ServerUtilities serverUtilities = new ServerUtilities();
			return serverUtilities.getDatabaseCoreUri();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}
