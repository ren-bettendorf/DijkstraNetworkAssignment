package cs455.overlay.node;

import java.io.IOException;
import java.net.InetAddress;
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

	public int getPort() {
		return this.port;
	}
	
	public void getPort(int port) {
		this.port = port;
	}

	// java cs455.overlay.node.MessagingNode registry_host registry_port
	public static void main(String[] args) {
		byte[] baData = (new String("registered on " + args[0] + " : " + args[1])).getBytes();
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
	}

	@Override
	public String toString() {
		try {
			return "Node:" + this.nodeID + " : " + InetAddress.getLocalHost().getHostAddress() + " : " + this.serverThread.getPort();
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	@Override
	public void onEvent(Event event) throws IOException {
		// TODO Auto-generated method stub

	}
}
