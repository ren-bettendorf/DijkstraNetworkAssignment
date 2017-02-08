package cs455.overlay.node;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import cs455.overlay.dijkstra.Graph;
import cs455.overlay.dijkstra.Vertex;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.DeregisterRequest;
import cs455.overlay.wireformats.DeregisterResponse;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.LinkWeights;
import cs455.overlay.wireformats.Protocols;
import cs455.overlay.wireformats.RegistrationRequest;
import cs455.overlay.wireformats.RegistrationResponse;

public class Registry implements Node {

	private int port;
	private String host;
	private HashMap<String, TCPSender> messageNodeConnections = new HashMap<String, TCPSender>();
	private TCPServerThread serverThread;
	private Thread thread;
	private Graph graph;

	public Registry(int port) {
		try {
			this.host = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.port = port;
	}

	public void startServerThread(TCPServerThread serverThread) {
		this.serverThread = serverThread;
		this.thread = new Thread(this.serverThread);
		this.thread.start();
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
			if(userInput.equals("setup-overlay")) {
				if(registry.getNodesConnectedSize() >= 10) {
					System.out.println("Creating overlay setup...");
					registry.setupOverlay(4);
				}else {
					System.out.println("Can't create overlay due to insufficient nodes: " + registry.getNodesConnectedSize());
				}
			}else if(userInput.equals("assign")) {
				registry.assignWeights();
			}else if(userInput.equals("list-weights")) {
				registry.listNodes();
			}else if(userInput.equals("send-overlay-link-weights")) {
				registry.sendLinkWeights();
			}else if(userInput.equals("list-messaging-nodes")) {
				registry.listNodes();
			}
		}
		keyboard.close();
	}

	public void listNodes() {
		for(String s : messageNodeConnections.keySet()) {
			System.out.println(s);
		}
	}

	public void sendLinkWeights() {
		LinkWeights linkWeights = new LinkWeights(this.graph.getEdges().size(),this.graph.marshallEdgeString());
		for(TCPSender sender : messageNodeConnections.values()) {
			try {
				sender.sendData(linkWeights.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void assignWeights() {
		this.graph.assignWeights();
	}

	@Override
	public synchronized void onEvent(Event event) throws IOException {
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
		String node = derequest.getFullHost();
		Socket nodeSocket = derequest.getSocket();
		System.out.println("Received a deregistration request from: " + node);
		String response = "";
		byte result = 1;
		if(messageNodeConnections.containsKey(node)) {
			Socket socket = messageNodeConnections.get(node).getSocket();
			TCPSender sender = messageNodeConnections.get(node);
			if(nodeSocket.equals(socket)) {
				messageNodeConnections.remove(node);
				response = "Deregistration request successful. The number of messaging nodes currently constituting the overlay is (" + messageNodeConnections.size() + ")";
			} else {
				result = 0;
				response = "Deregistration request unsuccessful. Couldn't verify sender IP matched with cached sender IP";
			}
			
			try {
				sender.sendData((new DeregisterResponse(result, response)).getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			result = 0;
			response = "Deregistration request unsuccessful. Node isn't registered already";
		}
		System.out.println(response);
		
	}

	private synchronized void registerNode(RegistrationRequest request) throws UnknownHostException, IOException {
		String nodeHostPort = request.getHostname() + ":" + request.getPort();
		System.out.println("Received a registration request from: " + nodeHostPort);
		byte regResult = 1;
		String response = "";

		Socket socket = request.getSocket();
		TCPSender sender = new TCPSender(socket);
		if(!messageNodeConnections.containsKey(nodeHostPort)) {
			messageNodeConnections.put(nodeHostPort, sender);
			response = "Registration request successful. The number of messaging nodes currently constituting the overlay is (" + messageNodeConnections.size() + ")";
			
		}else {
			regResult = 0;
			response = "Registration request unsuccessful. The number of messaging nodes currently constituting the overlay is (" + messageNodeConnections.size() + ")";
		}
		
		System.out.println(response);
		RegistrationResponse registrationResponse = new RegistrationResponse(regResult, response);
		System.out.println("Creating registration response...");


		System.out.println("Sending registration report...");
		sender.sendData(registrationResponse.getBytes());
	}
	
	private void setupOverlay(int connectionsRequired) {
		// Add a vertex for each node in node map
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		for(String node : messageNodeConnections.keySet()) {
			vertices.add(new Vertex(node, connectionsRequired));
		}
		
		this.graph = new Graph(vertices);
		graph.setupOverlay(connectionsRequired);
	}
	
	
	public int getNodesConnectedSize() {
		return this.messageNodeConnections.size();
	}

	public int getPort() {
		return this.port;
	}
	
	@Override
	public String toString() {
		return this.host;
	}
}
