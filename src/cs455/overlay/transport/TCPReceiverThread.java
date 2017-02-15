package cs455.overlay.transport;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import cs455.overlay.node.Node;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;

public class TCPReceiverThread implements Runnable {

	private Node node;
	private Socket socket;
	private DataInputStream din;
	
	/**
	* Creates TCPReceiverThread object for all incoming connections for a given node and socket
	*
	* @param node Node to be associated with object
	* @param socket Socket to be associated with object
	*/
	public TCPReceiverThread(Node node, Socket socket) throws IOException {
		this.node = node;
		this.socket = socket;
		din = new DataInputStream(socket.getInputStream());
	}
	
	/**
	* Starts when Thread is executed
	*/
	@Override
	public void run() {
		int dataLength;
		while(socket != null) {
			try {
				dataLength = din.readInt();
				
				byte[] data = new byte[dataLength];
				din.readFully(data, 0, dataLength);
				
				Event event = EventFactory.getInstance().getEvent(data, socket);
				
				node.onEvent(event);
			} catch(SocketException se) {
				System.out.println(se.getMessage());
				break;
			} catch(IOException ioe) {
				System.out.println(ioe.getMessage());
				break;
			}
			
		}
 		try {
			teardown();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	* Teardown method for the TCPReceiverThread
	*/
	private void teardown() throws IOException {
		din.close();
		this.socket.close();
	}
}
