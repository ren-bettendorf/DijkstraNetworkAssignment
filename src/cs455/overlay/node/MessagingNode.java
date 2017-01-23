package cs455.overlay.node;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import cs455.overlay.transport.TCPSender;
import cs455.overlay.wireformats.Event;

public class MessagingNode implements Node {
	private int port, nodeID;
	private TCPServerThread serverThread;
	
	public MessagingNode(int port) throws IOException
	{
		this.port = port;
	}
	
	public void register()
	{
		
	}
	
	public void setID(int ID) {
		this.nodeID = ID;
	}
	
	public Socket acceptConnection() throws IOException {
		return serverSocket.accept();
	}
	
	private int getPort() {
		return this.port;
	}

	@Override
	public void onEvent(Event event) throws IOException {
		// TODO Auto-generated method stub
		
	}
	
	// java cs455.overlay.node.MessagingNode registry_host registry_port local_port
	public static void main(String[] args) {
		byte[] data = (new String("registered on " + args[0] + " : " + args[1])).getBytes();
		if(args.length != 3) {
			System.out.println("Sorry but you need a host and port number entered");
		}
		MessagingNode mNode = null;
		System.out.println("Messaging Node is being created on port " + args[2] + " .......");
		try {
			mNode = new MessagingNode(Integer.parseInt(args[0]));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.serverThread = new TCPServerThread(getPort(), mNode);
		
		
	}
	
	@Override
	public String toString() {
		return "Node:" + this.nodeID + " : " + this.serverThread.getServerSocket().toString();
	}
}
