package application.ejb;

import java.util.ArrayList;

import javax.ejb.Local;

import data.dataModel.Intersection;

@Local
public interface TestEJBLocalAdmin {

	public String test();
	public ArrayList<Intersection> getTopCriticalNodes(int top);
}