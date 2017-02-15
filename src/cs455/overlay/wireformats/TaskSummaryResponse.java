package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TaskSummaryResponse implements Event, Protocols {
	private int type;
	private String hostname;
	private int port, messageSent, messageReceived, messageRelayed;
	private long sumSent, sumReceived;
	
	public TaskSummaryResponse(String hostname, int port, int messageSent, long sumSent, int messageReceived, long sumReceived, int messageRelayed) {
		this.type = Protocols.TASK_SUMMARY_RESPONSE;
		this.hostname = hostname;
		this.port = port;
		this.messageSent = messageSent;
		this.sumSent = sumSent;
		this.messageReceived = messageReceived;
		this.sumReceived = sumReceived;
		this.messageRelayed = messageRelayed;
	}
	
	public TaskSummaryResponse(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		type = din.readInt();
		
		int hostnameLength = din.readInt();
		byte[] hostnameBytes = new byte[hostnameLength];
		din.readFully(hostnameBytes);
		
		hostname = new String(hostnameBytes);
		
		port = din.readInt();
		
		messageSent = din.readInt();
		sumSent = din.readLong();
		messageReceived = din.readInt();
		sumReceived = din.readLong();
		messageRelayed = din.readInt();
		
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
		dout.writeInt(messageSent);
		dout.writeLong(sumSent);
		dout.writeInt(messageReceived);
		dout.writeLong(sumReceived);
		dout.writeInt(messageRelayed);
		
		dout.flush();
		marshalledBytes = baOutputStream.toByteArray();
		
		baOutputStream.close();
		dout.close();
		
		return marshalledBytes;
	}
	
	/**
	* Getter function for the message type
	*
	* @return message type
	*/
	@Override
	public int getType() {
		return this.type;
	}

	/**
	* Getter function for the hostname
	*
	* @return hostname
	*/
	public String getHostname() {
		return hostname;
	}

	/**
	* Getter function for the port
	*
	* @return port
	*/
	public int getPort() {
		return port;
	}

	/**
	* Getter function for the messageSent
	*
	* @return messageSent
	*/
	public int getMessageSent() {
		return messageSent;
	}

	/**
	* Getter function for the messageReceived
	*
	* @return messageReceived
	*/
	public int getMessageReceived() {
		return messageReceived;
	}

	/**
	* Getter function for the messageRelayed
	*
	* @return messageRelayed
	*/
	public int getMessageRelayed() {
		return messageRelayed;
	}

	/**
	* Getter function for the sumSent
	*
	* @return sumSent
	*/
	public long getSumSent() {
		return sumSent;
	}

	/**
	* Getter function for the sumReceived
	*
	* @return sumReceived
	*/
	public long getSumReceived() {
		return sumReceived;
	}

	/**
	* Getter function for the nodeID
	*
	* @return nodeID
	*/
	public String getNodeID() {
		return this.hostname + ":" + this.port;
	}
}
