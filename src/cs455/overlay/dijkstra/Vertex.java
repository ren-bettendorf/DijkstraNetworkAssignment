package cs455.overlay.dijkstra;

public class Vertex {
	private String id;
	private int connections, maxConnections;
	public Vertex (String id, int maxConnections) {
		this.id = id;
		this.maxConnections = maxConnections;
		this.connections = 0;
	}

	public String getID() {
		return this.id;	
	}

	public boolean addConnection() {
		if(connections < maxConnections) {
			connections++;
			return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}else if (obj instanceof Vertex) {
			Vertex v = (Vertex)obj;
			return getID().equals(v.getID());
		}
		return false;
	}

}
