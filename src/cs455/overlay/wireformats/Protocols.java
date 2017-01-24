package cs455.overlay.wireformats;

public enum PROTOCOLS {
	REGISTER_REQUEST(0), REGISTER_RESPONSE(1), DEREGISTER_REQUEST(2), DEREGISTER_RESPONSE(3), MESSAGING_NODES_LIST(4), 
	LINK_WEIGHTS(5), TASK_INITIATE(6), TASK_COMPLETE(7), PULL_TRAFFIC_SUMMARY(8), TRAFFIC_SUMMARY(9);
	
	private int id;
	PROTOCOLS(int id) {
		this.id = id;
	}
	
	public int getID() {
		return id;
	}
}
