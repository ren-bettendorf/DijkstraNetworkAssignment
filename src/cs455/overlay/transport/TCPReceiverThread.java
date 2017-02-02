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
	
	public TCPReceiverThread(Node node, Socket socket) throws IOException {
		this.node = node;
		this.socket = socket;
		din = new DataInputStream(socket.getInputStream());
	}
	
	
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
	
	
	private void teardown() throws IOException {
		this.socket.close();
		din.close();
	}
}
