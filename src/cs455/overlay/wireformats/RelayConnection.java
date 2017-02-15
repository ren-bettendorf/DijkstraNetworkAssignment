package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class RelayConnection implements Event {
	private int type;
	private String connection;
	private Socket socket;
	
	/**
	* Creates RelayConnection object for marshalling bytes
	*
	* @param  connection Connection info 
	*/
	public RelayConnection(String connection) {
		this.type = Protocols.RELAY_CONNECTION;
		this.connection = connection;
	}
	
	/**
	* Creates RelayConnection object for unmarshalling bytes
	*
	* @param  marshalledBytes  Marshalled bytes to be unwrapped
	* @param socket Socket that the data came across for creating a new TCPSender
	*/
	public RelayConnection(byte[] marshalledBytes, Socket socket) throws IOException {
		this.socket = socket;
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		type = din.readInt();
		
		int connectionLength = din.readInt();
		byte[] connectionBytes = new byte[connectionLength];
		din.readFully(connectionBytes);
		
		connection= new String(connectionBytes);
		
		baInputStream.close();
		din.close();
	}
	
	/**
	* Returns the marshalled bytes for sending a message
	*
	* @return marshalled bytes
	*/
	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		dout.writeInt(type);
		
		byte[] connectionBytes = connection.getBytes();
		int connectionLength = connectionBytes.length;
		dout.writeInt(connectionLength);
		dout.write(connectionBytes);
		
		dout.flush();
		marshalledBytes = baOutputStream.toByteArray();
		
		baOutputStream.close();
		dout.close();
		
		return marshalledBytes;
	}

	/**
	* Getter function for the socket
	*
	* @return socket
	*/
	public Socket getSocket() {
		return this.socket;
	}

	/**
	* Getter function for the connection
	*
	* @return connection
	*/
	public String getConnection() {
		return this.connection;
	}
	
	/**
	* Getter function for the type
	*
	* @return type
	*/
	@Override
	public int getType() {
		return this.type;
	}
}
