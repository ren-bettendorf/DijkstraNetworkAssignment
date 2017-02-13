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

	public void findShortestPaths() {
		setInitialDistances();
		unsettledVertices.add(startVertex);

		while (unsettledVertices.size() > 0) {
			Vertex vertex = getClosestVertex();
			settledVertices.add(vertex);
			unsettledVertices.remove(vertex);
			findMinimalDistances(vertex);
		}
	}

	private void setInitialDistances() {
		for(Vertex vertex : otherVertices) {
			pathWeights.put(vertex, Integer.MAX_VALUE);
		}
		pathWeights.put(startVertex, 0);
	}

	private void findMinimalDistances(Vertex sourceVertex) {
		ArrayList<Vertex> adjacentNodes = getAdjacentVertices(sourceVertex);
		for (Vertex target : adjacentNodes) {
			int distance = graph.getEdgeWeight(sourceVertex, target);
			if (pathWeights.get(sourceVertex) + distance < pathWeights.get(target)) {
				pathWeights.put(target, pathWeights.get(sourceVertex) + distance);
				predecessors.put(target, sourceVertex);
				unsettledVertices.add(target);
			}
		}
	}

	private ArrayList<Vertex> getAdjacentVertices(Vertex sourceVertex) {
		ArrayList<Vertex> adjacentVertices = new ArrayList<Vertex>();
		for (Edge edge : edges) {
			if (edge.getSource().equals(sourceVertex) && !settledVertices.contains(edge.getDestination())) {
				adjacentVertices.add(edge.getDestination());
			}
		}
		return adjacentVertices;
	}

	private Vertex getClosestVertex() {
		Vertex minimum = unsettledVertices.remove(0);
		for (Vertex vertex : unsettledVertices) {
			if (pathWeights.get(vertex) < pathWeights.get(minimum)) {
				minimum = vertex;
			}
		}
		return minimum;
	}

	public ArrayList<Vertex> getPath(Vertex target) {
		ArrayList<Vertex> path = new ArrayList<Vertex>();
		Vertex step = target;
		path.add(step);
		while (predecessors.get(step) != null) {
			step = predecessors.get(step);
			path.add(step);
		}
		Collections.reverse(path);
		return path;
	}

	public void cacheRoutes() {
		relayCache = new HashMap<Vertex, ArrayList<Vertex>>();
		for (Vertex target : otherVertices) {
			relayCache.put(target, getPath(target));
		}
	}

	public String getFullPathWeights() {
		String printedPath = "";

		for (Vertex target : otherVertices) {
			int weight = 0;
			ArrayList<Vertex> route = new ArrayList<Vertex>(relayCache.get(target));
			Vertex source = route.remove(0);
			System.out.println("Route Length " + route.size());
			for(Vertex step : route) {
				System.out.println("Adding " + graph.getEdgeWeight(source, step) + " to weight " + weight);
				weight += graph.getEdgeWeight(source, step);
				source = step;
			}
			printedPath += target.getID() + "--" + weight + "--";
		}

		return printedPath.substring(0, printedPath.length() - 2);
	}
	
	public String getCachedRoute(Vertex target) {
		ArrayList<Vertex> route = new ArrayList<Vertex>(relayCache.get(target));
		String path = "";
		route.remove(0);
		for(Vertex vertex : route) {
			path += vertex.getID() + " ";
		}
		return path;
	}
}
