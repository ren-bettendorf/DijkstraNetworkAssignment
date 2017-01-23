package cs455.overlay.node;

import java.io.IOException;

import cs455.overlay.transport.TCPSender;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.*;

public class MessagingNode implements Node {
	private int port, nodeID;
	private TCPServerThread serverThread;

	public MessagingNode() throws IOException {
	}

	public void setServerThread(TCPServerThread serverThread) {
		this.serverThread = serverThread;
	}
	
	public void startServerThread() {
		new Thread(this.serverThread).start();
	}

	public void register() {

	}

	public void setID(int ID) {
		this.nodeID = ID;
	}

	private int getPort() {
		return this.port;
	}
	
	public void getPort(int port) {
		this.port = port;
	}

	// java cs455.overlay.node.MessagingNode registry_host registry_port
	public static void main(String[] args) {
		byte[] baData = (new String("registered on " + args[0] + " : " + args[1])).getBytes();
		if (args.length != 3) {
			System.out.println("Sorry but you need a host and port number entered");
		}
		MessagingNode mNode = null;
		try {
			mNode = new MessagingNode();
			mNode.setID(10);

			// Set portnumber to 0 so ServerSocket picks one
			mNode.setServerThread(new TCPServerThread(0, mNode));
			mNode.startServerThread();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			//Attempt to send data to another widget
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public String toString() {
		return "Node:" + this.nodeID + " : " + this.serverThread.getServerSocket().toString();
	}

	@Override
	public void onEvent(Event event) throws IOException {
		// TODO Auto-generated method stub

	}
}
