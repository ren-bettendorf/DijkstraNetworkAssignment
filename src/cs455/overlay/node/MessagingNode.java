package cs455.overlay.node;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import cs455.overlay.transport.TCPSender;
import cs455.overlay.wireformats.Event;

public class MessagingNode implements Node {
	private int port;
	private ServerSocket serverSocket;
	
	public MessagingNode(int port) throws IOException
	{
		this.port = port;
		this.serverSocket = new ServerSocket(port);
	}
	
	public void register()
	{
		
	}
	
	public Socket acceptConnection() throws IOException {
		return serverSocket.accept();
	}

	@Override
	public void onEvent(Event event) throws IOException {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args) {
		byte[] data = (new String("registered on " + args[0])).getBytes();
		if(args.length != 1) {
			System.out.println("Sorry but you need a host and port number entered");
		}
		MessagingNode mNode = null;
		System.out.println("Messaging Node is being created on port " + args[0] + " .......");
		try {
			mNode = new MessagingNode(Integer.parseInt(args[0]));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(true) {
			Socket socket = null;
			TCPSender tcpSender = null;
			try {
				socket = mNode.acceptConnection();
				tcpSender = new TCPSender(socket);
				tcpSender.sendData(data);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
