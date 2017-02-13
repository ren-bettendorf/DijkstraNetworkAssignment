package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RelayMessage implements Event, Protocols {
	private int type;
	private String connectList;
	private int data;
	
	public RelayMessage(int data, String connectList) {
		this.type = Protocols.RELAY_MESSAGE;
		this.connectList = connectList;
		this.data = data;
	}
	
	public RelayMessage(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		type = din.readInt();
		
		int connectLinksLength = din.readInt();
		byte[] connectLinksBytes = new byte[connectLinksLength];
		din.readFully(connectLinksBytes);
		
		connectList = new String(connectLinksBytes);
		
		data = din.readInt();
		
		baInputStream.close();
		din.close();
	}
	
	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		dout.writeInt(type);
		
		byte[] connectLinksBytes = connectList.getBytes();
		int connectLinksLength = connectLinksBytes.length;
		dout.writeInt(connectLinksLength);
		dout.write(connectLinksBytes);

		dout.writeInt(data);
		
		dout.flush();
		marshalledBytes = baOutputStream.toByteArray();
		
		baOutputStream.close();
		dout.close();
		
		return marshalledBytes;
	}
	
	public int getData() {
		return this.data;
	}
	
	public String getConnections() {
		return this.connectList;
	}

	@Override
	public int getType() {
		return this.type;
	}
}
