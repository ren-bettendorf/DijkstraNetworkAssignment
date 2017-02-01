package cs455.overlay.node;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.DeregisterRequest;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.Protocols;
import cs455.overlay.wireformats.RegistrationRequest;
import cs455.overlay.wireformats.RegistrationResponse;

public class Registry implements Node {

	private int port;
	private ArrayList<String> registeredNodes = new ArrayList<String>();
	private TCPServerThread serverThread;
	private Thread thread;

	public Registry(int port) {
		this.port = port;
	}

	public void startServerThread(TCPServerThread serverThread) {
		this.serverThread = serverThread;
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
			registry.startServerThread(new TCPServerThread(registry.getPort(), registry));
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			System.exit(1);
		}

		Scanner keyboard = new Scanner(System.in);
		System.out.println("Keyboard scanner ready for commands...");
		String userInput = "";
		while(!userInput.equals("close")) {
			userInput = keyboard.nextLine();
			if(userInput.equals("close")) {
				registry.close();
			}
		}
		keyboard.close();
	}

	@Override
	public void onEvent(Event event) throws IOException {
		// TODO Auto-generated method stub
		int eventType = event.getType();
		switch (eventType) {
		case Protocols.REGISTER_REQUEST:
			RegistrationRequest request = (RegistrationRequest) event;
			registerNode(request);
			break;
		case Protocols.DEREGISTER_REQUEST:
			DeregisterRequest derequest = (DeregisterRequest) event;
			deregisterNode(derequest);
			break;
			
			
		}

	}

	private void deregisterNode(DeregisterRequest derequest) {		
		String node = derequest.getHostname() + ":" + derequest.getPort();
		String nodeSocket = derequest.getSocketAddress();
		System.out.println("Received a deregistration request from: " + node);
		if(node.equals(nodeSocket) && registeredNodes.contains(node)) {
			registeredNodes.remove(node);
		}
		
	}

	private void registerNode(RegistrationRequest request) throws UnknownHostException, IOException {
		String nodeHostPort = request.getHostname() + ":" + request.getPort();
		System.out.println("Received a registration request from: " + nodeHostPort);
		byte regResult = 1;
		String response = "";
		if(!registeredNodes.contains(nodeHostPort)) {
			registeredNodes.add(nodeHostPort);
			response = "Registration request successful. The number of messaging nodes currently constituting the overlay is (" + registeredNodes.size() + ")";
			
		}else {
			regResult = -1;
			response = "Registration request unsuccessful. The number of messaging nodes currently constituting the overlay is (" + registeredNodes.size() + ")";
		}
		
		System.out.println(response);
		RegistrationResponse registrationResponse = new RegistrationResponse(regResult, response);
		System.out.println("Creating registration response...");

		Socket socket = new Socket(request.getHostname(),  request.getPort());
		TCPConnection connection = new TCPConnection(this, socket);

		System.out.println("Sending registration report...");
		connection.sendData(registrationResponse.getBytes());
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
	
	public void close() {
		this.serverThread.endThread();
		
	}
}
