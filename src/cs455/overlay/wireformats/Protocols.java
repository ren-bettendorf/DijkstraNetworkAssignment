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
	final int TASK_SUMMARY_REQUEST = 8;
	final int TASK_SUMMARY_RESPONSE = 9;
	final int RELAY_CONNECTION = 10;
	final int RELAY_MESSAGE = 11;
}
