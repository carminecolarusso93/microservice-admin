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
public class TestEJB implements TestEJBRemote, TestEJBLocal {

	DriverDatabase driverDatabase;
	protected String databeseURI = null;
	protected String databaseUser = null;
	protected String databasePass = null; 
    /**
     * Default constructor. 
     */
    public TestEJB() {
		try {
			ServerUtilities serverUtilities = new ServerUtilities();
			this.databeseURI = serverUtilities.getDatabaseUri();
			this.databaseUser = serverUtilities.getDatabaseUser();
			this.databasePass = serverUtilities.getDatabasePass();
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
			return serverUtilities.getDatabaseUri();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}
