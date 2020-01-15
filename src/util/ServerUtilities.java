package util;

import java.io.File;
import java.io.FileNotFoundException;

public class ServerUtilities {

	public static final String CONF_FILE_NAME = "server_configuration.xml";
	public static final String JBOSS_SERVER_DATA_DIR = "jboss.server.data.dir";

	protected File configurationFile;

	public static File getConfigurationFile() throws FileNotFoundException {
		File f = new File(System.getProperty(JBOSS_SERVER_DATA_DIR)+"/"+CONF_FILE_NAME);
		File f2 = new File("/opt/app-root/src/" + CONF_FILE_NAME);
		if (f.exists()) {
			return f;
		} else if (f2.exists()) {
			return f2;

		} else {
			throw new FileNotFoundException("File \'" + CONF_FILE_NAME + "\' not found");
		}
	}

	public ServerUtilities() throws FileNotFoundException {
		this.configurationFile = ServerUtilities.getConfigurationFile();
	}

	public String getDatabaseReplicaUri() {
		String ip = ConfigurationParser.readElementFromFileXml(configurationFile, "neo4j-replica", "bolt-ip");
		String port = ConfigurationParser.readElementFromFileXml(configurationFile, "neo4j-replica", "bolt-port");
		return "bolt://" + ip + ":" + port;
	}
	public String getDatabaseReplicaUser() {
		return ConfigurationParser.readElementFromFileXml(configurationFile, "neo4j-replica", "user");
	}

	public String getDatabaseReplicaPass() {
		return ConfigurationParser.readElementFromFileXml(configurationFile, "neo4j-replica", "password");
	}

	public String getDatabaseCoreUri() {
		String ip = ConfigurationParser.readElementFromFileXml(configurationFile, "neo4j-core", "bolt-ip");
		String port = ConfigurationParser.readElementFromFileXml(configurationFile, "neo4j-core", "bolt-port");
		return "bolt+routing://" + ip + ":" + port;
	}

	public String getDatabaseCoreUser() {
		return ConfigurationParser.readElementFromFileXml(configurationFile, "neo4j-core", "user");
	}

	public String getDatabaseCorePass() {
		return ConfigurationParser.readElementFromFileXml(configurationFile, "neo4j-core", "password");
	}

	@Override
	public String toString() {
		return "ServerUtilities{" +
				"configurationFile=" + configurationFile +
				'}';
	}
}
