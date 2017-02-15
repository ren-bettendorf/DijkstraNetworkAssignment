package cs455.overlay.dijkstra;

public class Edge {
	private Vertex source, destination;
	private int weight;

	/**
	* Creates Edge object from two vertices
	*
	* @param source Vertex where Edge begins
	* @param destination Vertex where Edge ends
	*/
	public Edge(Vertex source, Vertex destination) {
		this.source = source;
		this.destination = destination;
	}
	
	/**
	* Sets the weight of the edge
	*
	* @param weight  Weight to be set to
	*/
	public void setWeight(int weight) {
		this.weight = weight;
	}

	/**
	* Getter for the destination Vertex
	*
	* @return  Returns destination of the Edge
	*/
	public Vertex getDestination() {
		return this.destination;
	}

	/**
	* Getter for the source Vertex
	*
	* @return  Returns source of the Edge
	*/
	public Vertex getSource() {
		return this.source;
	}

	/**
	* Getter for the weight of the Edge
	* 
	* @return  Returns weight of the Edge
	*/
	public int getWeight() {
		return this.weight;
	}
	
	/**
	* Overrides the toString() for better printing
	*
	* @return  Node details as a string
	*/
	@Override
	public String toString() {
		return source.toString()+" "+destination.toString()+" "+weight;
	}
}
