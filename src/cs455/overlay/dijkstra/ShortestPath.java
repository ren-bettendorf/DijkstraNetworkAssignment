package cs455.overlay.dijkstra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ShortestPath {
	private Graph graph;
	private ArrayList<Vertex> vertices, settledNodes, unSettledNodes;
	private ArrayList<Edge> edges;
    private HashMap<Vertex, Vertex> predecessors;
    private HashMap<Vertex, Integer> distance;

	public ShortestPath(Graph graph) {
		this.graph = graph;
		this.edges = graph.getEdges();
		this.vertices = graph.getVertices();
	}

	public void execute(Vertex source) {
		settledNodes = new ArrayList<Vertex>();
		unSettledNodes = new ArrayList<Vertex>();
		distance = new HashMap<Vertex, Integer>();
		predecessors = new HashMap<Vertex, Vertex>();
		distance.put(source, 0);
		unSettledNodes.add(source);
		while (unSettledNodes.size() > 0) {
			Vertex node = getMinimum(unSettledNodes);
			settledNodes.add(node);
			unSettledNodes.remove(node);
			findMinimalDistances(node);
		}
	}

	private void findMinimalDistances(Vertex node) {
		ArrayList<Vertex> adjacentNodes = getNeighbors(node);
		for (Vertex target : adjacentNodes) {
			if (getShortestDistance(target) > getShortestDistance(node) + getDistance(node, target)) {
				distance.put(target, getShortestDistance(node) + getDistance(node, target));
				predecessors.put(target, node);
				unSettledNodes.add(target);
			}
		}

	}

	private int getDistance(Vertex node, Vertex target) {
		for (Edge edge : edges) {
			if (edge.getSource().equals(node) && edge.getDestination().equals(target)) {
				return edge.getWeight();
			}
		}
		throw new RuntimeException("Should not happen");
	}

	private ArrayList<Vertex> getNeighbors(Vertex node) {
		ArrayList<Vertex> neighbors = new ArrayList<Vertex>();
		for (Edge edge : edges) {
			if (edge.getSource().equals(node) && !isSettled(edge.getDestination())) {
				neighbors.add(edge.getDestination());
			}
		}
		return neighbors;
	}

	private Vertex getMinimum(ArrayList<Vertex> vertexes) {
		Vertex minimum = null;
		for (Vertex vertex : vertexes) {
			if (minimum == null) {
				minimum = vertex;
			} else {
				if (getShortestDistance(vertex) < getShortestDistance(minimum)) {
					minimum = vertex;
				}
			}
		}
		return minimum;
	}

	private boolean isSettled(Vertex vertex) {
		return settledNodes.contains(vertex);
	}

	private int getShortestDistance(Vertex destination) {
		Integer d = distance.get(destination);
		if (d == null) {
			return Integer.MAX_VALUE;
		} else {
			return d;
		}
	}

	/*
	 * This method returns the path from the source to the selected target and
	 * NULL if no path exists
	 */
	public ArrayList<Vertex> getPath(Vertex target) {
		ArrayList<Vertex> path = new ArrayList<Vertex>();
		Vertex step = target;
		// check if a path exists
		if (predecessors.get(step) == null) {
			System.out.println("No predecessors");
			return null;
		}
		path.add(step);
		while (predecessors.get(step) != null) {
			step = predecessors.get(step);
			path.add(step);
		}
		// Put it into the correct order
		Collections.reverse(path);
		return path;
	}
	
	public String getFullPathWeights(Vertex source) {
		System.out.println("Executing for " + source.toString());
		execute(source);
		String ret = "";
		ArrayList<Vertex> otherNodes = new ArrayList<Vertex>(vertices);
		otherNodes.remove(source);
		for(Vertex vertex : otherNodes) {
			System.out.println("Finding path for " + vertex.toString());
			if(!source.equals(vertex)) {
				ArrayList<Vertex> path = getPath(vertex);
				path.remove(path.indexOf(source));
				int pathWeight = 0;
				for(Vertex step : path) {
					pathWeight += graph.getEdgeWeight(source, step);
					System.out.println("Added " + step.toString() + " Weight " + pathWeight);
				}
				ret += vertex.getID() + "--" + pathWeight;
				System.out.println("Total Path Weight " + pathWeight);
			}
		}
		
		return ret;
	}
}
