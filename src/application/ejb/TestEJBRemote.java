package application.ejb;

import java.util.ArrayList;

import javax.ejb.Remote;

import data.dataModel.Intersection;

@Remote
public interface TestEJBRemote {

	public String test();
	public ArrayList<Intersection> getTopCriticalNodes(int top);
}