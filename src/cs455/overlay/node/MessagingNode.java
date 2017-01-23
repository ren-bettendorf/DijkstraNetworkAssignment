package cs455.overlay.node;

import java.io.IOException;

import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.*;

public class MessagingNode implements Node {
	private int port, nodeID;
	private TCPServerThread serverThread;

	public MessagingNode(int port) throws IOException {
		this.port = port;
	}

	public void setServerThread(TCPServerThread serverThread) {
		this.serverThread = serverThread;
		this.serverThread.run();
	}

	public void register() {

	}

	public void setID(int ID) {
		this.nodeID = ID;
	}

	private int getPort() {
		return this.port;
	}

	// java cs455.overlay.node.MessagingNode registry_host registry_port local_port
	public static void main(String[] args) {
		byte[] data = (new String("registered on " + args[0] + " : " + args[1])).getBytes();
		if (args.length != 3) {
			System.out.println("Sorry but you need a host and port number entered");
		}
		MessagingNode mNode = null;
		System.out.println("Messaging Node is being created on port " + args[2] + " .......");
		try {
			mNode = new MessagingNode(Integer.parseInt(args[2]));
			mNode.setID(10);

			mNode.setServerThread(new TCPServerThread(mNode.getPort(), mNode));
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
