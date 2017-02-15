package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TaskComplete implements Event, Protocols {
	private int type;
	private String hostname;
	private int port;
	
	/**
	* Creates TaskComplete object for marshalling bytes
	*
	* @param  hostname  Contains the hostname of the node that completed the task
	* @param  port Contains the port of the node that completed the task
	*/
	public TaskComplete(String hostname, int port) {
		this.type = Protocols.TASK_COMPLETE;
		this.hostname = hostname;
		this.port = port;
	}
	
	/**
	* Creates TaskComplete object for unmarshalling bytes
	*
	* @param  marshalledBytes  Marshalled bytes to be unwrapped
	*/
	public TaskComplete(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		type = din.readInt();
		
		int hostnameLength = din.readInt();
		byte[] hostnameBytes = new byte[hostnameLength];
		din.readFully(hostnameBytes);
		
		hostname = new String(hostnameBytes);
		
		port = din.readInt();
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

	/**
	* Getter function for the NodeID list
	*
	* @return NodeID
	*/
	public String getNodeID() {
		return this.hostname + ":" + this.port;
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
