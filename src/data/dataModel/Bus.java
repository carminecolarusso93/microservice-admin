package data.dataModel;

public class Bus {
	private boolean active;
	private String name;
	private Coordinate c;
	
	public Bus(boolean active, String name, Coordinate c) {
		super();
		this.active = active;
		this.name = name;
		this.c = c;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Coordinate getC() {
		return c;
	}

	public void setC(Coordinate c) {
		this.c = c;
	}

	@Override
	public String toString() {
		return "Bus [active=" + active + ", name=" + name + ", c=" + c + "]";
	}
	
	
}
