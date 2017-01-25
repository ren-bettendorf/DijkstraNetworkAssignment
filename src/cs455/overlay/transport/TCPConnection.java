package cs455.overlay.transport;

import java.io.IOException;
import java.net.Socket;

import cs455.overlay.node.Node;

public class TCPConnection {
	private TCPSender tcpSender;
	private TCPReceiverThread tcpReceiverThread;
	private Thread receiverThread;
	private Node node;
	private Socket socket;
	
	public TCPConnection(Node node, Socket socket) throws IOException {
		this.node = node;
		this.socket = socket;
		this.tcpSender = new TCPSender(socket);
		this.tcpReceiverThread = new TCPReceiverThread(node, socket);
		this.receiverThread = new Thread(this.tcpReceiverThread);
		this.receiverThread.start();
	}
	
	public void sendData(byte[] dataToSend) throws IOException {
		this.tcpSender.sendData(dataToSend);
	}
	
	public void closeConnections() {
		
	}
}
