package presentation.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/rest")
public class RestApplication extends Application {
	public Set<Class<?>> getClasses() {
		Set<Class<?>> s = new HashSet<Class<?>>();
		s.add(presentation.rest.DatabaseManagementController.class);
		
		return s;
	}

	public Set<Object> getSingletons() {
		Set<Object> s = new HashSet<Object>();
		s.add(new presentation.rest.DatabaseManagementController());
		return s;
	}

}
