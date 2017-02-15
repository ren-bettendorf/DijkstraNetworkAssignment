package cs455.overlay.transport;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPSender {
	private Socket socket;
	private DataOutputStream dout;
	
	/**
	* Creates the TCPSender object for a given Socket
	*
	* @param socket Socket to be used for connection
	*/
	public TCPSender(Socket socket) throws IOException {
		this.socket = socket;
		dout = new DataOutputStream(socket.getOutputStream());
	}
	
	/**
	* Sends marshalled byte[] to the other end of the socket
	*
	* @param dataToSend Marshalled data to be sent
	*/
	public synchronized void sendData(byte[] dataToSend) throws IOException {
		int dataLength = dataToSend.length;
		
		dout.writeInt(dataLength);
		dout.write(dataToSend, 0, dataLength);
		dout.flush();
	}
	
	/**
	* Getter for the socket being used
	*
	* @return Socket being used
	*/
	public Socket getSocket() {
		return this.socket;
	}
}
