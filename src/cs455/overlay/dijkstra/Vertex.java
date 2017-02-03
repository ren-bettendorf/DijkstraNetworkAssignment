package cs455.overlay.dijkstra;

public class Vertex {
	private String id;

	public Vertex (String id) {
		this.id = id;
	}

	public String getID() {
		return this.id;	
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

}
