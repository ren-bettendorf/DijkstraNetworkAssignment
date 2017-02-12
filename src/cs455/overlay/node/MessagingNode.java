package cs455.overlay.node;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import cs455.overlay.dijkstra.Edge;
import cs455.overlay.dijkstra.Graph;
import cs455.overlay.dijkstra.ShortestPath;
import cs455.overlay.dijkstra.Vertex;
import cs455.overlay.transport.TCPReceiverThread;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.*;

public class MessagingNode implements Node {
	private int port;
	private Socket registrySocket;
	private TCPSender registrySender;
	private TCPReceiverThread receiverThread;
	private TCPServerThread serverThread;
	private Thread thread;
	private HashMap<String, TCPSender> messageNodeConnections;
	private ShortestPath path;

	public MessagingNode() throws IOException {
		messageNodeConnections = new HashMap<String, TCPSender>();
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
		while (!userInput.equals("exit-overlay")) {
			userInput = keyboard.nextLine();

			if (userInput.equals("exit-overlay")) {

			} else if (userInput.equals("deregister")) {
				try {
					mNode.deregister(registryHost, registryPort);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (userInput.equals("register")) {
				try {
					mNode.register(registryHost, registryPort);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (userInput.equals("print-shortest-path")) {
				mNode.printShortestPath();
			}
		}
		keyboard.close();
	}

	private void printShortestPath() {
		System.out.println(this.path.getFullPathWeights());
	}

	@Override
	public synchronized void onEvent(Event event) throws IOException {
		int eventType = event.getType();
		switch (eventType) {
		case Protocols.REGISTER_RESPONSE:
			RegistrationResponse response = (RegistrationResponse) event;
			System.out.println(response.getResponse());
			break;
		case Protocols.DEREGISTER_RESPONSE:
			DeregisterResponse deregister = (DeregisterResponse) event;
			System.out.println(deregister.getResponse());
			deregisterResponse(deregister);
			break;
		case Protocols.MESSAGING_NODES_LIST:
			MessagingNodesList nodesList = (MessagingNodesList) event;
			System.out.println("Received MessagingNodesList");
			setMessagingNodesList(nodesList);
			break;
		case Protocols.LINK_WEIGHTS:
			LinkWeights linkWeights = (LinkWeights) event;
			System.out.println("Received LinkWeights");
			ArrayList<Edge> edges = setLinkWeights(linkWeights);
			Graph graph = new Graph(edges);
			this.path = new ShortestPath(graph, new Vertex(InetAddress.getLocalHost().getHostAddress()+":"+getPort(), 4));
			break;
		}
	}

	private void setMessagingNodesList(MessagingNodesList nodeList) {
		String[] list = nodeList.getConnectList().split("\n");
		for (int index = 0; index < nodeList.getNumberNodes(); index++) {
			try {
				String[] splitInfo = list[index].split(":");
				Socket socket = new Socket(splitInfo[0], Integer.parseInt(splitInfo[1]));

				TCPSender sender = new TCPSender(socket);
				messageNodeConnections.put(list[index], sender);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		System.out.println("All connections are established. Number of connections: " + nodeList.getNumberNodes());
	}

	private ArrayList<Edge> setLinkWeights(LinkWeights linkWeights) {
		int numberLinks = linkWeights.getNumberLinks();
		System.out.println("Number of Edges: " + numberLinks);
		ArrayList<Edge> returnEdges = new ArrayList<Edge>();
		String[] edges = linkWeights.getListWeights().split("\n");
		for (int i = 0; i < edges.length; i++) {
			String[] edgeSplit = edges[i].split(" ");
			Edge edge = new Edge(new Vertex(edgeSplit[0], 4), new Vertex(edgeSplit[1], 4));
			edge.setWeight(Integer.parseInt(edgeSplit[2]));
			returnEdges.add(edge);
			
			System.out.println("Added to Edge List: " + edge.toString());
		}
		return returnEdges;
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

		System.out.println("Sending registration request...");
		registrySocket = new Socket(registryHost, registryPort);
		registrySender = new TCPSender(registrySocket);
		receiverThread = new TCPReceiverThread(this, registrySocket);
		Thread tcpReceiverThread = new Thread(this.receiverThread);
		tcpReceiverThread.start();
		registrySender.sendData(registrationRequest.getBytes());
	}

	public void deregister(String registryHost, int registryPort) throws IOException {
		System.out.println("Creating deregistration request...");
		DeregisterRequest deregisterRequest = new DeregisterRequest(InetAddress.getLocalHost().getHostAddress(),
				getPort());

		System.out.println("Sending deregistration request....");
		registrySender.sendData(deregisterRequest.getBytes());
	}

	public void deregisterResponse(DeregisterResponse response) throws IOException {
		if (response.getResult() == 1) {
			registrySocket.close();
		}
	}
	
	public void close() {
		this.serverThread.endThread();

	}
	

	public int getPort() {
		return this.port;
	}

	public void setPort(int port) {
		this.port = port;
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
