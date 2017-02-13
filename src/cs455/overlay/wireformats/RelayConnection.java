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
	
	public RelayConnection(String connection) {
		this.type = Protocols.RELAY_CONNECTION;
		this.connection = connection;
	}
	
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

	@Override
	public int getType() {
		return this.type;
	}

	public Socket getSocket() {
		return this.socket;
	}

	public String getConnection() {
		return this.connection;
	}
}
