package cs455.overlay.node;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.*;

public class MessagingNode implements Node {
	private int port, nodeID;
	private TCPServerThread serverThread;
	private Thread thread;

	public MessagingNode() throws IOException {
	}

	public void setServerThread(TCPServerThread serverThread) {
		this.serverThread = serverThread;
		setPort(serverThread.getPort());
	}
	
	public void startServerThread() {
		this.thread = new Thread(this.serverThread);
		this.thread.start();
	}

	public void register(String registryHost, int registryPort) throws IOException {
		System.out.println("Creating registration request...");
		SendRegistrationRequest registrationRequest = new SendRegistrationRequest(InetAddress.getLocalHost().getHostAddress(), getPort());
		
		byte[] data = registrationRequest.getBytes();
		System.out.println("Sending registration request...");
		Socket socket = new Socket(registryHost, registryPort);
		TCPConnection connection = new TCPConnection(this, socket);
	}

	public void setID(int ID) {
		this.nodeID = ID;
	}

	public int getPort() {
		return this.port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}

	// java cs455.overlay.node.MessagingNode registry_host registry_port
	public static void main(String[] args) {
		String registryHost = null;
		int registryPort = -1;
		if(args.length != 2) {
			System.out.println("arg0 = registry_host and arg1=registry_port");
			System.exit(1);
		} else {
			registryHost = args[0];
			registryPort = Integer.parseInt(args[1]);
		}
		
		MessagingNode mNode = null;
		try {
			mNode = new MessagingNode();
			mNode.setID(10);

			// Set portnumber to 0 so ServerSocket picks one
			mNode.setServerThread(new TCPServerThread(0, mNode));
			mNode.startServerThread();
			
			mNode.register(registryHost, registryPort);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		try {
			return InetAddress.getLocalHost().getHostAddress() + ":" + this.serverThread.getPort();
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
