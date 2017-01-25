package cs455.overlay.node;

import java.net.InetAddress;
import java.net.Socket;
import java.io.IOException;
import java.util.ArrayList;

import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.Protocols;
import cs455.overlay.wireformats.RegistrationRequest;
import cs455.overlay.wireformats.RegistrationResponse;

public class Registry implements Node {

	private int port;
	private ArrayList<Node> registeredNodes = new ArrayList<Node>();
	private TCPServerThread serverThread;
	private Thread thread;
	private int nodesConnect = 0;

	public Registry(int port) {
		this.port = port;
	}

	public void registerNode(Node node) {
		if (!registeredNodes.contains(node)) {
			registeredNodes.add(node);
		}
	}

	public void deregisterNode(Node node) {
		if (registeredNodes.contains(node)) {
			registeredNodes.remove(node);
		}
	}

	public void setServerThread(TCPServerThread serverThread) {
		this.serverThread = serverThread;
	}

	public void startServerThread() {
		this.thread = new Thread(this.serverThread);
		this.thread.start();
	}

	public int getPort() {
		return this.port;
	}

	// java cs455.overlay.node.Registry local_port
	public static void main(String[] args) {
		Registry registry = null;
		if (args.length == 1) {
			registry = new Registry(Integer.parseInt(args[0]));
		}
		try {
			registry.setServerThread(new TCPServerThread(registry.getPort(), registry));
			registry.startServerThread();
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			System.exit(1);
		}

	}

	@Override
	public void onEvent(Event event) throws IOException {
		// TODO Auto-generated method stub
		int eventType = event.getType();
		switch (eventType) {
		case Protocols.REGISTER_REQUEST:
			RegistrationRequest request = (RegistrationRequest) event;
			System.out.println("Received a request from: " + request.getHostname() + ":" + request.getPort());
			nodesConnect++;
			
			byte regResult = 1;
			String response = "Registration request successful. The number of messaging nodes currently constituting the overlay is (" + nodesConnect + ")";
			System.out.println(response);
			RegistrationResponse registrationResponse = new RegistrationResponse(regResult, response);
			System.out.println("Creating registration response...");

			Socket socket = new Socket(request.getHostname(),  request.getPort());
			TCPConnection connection = new TCPConnection(this, socket);

			System.out.println("Sending registration report...");
			connection.sendData(registrationResponse.getBytes());
			break;
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
}
