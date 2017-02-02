package cs455.overlay.wireformats;

public interface Protocols {
	final int REGISTER_REQUEST = 0;
	final int REGISTER_RESPONSE = 1;
	final int DEREGISTER_REQUEST = 2;
	final int DEREGISTER_RESPONSE = 3;
	final int MESSAGING_NODES_LIST = 4; 
	final int LINK_WEIGHTS = 5;
	final int TASK_INITIATE = 6;
	final int TASK_COMPLETE = 7;
	final int PULL_TRAFFIC_SUMMARY = 8;
	final int TRAFFIC_SUMMARY = 9;
	final int ERROR_STATUS = 86;
}
