package cs455.overlay.node;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

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
	private String ID, host;
	private Socket registrySocket;
	private TCPSender registrySender;
	private TCPReceiverThread receiverThread;
	private TCPServerThread serverThread;
	private Thread thread;
	private HashMap<String, TCPSender> messageNodeConnections;
	private ShortestPath path;
	private AtomicLong sumReceived, sumSent;
	private AtomicInteger messageReceived, messageSent, messageRelayed;

	public MessagingNode() throws IOException {
		this.host = InetAddress.getLocalHost().getHostAddress();
		messageNodeConnections = new HashMap<String, TCPSender>();
		sumReceived = new AtomicLong(0);
		sumSent = new AtomicLong(0);
		messageReceived = new AtomicInteger(0);
		messageSent = new AtomicInteger(0);
		messageRelayed = new AtomicInteger(0);
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
			if (userInput.equals("deregister")) {
				try {
					mNode.deregister(registryHost, registryPort);
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
			this.path = new ShortestPath(graph, new Vertex(this.ID, 4));
			break;
		case Protocols.RELAY_CONNECTION:
			RelayConnection relayConnection = (RelayConnection) event;
			messageNodeConnections.put(relayConnection.getConnection(), new TCPSender(relayConnection.getSocket()));
			break;
		case Protocols.TASK_INITIATE:
			TaskInitiate task = (TaskInitiate) event;
			startRounds(task.getRoundNumber());
			sendTaskCompleteMessage();
			break;
		case Protocols.RELAY_MESSAGE:
			consumeMessage((RelayMessage) event);
			break;
		case Protocols.TASK_SUMMARY_REQUEST:
			sendTaskSummaryResponse();
			break;
		}
	}

	private void sendTaskSummaryResponse() {
		TaskSummaryResponse task = new TaskSummaryResponse(this.host, getPort(), messageSent.get(), sumSent.get(),
				messageReceived.get(), sumReceived.get(), messageRelayed.get());
		System.out.println("Sending task summary response");
		try {
			registrySender.sendData(task.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		resetTrackers();
	}

	private void resetTrackers() {
		sumReceived.set(0);
		sumSent.set(0);
		messageReceived.set(0);
		messageSent.set(0);
		messageRelayed.set(0);
	}

	private void sendTaskCompleteMessage() {
		TaskComplete task = new TaskComplete(this.host, getPort());
		try {
			registrySender.sendData(task.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void consumeMessage(RelayMessage message) {
		String[] relayPaths = message.getConnections().split(" ");
		if ( !relayPaths[relayPaths.length-1].equals(this.ID) ) {
			String connections = "";
			for (int i = 1; i < relayPaths.length; i++) {
				connections += relayPaths[i] + " ";
			}
			RelayMessage relay = new RelayMessage(message.getData(), connections);
			messageRelayed.incrementAndGet();
			try {
				messageNodeConnections.get(relayPaths[1]).sendData(relay.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			messageReceived.incrementAndGet();
			sumReceived.addAndGet(message.getData());
		}
	}

	private void startRounds(int roundNumber) {
		for (int round = 0; round < roundNumber; round++) {
			if(round % (roundNumber/5) == 0) {
				System.out.println("[STATUS] On round " + round);
			}
			ArrayList<String> nodesToMessage = new ArrayList<String>();

			String node = pickRandomNode();
			String nodePath = path.getCachedRoute(new Vertex(node, 4));

			String firstNode = nodePath.split(" ")[0];
			for (int nodes = 0; nodes < 5; nodes++) {
				
				Random random = new Random();
				int data = random.nextInt(2147483647);
				if (random.nextDouble() < 0.5) {
					data *= -1;
				}
				sumSent.getAndAdd(data);
				
				RelayMessage message = new RelayMessage(data, nodePath);
				messageSent.incrementAndGet();
				try {
					messageNodeConnections.get(firstNode).sendData(message.getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private String pickRandomNode() {
		ArrayList<String> nodes = new ArrayList<String>(path.getOtherVertices());
		return (String) nodes.get(new Random().nextInt(nodes.size()));
	}

	private void setMessagingNodesList(MessagingNodesList nodeList) {
		String[] list = nodeList.getConnectList().split("\n");
		for (int index = 0; index < nodeList.getNumberNodes(); index++) {
			try {
				String[] splitInfo = list[index].split(":");
				Socket socket = new Socket(splitInfo[0], Integer.parseInt(splitInfo[1]));

				TCPSender sender = new TCPSender(socket);
				RelayConnection connection = new RelayConnection(this.ID);
				TCPReceiverThread thread = new TCPReceiverThread(this, socket);
				new Thread(thread).start();
				sender.sendData(connection.getBytes());
				messageNodeConnections.put(list[index], sender);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		System.out.println("All connections are established. Number of connections: " + nodeList.getNumberNodes());
		
	}

	private ArrayList<Edge> setLinkWeights(LinkWeights linkWeights) {
		ArrayList<Edge> returnEdges = new ArrayList<Edge>();
		String[] edges = linkWeights.getListWeights().split("\n");
		for (int i = 0; i < edges.length; i++) {
			String[] edgeSplit = edges[i].split(" ");
			Edge edge = new Edge(new Vertex(edgeSplit[0], 4), new Vertex(edgeSplit[1], 4));
			edge.setWeight(Integer.parseInt(edgeSplit[2]));
			returnEdges.add(edge);
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
		this.ID = this.host + ":" + getPort();
		RegistrationRequest registrationRequest = new RegistrationRequest(this.host, getPort());

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
		DeregisterRequest deregisterRequest = new DeregisterRequest(this.host, getPort());

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
			return this.host + ":" + this.serverThread.getPort();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
}
