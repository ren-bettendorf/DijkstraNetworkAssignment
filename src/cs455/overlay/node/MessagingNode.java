package cs455.overlay.node;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.*;

public class MessagingNode implements Node {
	private int port, nodeID;
	private TCPServerThread serverThread;
	private Thread thread;
	private TCPConnection connection;

	public MessagingNode() throws IOException {
	}

	private void startServerThread(TCPServerThread serverThread) {
		this.serverThread = serverThread;
		setPort(serverThread.getPort());
		this.thread = new Thread(this.serverThread);
		this.thread.start();
	}

	public void register(String registryHost, int registryPort) throws IOException {
		System.out.println("Creating registration request...");
		RegistrationRequest registrationRequest = new RegistrationRequest(InetAddress.getLocalHost().getHostAddress(),
				getPort());

		byte[] data = registrationRequest.getBytes();
		System.out.println("Sending registration request...");
		Socket socket = new Socket(registryHost, registryPort);
		this.connection = new TCPConnection(this, socket);
		connection.sendData(data);
	}
	
	public void deregister(String registryHost, int registryPort) throws IOException {
		System.out.println("Creating registration request...");
		DeregisterRequest deregisterRequest = new DeregisterRequest(InetAddress.getLocalHost().getHostAddress(),
				getPort());

		byte[] data = deregisterRequest.getBytes();
		System.out.println("Sending registration request...");
		Socket socket = new Socket(registryHost, registryPort);
		this.connection = new TCPConnection(this, socket);
		connection.sendData(data);
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
		if (args.length != 2) {
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
			mNode.startServerThread(new TCPServerThread(0, mNode));

			mNode.register(registryHost, registryPort);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Scanner keyboard = new Scanner(System.in);
		System.out.println("Keyboard scanner ready for commands...");
		String userInput = "";
		while(!userInput.equals("exit-overlay")) {
			userInput = keyboard.nextLine();
			System.out.println("User input: " + userInput);
			if(userInput.equals("exit-overlay")) {
				this.connection.
			} else if (userInput.equals("deregister")) {
				deregister(registryHost, registryPort);
			}
		}
	}

	@Override
	public String toString() {
		try {
			return InetAddress.getLocalHost().getHostAddress() + ":" + this.serverThread.getPort();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	@Override
	public void onEvent(Event event) throws IOException {
		// TODO Auto-generated method stub
		int eventType = event.getType();
		switch (eventType) {
		case Protocols.REGISTER_RESPONSE:
			System.out.println("REGISTERED");
			break;
		}
	}
}
