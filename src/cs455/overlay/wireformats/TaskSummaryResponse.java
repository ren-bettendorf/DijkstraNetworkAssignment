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
	
	@Override
	public int getType() {
		return this.type;
	}

	public String getHostname() {
		return hostname;
	}

	public int getPort() {
		return port;
	}

	public int getMessageSent() {
		return messageSent;
	}

	public int getMessageReceived() {
		return messageReceived;
	}

	public int getMessageRelayed() {
		return messageRelayed;
	}

	public long getSumSent() {
		return sumSent;
	}

	public long getSumReceived() {
		return sumReceived;
	}

	public String getNodeID() {
		return this.hostname + ":" + this.port;
	}
}
