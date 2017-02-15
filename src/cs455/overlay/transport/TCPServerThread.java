package cs455.overlay.transport;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import cs455.overlay.node.Node;

public class TCPServerThread implements Runnable {
	
	private int port;
	private ServerSocket serverSocket;
	private Node node;
	private boolean runStatus;
	
	/**
	* Creates TCPServerThread object at a given port for a given node for all incoming connections
	*
	* @param dataToSend Marshalled data to be sent
	*/
	public TCPServerThread(int port, Node node) throws IOException {
		this.node = node;
		try {
			this.serverSocket = new ServerSocket(port);
			this.port = this.serverSocket.getLocalPort();
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}
	
	/**
	* Starts when Thread is executed
	*/
	@Override
	public void run() {
		System.out.println("Starting thread for node: " + node.toString() + ". Listening on port: " + getPort());
		this.runStatus = true;
		while(runStatus) {
			try {
				Socket socket = serverSocket.accept();
				
				TCPReceiverThread tcpReceiverThread = new TCPReceiverThread(node, socket);
				new Thread(tcpReceiverThread).start();
			} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			break;
			}
		}
		try {
			teardown();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Ending thread for node: " + node.toString() + ". Closing on port: " + getPort());
	}
	
	/**
	* Getter for port being used
	*
	* @return Port being used
	*/
	public int getPort() {
		return this.port;
	}
	
	/**
	* Getter for ServerSocket
	*
	* @return ServerSocket being used
	*/
	public ServerSocket getServerSocket() {
		return this.serverSocket;
	}
	
	/**
	* Ends the thread
	*/
	public void endThread() {
		this.runStatus = false;
	}
	
	/**
	* Teardown method for the TCPServerThread
	*/
	private void teardown() throws IOException {
		this.serverSocket.close();
		
	}
}
