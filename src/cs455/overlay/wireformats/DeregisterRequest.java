package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DeregisterRequest implements Event, Protocols {
	private int type;
	private long timestamp;
	private String hostname;
	private int port;
	
	public DeregisterRequest(String hostname, int port) {
		this.type = Protocols.DEREGISTER_REQUEST;
		this.hostname = hostname;
		this.port = port;
	}
	
	public DeregisterRequest(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		type = din.readInt();
		timestamp = din.readLong();
		
		int hostnameLength = din.readInt();
		byte[] hostnameBytes = new byte[hostnameLength];
		din.readFully(hostnameBytes);
		
		hostname = new String(hostnameBytes);
		
		port = din.readInt();
		
		baInputStream.close();
		din.close();
	}
	
	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		dout.writeInt(type);
		dout.writeLong(timestamp);
		
		byte[] hostnameBytes = hostname.getBytes();
		int hostnameLength = hostnameBytes.length;
		dout.writeInt(hostnameLength);
		dout.write(hostnameBytes);
		
		dout.writeInt(port);
		
		dout.flush();
		marshalledBytes = baOutputStream.toByteArray();
		
		baOutputStream.close();
		dout.close();
		
		return marshalledBytes;
	}
	
	public String getHostname() {
		return this.hostname;
	}
	
	public int getPort() {
		return this.port;
	}

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return this.type;
	}
}
