package cs455.overlay.node;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import cs455.overlay.dijkstra.Edge;
import cs455.overlay.dijkstra.Graph;
import cs455.overlay.dijkstra.Vertex;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.DeregisterRequest;
import cs455.overlay.wireformats.DeregisterResponse;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.LinkWeights;
import cs455.overlay.wireformats.MessagingNodesList;
import cs455.overlay.wireformats.Protocols;
import cs455.overlay.wireformats.RegistrationRequest;
import cs455.overlay.wireformats.RegistrationResponse;
import cs455.overlay.wireformats.TaskComplete;
import cs455.overlay.wireformats.TaskInitiate;
import cs455.overlay.wireformats.TaskSummaryRequest;
import cs455.overlay.wireformats.TaskSummaryResponse;

public class Registry implements Node {

	private int port;
	private String host;
	private HashMap<String, TCPSender> messageNodeConnections = new HashMap<String, TCPSender>();
	private TCPServerThread serverThread;
	private Thread thread;
	private Graph graph;
	private ArrayList<String> tasksComplete;
	private String displayResults;
	private AtomicLong sumSumSent, sumSumReceived;
	private AtomicInteger sumMessageSent, sumMessageReceived;
	private boolean overlayMessageStatus = false;

	/**
	* Creates the Registry object at a given port
	*
	* @param port Port to defined connections
	*/
	public Registry(int port) {
		sumSumSent = new AtomicLong(0);
		sumSumReceived = new AtomicLong(0);
		sumMessageSent = new AtomicInteger(0);
		sumMessageReceived = new AtomicInteger(0);

		try {
			this.host = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.port = port;
		this.graph = new Graph();
	}

	/**
	* Creates the Registry object at a given port
	* java cs455.overlay.node.Registry local_port
	*
	*/
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
		while (!userInput.equals("close")) {
			userInput = keyboard.nextLine();
			if (userInput.contains("setup-overlay")) {
				if (registry.getNodesConnectedSize() >= 10) {
					int connectionsRequired = 0;
					if (userInput.equals("setup-overlay")) {
						connectionsRequired = 4;
					} else {
						try {
							connectionsRequired = Integer.parseInt(userInput.split(" ")[1]);

						} catch (NumberFormatException nfe) {
							System.out.println("Sorry please try again with 'setup-overlay connections-required'");
						}
					}
					if (connectionsRequired > 1 && connectionsRequired < registry.getNodesConnectedSize()) {
						System.out.println("Creating overlay setup for size " + connectionsRequired + "...");
						registry.setupOverlay(connectionsRequired);
						registry.sendConnectionList();
					} else {
						System.out.println("Couldn't create overlay with size > 1 and < "
								+ registry.getNodesConnectedSize());
					}
				} else {
					System.out.println(
							"Can't create overlay due to insufficient nodes: " + registry.getNodesConnectedSize());
				}
			} else if (userInput.equals("send-overlay-link-weights")) {
				registry.assignWeights();
				registry.sendLinkWeights();
				registry.setOverlayStatus(true);
			} else if (userInput.equals("list-messaging-nodes")) {
				registry.listNodes();
			} else if (userInput.equals("list-weights")) {
				registry.listWeights();
			} else if (userInput.contains("start")) {
				if (registry.getOverlayMessageStatus()) {
					int rounds = 0;
					try {
						rounds = Integer.parseInt(userInput.split(" ")[1]);
						System.out.println("Starting " + rounds + " rounds");
					} catch (NumberFormatException nfe) {
						System.out.println("Sorry please try again with 'start number-of-rounds'");
					}
					registry.startTasks(rounds);
				} else {
					System.out.println(
							"Messaging Nodes have not been informed of all connections and edge weights. Please setup-overlay <number-of-connection> and then send-overlay-link-weights.");
				}
			}
		}
		keyboard.close();
	}

	
	private void listWeights() {
		if(overlayMessageStatus) {
		for(Edge edge : graph.getEdges()) {
			System.out.println(edge.toString());
		}
		}else {
			System.out.println("Edge weights haven't been setup yet. Please 'send-overlay-link-weights' first");
		}
	}
	/**
	* Starts a task for a given amount of rounds for all nodes
	*
	* @param rounds Number of rounds to run
	*/
	private void startTasks(int rounds) {
		if(rounds > 0) {
			tasksComplete = new ArrayList<String>(messageNodeConnections.keySet());
			displayResults = "Node ID\t\t\tMessage Sent\tMessage Received\tSum Sent Messages\tSum Received Messages\tMessage Relayed\n";
			TaskInitiate task = new TaskInitiate(rounds);
			for (TCPSender sender : messageNodeConnections.values()) {
				try {
					sender.sendData(task.getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			System.out.println("Sorry but the rounds must be greater than 0");
		}
	}

	/**
	* Sends a connection list for each messaging node for what connections they need to make
	*/
	private void sendConnectionList() {
		HashMap<Vertex, ArrayList<Vertex>> connections = this.graph.getConnectionsHashMap();
		for (Entry<Vertex, ArrayList<Vertex>> entry : connections.entrySet()) {
			String nodesList = "";
			for (Vertex vertex : entry.getValue()) {
				nodesList += vertex.toString() + "\n";
			}
			MessagingNodesList list = new MessagingNodesList(entry.getValue().size(), nodesList);
			try {
				messageNodeConnections.get(entry.getKey().getID()).sendData(list.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	* Prints the nodes connected
	*/
	public void listNodes() {
		for (String s : messageNodeConnections.keySet()) {
			System.out.println(s);
		}
	}

	/**
	* Sends the edge information to the messaging nodes
	*/
	public void sendLinkWeights() {
		LinkWeights linkWeights = new LinkWeights(this.graph.getEdges().size(), this.graph.marshallEdgeString());
		for (TCPSender sender : messageNodeConnections.values()) {
			try {
				sender.sendData(linkWeights.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	* Assigns weights to the edges
	*/
	public void assignWeights() {
		this.graph.assignWeights();
	}

	/**
	* Handles the event that was seen at the TCPReceiverThread
	*
	* @param event Event to handle
	*/
	@Override
	public synchronized void onEvent(Event event) throws IOException {
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
		case Protocols.TASK_COMPLETE:
			handleTaskComplete((TaskComplete) event);
			break;
		case Protocols.TASK_SUMMARY_RESPONSE:
			handleTaskSummaryResponse((TaskSummaryResponse) event);
			break;
		}

	}

	/**
	* Creates the task summary responses final message
	*
	* @param task Task to have info unmarshalled
	*/
	private void handleTaskSummaryResponse(TaskSummaryResponse task) {
		tasksComplete.remove(task.getNodeID());
		displayResults += task.getNodeID() + "\t" + task.getMessageSent() + "\t\t" + task.getMessageReceived()
				+ "\t\t\t" + task.getSumSent() + "\t\t" + task.getSumReceived() + "\t\t" + task.getMessageRelayed();
		sumSumSent.addAndGet(task.getSumSent());
		sumSumReceived.addAndGet(task.getSumReceived());
		sumMessageSent.addAndGet(task.getMessageSent());
		sumMessageReceived.addAndGet(task.getMessageReceived());
		if (!tasksComplete.isEmpty()) {
			displayResults += "\n";
		} else {
			System.out.println("Summaries have been collected.");

			System.out.println(displayResults);
			System.out.println("\t\tSUM\t" + sumMessageSent + "\t\t" + sumMessageReceived + "\t\t\t" + sumSumSent.get()
					+ "\t\t" + sumSumReceived.get());

			resetTrackers();

		}
	}

	/**
	* Handles task complete message
	*
	* @param task Rounds have been completed from messaging node
	*/
	private void handleTaskComplete(TaskComplete task) {
		tasksComplete.remove(task.getNodeID());
		if (tasksComplete.isEmpty()) {

			System.out.println("Tasks have completed. Waiting for messages to finish relay");
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			pullTrafficSummary();
		}
	}

	/**
	* Creates the TaskSummaryRequest to be sent to all nodes
	*/
	private void pullTrafficSummary() {
		TaskSummaryRequest request = new TaskSummaryRequest();
		tasksComplete = new ArrayList<String>(messageNodeConnections.keySet());
		for (TCPSender sender : messageNodeConnections.values()) {
			try {
				sender.sendData(request.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	* Attempts to deregister a given node
	*
	* @param derequest Deregister request from a node
	*/
	private void deregisterNode(DeregisterRequest derequest) {
		String node = derequest.getFullHost();
		Socket nodeSocket = derequest.getSocket();
		System.out.println("Received a deregistration request from: " + node);
		String response = "";
		byte result = 1;
		if (!this.graph.getOverlayStatus()) {
			if (messageNodeConnections.containsKey(node)) {
				Socket socket = messageNodeConnections.get(node).getSocket();
				TCPSender sender = messageNodeConnections.get(node);
				if (nodeSocket.equals(socket)) {
					messageNodeConnections.remove(node);
					response = "Deregistration request successful. The number of messaging nodes currently constituting the overlay is ("
							+ messageNodeConnections.size() + ")";
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
			} else {
				result = 0;
				response = "Deregistration request unsuccessful. Node isn't registered already";
			}
		} else {
			response = "Overlay has been setup so no nodes can register or deregister";
		}
		System.out.println(response);

	}

	/**
	* Attempts to register a node
	*
	* @param request Register request data to be unmarshalled
	*/
	private void registerNode(RegistrationRequest request) throws UnknownHostException, IOException {
		String nodeHostPort = request.getHostname() + ":" + request.getPort();
		System.out.println("Received a registration request from: " + nodeHostPort);
		byte regResult = 1;
		String response = "";

		Socket socket = request.getSocket();
		TCPSender sender = new TCPSender(socket);
		if (!this.graph.getOverlayStatus()) {
			if (!messageNodeConnections.containsKey(nodeHostPort)) {
				messageNodeConnections.put(nodeHostPort, sender);
				response = "Registration request successful. The number of messaging nodes currently constituting the overlay is ("
						+ messageNodeConnections.size() + ")";
			} else {
				regResult = 0;
				response = "Registration request unsuccessful. Node already in overlay";
			}
		} else {
			regResult = 0;
			response = "Registration request unsuccessful. Overlay already setup";
		}

		System.out.println(response);
		System.out.println("Creating registration response...");
		RegistrationResponse registrationResponse = new RegistrationResponse(regResult, response);

		System.out.println("Sending registration report...");
		sender.sendData(registrationResponse.getBytes());
	}

	/**
	* Creates the Overlay for the nodes
	*
	* @param connectionsRequired Number of connections required
	*/
	private void setupOverlay(int connectionsRequired) {
		// Add a vertex for each node in node map
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		for (String node : messageNodeConnections.keySet()) {
			vertices.add(new Vertex(node, connectionsRequired));
		}
		graph.setupOverlay(vertices, connectionsRequired);
		System.out.println("Overlay setup");
	}

	/**
	* Starts TCPServerThread for registry
	*
	* @param serverThread Starts this thread
	*/
	public void startServerThread(TCPServerThread serverThread) {
		this.serverThread = serverThread;
		this.thread = new Thread(this.serverThread);
		this.thread.start();
	}

	/**
	* Resets the trackers to 0
	*/
	private void resetTrackers() {
		sumSumSent.set(0);
		sumSumReceived.set(0);
		sumMessageSent.set(0);
		sumMessageReceived.set(0);
	}

	/**
	* Getter for the overlayMessageStatus
	*
	* @return overlay message status
	*/
	private boolean getOverlayMessageStatus() {
		return overlayMessageStatus;
	}

	/**
	* Sets overlayMessageStatus to a given status
	*
	* @param status Status to be set to
	*/
	private void setOverlayStatus(boolean status) {
		overlayMessageStatus = status;
	}

	/**
	* Getter for the number of nodes connected
	*
	* @return Number of nodes connected
	*/
	public int getNodesConnectedSize() {
		return this.messageNodeConnections.size();
	}

	/**
	* Getter for the port used for connections
	*
	* @return Registry port used for connections
	*/
	public int getPort() {
		return this.port;
	}

	/**
	* Creates the String version of the Registry
	*
	* @return String versino of registry
	*/
	@Override
	public String toString() {
		return this.host;
	}
}
