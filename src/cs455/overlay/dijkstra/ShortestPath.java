package cs455.overlay.dijkstra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ShortestPath {
	private Graph graph;
	private ArrayList<Vertex> vertices, otherVertices, settledVertices, unsettledVertices;
	private ArrayList<Edge> edges;
	private HashMap<Vertex, Vertex> predecessors;
	private HashMap<Vertex, Integer> pathWeights;
	private Vertex startVertex;
	private HashMap<Vertex, ArrayList<Vertex>> relayCache;

	/**
	* Creates ShortestPath object from a Graph and the source Vertex
	*
	* @param graph Graph information to find shortest paths
	* @param source Vertex where Dijkstra's Algorighm starts
	*/
	public ShortestPath(Graph graph, Vertex source) {
		this.graph = graph;
		this.edges = graph.getEdges();
		this.vertices = graph.getVertices();
		this.startVertex = source;
		this.otherVertices = new ArrayList<Vertex>(vertices);
		this.otherVertices.remove(startVertex);
		this.settledVertices = new ArrayList<Vertex>();
		this.unsettledVertices = new ArrayList<Vertex>();
		this.pathWeights = new HashMap<Vertex, Integer>();
		this.predecessors = new HashMap<Vertex, Vertex>();

		findShortestPaths();
		cacheRoutes();
	}

	/**
	* Finds the shortest distance to all other vertices
	*/
	public void findShortestPaths() {
		setInitialDistances();
		unsettledVertices.add(startVertex);

		while (!unsettledVertices.isEmpty()) {
			Vertex vertex = getClosestVertex();
			settledVertices.add(vertex);
			unsettledVertices.remove(vertex);
			findMinimalDistances(vertex);
		}
	}

	/**
	* Sets initial distance for source to 0 and all other vertices to max value
	*/
	private void setInitialDistances() {
		for(Vertex vertex : otherVertices) {
			pathWeights.put(vertex, Integer.MAX_VALUE);
		}
		pathWeights.put(startVertex, 0);
	}

	/**
	* Finds minimal distance from a given vertex to closest vertex
	*
	* @param source Vertex where node will find next shortest distance
	*/
	private void findMinimalDistances(Vertex sourceVertex) {
		// Gets all the adjacent nodes
		ArrayList<Vertex> adjacentNodes = getAdjacentVertices(sourceVertex);
		
		// Iterate through to find next closest
		for (Vertex target : adjacentNodes) {
			int distance = graph.getEdgeWeight(sourceVertex, target);
			
			// If the edge weight + previous distance is shorter than replace the old vertex
			if (pathWeights.get(sourceVertex) + distance < pathWeights.get(target)) {
				// Update the distance
				pathWeights.put(target, pathWeights.get(sourceVertex) + distance);
				// Update the previous vertex for searching shortest path
				predecessors.put(target, sourceVertex);
				// Target needs to be settled
				unsettledVertices.add(target);
			}
		}
	}

	/**
	* Gets the vertices that vertex is connected to that are not settled
	*
	* @param sourceVetex Vertex where connections will be found
	* @return List of unsettled adjacent vertices
	*/
	private ArrayList<Vertex> getAdjacentVertices(Vertex sourceVertex) {
		ArrayList<Vertex> adjacentVertices = new ArrayList<Vertex>();
		for (Edge edge : edges) {
			if (edge.getSource().equals(sourceVertex) && !settledVertices.contains(edge.getDestination())) {
				adjacentVertices.add(edge.getDestination());
			}
		}
		return adjacentVertices;
	}

	/**
	* Finds the next closest unsettled vertex
	* 
	* @return The next Vertex to settle
	*/
	private Vertex getClosestVertex() {
		Vertex minimum = unsettledVertices.get(0);
		for (Vertex vertex : unsettledVertices) {
			if (pathWeights.get(vertex) < pathWeights.get(minimum)) {
				minimum = vertex;
			}
		}
		unsettledVertices.remove(minimum);
		return minimum;
	}

	/**
	* Gets the path from a given vertex
	*
	* @param target Vertex to find path to
	* @return List of vertices to get to target
	*/
	public ArrayList<Vertex> getPath(Vertex target) {
		// Create return path
		ArrayList<Vertex> path = new ArrayList<Vertex>();
		Vertex step = target;
		
		// Add the target as the first node
		path.add(step);
		
		// Run until you are back at the starting node
		while (!predecessors.get(step).equals(startVertex)) {
			step = predecessors.get(step);
			path.add(step);
		}
		// Add the starting vertex for finding total path weight
		path.add(startVertex);
		
		// Reverse so we are starting at startVertex and not the target
		Collections.reverse(path);
		return path;
	}

	/**
	* Cache the routes so we don't have to continually find pathing.
	*/
	public void cacheRoutes() {
		relayCache = new HashMap<Vertex, ArrayList<Vertex>>();
		for (Vertex target : otherVertices) {
			relayCache.put(target, getPath(target));
		}
	}

	/**
	* Returns the full path weights to each vertex excluding startVertex
	*
	* @param printedPath String with all printed paths and weight
	*/
	public String getFullPathWeights() {
		String printedPath = "";

		for (Vertex target : otherVertices) {
			int weight = 0;
			// Create a copy of the relayCache for target vertex
			ArrayList<Vertex> route = new ArrayList<Vertex>(relayCache.get(target));
			Vertex source = route.remove(0);
			for(Vertex step : route) {
				weight += graph.getEdgeWeight(source, step);
				source = step;
			}
			printedPath += target.getID() + "--" + weight + "--";
		}

		return printedPath.substring(0, printedPath.length() - 2);
	}
	
	/**
	* Getter for the cached route
	*
	* @param target Vertex where the cached route ends
	* @return Returns the path to travel to the given node
	*/
	public String getCachedRoute(Vertex target) {
		ArrayList<Vertex> route = new ArrayList<Vertex>(relayCache.get(target));
		String path = "";
		route.remove(0);
		for(Vertex vertex : route) {
			path += vertex.getID() + " ";
		}
		return path;
	}

	/**
	* Getter for the list of vertices not including the startVertex
	*
	* @return List of other vertices
	*/
	public ArrayList<String> getOtherVertices() {
		ArrayList<String> otherVerticesStrings = new ArrayList<String>();
		for(Vertex vertex : otherVertices) {
			otherVerticesStrings.add(vertex.getID());
		}
		return otherVerticesStrings;
	}
}
