package cs455.overlay.dijkstra;

import java.util.ArrayList;

public class Vertex {
	private String id;
	private int maxConnections;
	private ArrayList<Vertex> connections;
	
	/**
	* Creates Vertex object with an ID and number of max connections
	*
	* @param id Vertex ID
	* @param maxConnections Maximum number of connections allowed
	*/
	public Vertex (String id, int maxConnections) {
		this.id = id;
		this.maxConnections = maxConnections;
		this.connections = new ArrayList<Vertex>();
	}

	/**
	* Attempts to add a connection to the vertex
	*
	* @param other Vertex to attempt to add
	* @return Success or failure of adding other vertex
	*/
	public boolean addConnection(Vertex other) {
		if(getNumberConnection() < maxConnections && !connections.contains(other)) {
			connections.add(other);
			return true;
		}
		return false;
	}
	
	/**
	* Getter for the list of connections
	*
	* @return List of connections
	*/
	public ArrayList<Vertex> getConnections() {
		return this.connections;
	}
	
	/**
	* Getter for the number of current connections
	*
	* @return Number of connections
	*/
	public int getNumberConnection() {
		return this.connections.size();
	}

	/**
	* Override equals to determine equality between two vertices
	*
	* @param obj Object to determine equivalence to
	* @return Whether objects are equal
	*/
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
	
	/**
	* Creates hashcode for hashing Vertices
	*
	* @return hashCode of object
	*/
	@Override
    public int hashCode() {
            return id.hashCode();
    }

	/**
	* Creates the string version of Vertices
	*
	* @return String version of vertex
	*/
	@Override
	public String toString() {
		return id;
	}

	/**
	* Removes all connections
	*/
	public void clearConnections() {
		connections.clear();
	}

	/**
	* Checks where a vertex is already connected to
	*
	* @param vertex Vertex to check
	* @return Whether Vertex contains other
	*/
	public boolean contains(Vertex vertex) {
		return this.connections.contains(vertex);
	}

	/**
	* Getter for vertex ID
	*
	* @return Return the ID
	*/
	public String getID() {
		return this.id;	
	}
}
