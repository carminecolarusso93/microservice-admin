package util;

import java.io.File;

public class Test {

    public static void main(String[] args) {
        File f = new File(System.getProperty(ServerUtilities.JBOSS_SERVER_DATA_DIR), "CONF_FILE_NAME");
        System.out.println(f.getAbsolutePath());
    }
}
