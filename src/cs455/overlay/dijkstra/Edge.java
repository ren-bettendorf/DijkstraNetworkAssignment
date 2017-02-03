package cs455.overlay.dijkstra;

public class Edge {
	private String id;
	private Vertex source, destination;
	private int weight;

	public Edge(String id, Vertex source, Vertex destination, int weight) {
		this.id = id;
		this.source = source;
		this.destination = destination;
		this.weight = weight;
	}

	public String getID() {
		return this.id;
	}

	public Vertex getDestination() {
		return this.destination;
	}

	public Vertex getSource() {
		return this.source;
	}

	public int getWeight() {
		return this.weight;
	}
}
