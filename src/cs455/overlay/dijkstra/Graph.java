package cs455.overlay.dijkstra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Graph {
	private ArrayList<Vertex> vertices, possibleVertices;
	private ArrayList<Edge> edges;
	private boolean overlayStatus;

	/**
	 * Creates Graph object for Shortest Path to be found
	 */
	public Graph() {
		this.edges = new ArrayList<Edge>();
		setOverlayStatus(false);
	}

	/**
	 * Creates Graph object for Shortest Path to be found
	 *
	 * @param edges
	 *            Edges for the graph
	 */
	public Graph(ArrayList<Edge> edges) {
		this.edges = edges;
		this.vertices = new ArrayList<Vertex>();
		setOverlayStatus(true);
		createVertices();
	}

	/**
	 * Creates Vertices of the graph from the Edges
	 */
	private void createVertices() {
		for (Edge edge : edges) {
			if (!vertices.contains(edge.getSource())) {
				vertices.add(new Vertex(edge.getSource().getID(), 4));

			}
			if (!vertices.contains(edge.getDestination())) {
				vertices.add(new Vertex(edge.getDestination().getID(), 4));
			}
			vertices.get(vertices.indexOf(edge.getSource())).addConnection(edge.getDestination());
			vertices.get(vertices.indexOf(edge.getDestination())).addConnection(edge.getSource());
		}
	}

	/**
	 * Assign the weights for the edges
	 */
	public void assignWeights() {
		for (int index = 0; index < edges.size(); index += 2) {
			int weight = new Random().nextInt(10) + 1;
			edges.get(index).setWeight(weight);
			edges.get(index + 1).setWeight(weight);
		}
	}

	/**
	 * Sets the overlay for the Graph
	 *
	 * @param vertices
	 *            List of the Vertices of the Graph
	 * @param connectionsRequired
	 *            Number of connections required per Vertex. Must be greater
	 *            than or equal to 2
	 */
	public void setupOverlay(ArrayList<Vertex> vertices, int connectionsRequired) {
		// Remove all the old edges
		edges.clear();
		this.vertices = vertices;
		possibleVertices = new ArrayList<Vertex>();
		possibleVertices.addAll(vertices);

		// Sets the first round of connections and removing any chance of partitions
		setupFirstRoundConnections();
		
		// setupFirstRoundConnections sets all connections to 2 so only run this if there needs to be more than 2
		if (connectionsRequired > 2) {
			
			int count = 0;
			
			// Run while there are vertices that still need 
			while (!possibleVertices.isEmpty()) {

				count++;
				Vertex first, second;

				// Grab random vertex
				first = possibleVertices.get(new Random().nextInt(possibleVertices.size()));

				// Grab a second different random vertex
				do {
					second = possibleVertices.get(new Random().nextInt(possibleVertices.size()));
				} while (first.equals(second));

				// Check if they are already connected
				if (first.addConnection(second)) {
					second.addConnection(first);
					edges.add(new Edge(first, second));
					edges.add(new Edge(second, first));
				}

				// Remove if vertices have met their connections required and removes if they have
				if (first.getNumberConnection() == connectionsRequired) {
					possibleVertices.remove(first);
				}
				if (second.getNumberConnection() == connectionsRequired) {
					possibleVertices.remove(second);
				}

				// Check to make sure that the overlay will work and if not reset to after first round
				if (possibleVertices.size() == 1 || (possibleVertices.size() == 2
						&& possibleVertices.get(0).contains(possibleVertices.get(1)))) {
					count = 0;
					possibleVertices.clear();
					for (int i = 0; i < vertices.size(); i++) {
						vertices.get(i).clearConnections();
					}
					possibleVertices.addAll(vertices);
					edges.clear();
				}
			}
		}
		// Overlay has been set so no registering or dergistering allowed
		setOverlayStatus(true);
	}

	/**
	 * Setup the first round of connections so that no partitions can exist
	 */
	private void setupFirstRoundConnections() {
		for (int index = 0; index < possibleVertices.size() - 1; index++) {
			addConnections(possibleVertices.get(index), possibleVertices.get(index + 1));
		}
		addConnections(possibleVertices.get(0), possibleVertices.get(possibleVertices.size() - 1));
	}

	/**
	 * Adds the connections to each other
	 *
	 * @param first
	 *            First vertex added together
	 * @param second
	 *            Second vertex added together
	 */
	private void addConnections(Vertex first, Vertex second) {
		first.addConnection(second);
		second.addConnection(first);
		edges.add(new Edge(first, second));
		edges.add(new Edge(second, first));
	}

	
	/**
	 * Marshalls the edge string for transport
	 *
	 * @return Edge marshalled data
	 */
	public String marshallEdgeString() {
		String ret = "";

		for (Edge edge : edges) {
			ret += edge.toString() + "\n";
		}

		return ret;
	}

	/**
	 * Getter for edges of Graph
	 *
	 * @return Returns the list of edges
	 */
	public boolean getOverlayStatus() {
		return overlayStatus;
	}
	
	/**
	 * Sets the overlay status for the graph
	 *
	 * @param status Overlay status to be set to
	 */
	private void setOverlayStatus(boolean status) {
		this.overlayStatus = status;
	}

	/**
	 * Getter for vertices of Graph
	 *
	 * @return Returns the list of vertices
	 */
	public ArrayList<Vertex> getVertices() {
		return this.vertices;
	}

	/**
	 * Getter for edges of Graph
	 *
	 * @return Returns the list of edges
	 */
	public ArrayList<Edge> getEdges() {
		return this.edges;
	}

	/**
	 * Getter for edges of Graph
	 * @param source Source of the Vertex
	 * @param target Target of the Vertex
	 * @return Returns the edge weight between the vertices
	 */
	public int getEdgeWeight(Vertex source, Vertex target) {
		for (Edge edge : edges) {
			if ((edge.getSource().equals(source) && edge.getDestination().equals(target))
					|| (edge.getSource().equals(target) && edge.getDestination().equals(source))) {
				return edge.getWeight();
			}
		}
		return -1;
	}

	/**
	 * Getter for edges of Graph
	 *
	 * @return HashMap of the connections for a vertex
	 */
	public HashMap<Vertex, ArrayList<Vertex>> getConnectionsHashMap() {
		HashMap<Vertex, ArrayList<Vertex>> connections = new HashMap<Vertex, ArrayList<Vertex>>();

		for (Vertex vertex : vertices) {
			connections.put(vertex, new ArrayList<Vertex>());
		}

		for (int i = 0; i < edges.size(); i += 2) {
			connections.get(edges.get(i).getSource()).add(edges.get(i).getDestination());
		}

		return connections;
	}

}
