package cs455.overlay.dijkstra;

public class Edge {
	private Vertex source, destination;
	private int weight;

	public Edge(Vertex source, Vertex destination) {
		this.source = source;
		this.destination = destination;
	}
	
	public void setWeight(int weight) {
		this.weight = weight;
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
	
	@Override
	public String toString() {
		return source.toString()+":"+destination.toString()+" "+weight;
	}
}
