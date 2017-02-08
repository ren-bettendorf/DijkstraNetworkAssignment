package cs455.overlay.dijkstra;

import java.util.ArrayList;
import java.util.Random;

public class Graph {
	private ArrayList<Vertex> vertices, possibleVertices;
	private ArrayList<Edge> edges;
	private int connectionsRequired;

	public Graph(ArrayList<Vertex> vertices) {
		this.vertices = vertices;
		this.edges = new ArrayList<Edge>();
	}
	
	public void assignWeights() {
		for(Edge e : edges) {
			e.setWeight((int)Math.floor(Math.random()*10) + 1);
			System.out.println("Assigned "+e.toString());
		}
	}
	
	public void setupOverlay(int connectionsRequired) {
		possibleVertices = (ArrayList<Vertex>) vertices.clone();
		setupFirstRoundConnections();
		int count = 0;
		while(!possibleVertices.isEmpty()) {
			count++;
			System.out.println("[Iteration "+count+"] Size: "+possibleVertices.size());
			Vertex first = possibleVertices.get(new Random().nextInt(possibleVertices.size()));
			Vertex second;
			do {
				second = possibleVertices.get(new Random().nextInt(possibleVertices.size()));
			}while(first.equals(second));
			
			if(first.addConnection(second)) {
				System.out.println("Connected "+first.toString()+" and "+second.toString());
				second.addConnection(first);
			}
			
			if(first.getNumberConnection() == connectionsRequired) {
				System.out.println("Removing " + first.toString() + " due to max connections: " + first.getNumberConnection());
				possibleVertices.remove(first);
			}
			if(second.getNumberConnection() == connectionsRequired) {
				System.out.println("Removing " + second.toString() + " due to max connections: " + second.getNumberConnection());
				possibleVertices.remove(second);
			}
			if(possibleVertices.size() == 1) {
				possibleVertices = (ArrayList<Vertex>) vertices.clone();
			}
		}
	}
	
	private void setupFirstRoundConnections() {
		for(int index = 0; index < possibleVertices.size()-1; index++) {
			edges.add(new Edge(possibleVertices.get(index), possibleVertices.get(index+1)));
			possibleVertices.get(index).addConnection(possibleVertices.get(index+1));
			possibleVertices.get(index+1).addConnection(possibleVertices.get(index));
			
		}
		edges.add(new Edge(possibleVertices.get(0), possibleVertices.get(possibleVertices.size()-1)));
	}

	public ArrayList<Vertex> getVertices() {
		return this.vertices;
	}

	public ArrayList<Edge> getEdges() {
		return this.edges;
	}

	public void listWeights() {
		for(Edge edge : edges) {
			edge.setWeight(new Random().nextInt(10) + 1);
		}
	}

	public String marshallEdgeString() {
		String ret = "";
		
		for(Edge edge : edges) {
			ret += edge.toString() + "\n";
		}
		
		return ret;
	}
	
}
