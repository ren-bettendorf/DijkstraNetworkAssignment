package cs455.overlay.dijkstra;

import java.util.ArrayList;

public class Vertex {
	private String id;
	private int maxConnections;
	private ArrayList<Vertex> connections;
	public Vertex (String id, int maxConnections) {
		this.id = id;
		this.maxConnections = maxConnections;
		this.connections = new ArrayList<Vertex>();
	}

	public String getID() {
		return this.id;	
	}

	public boolean addConnection(Vertex other) {
		if(getNumberConnection() < maxConnections && !connections.contains(other)) {
			connections.add(other);
			return true;
		}
		return false;
	}
	
	public ArrayList<Vertex> getConnections() {
		return this.connections;
	}
	
	public int getNumberConnection() {
		return this.connections.size();
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

	@Override
	public String toString() {
		return id;
	}
}
