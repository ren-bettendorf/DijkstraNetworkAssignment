package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MessagingNodesList implements Event, Protocols {

	private int type;
	private String connectList;
	private int numberNodes;
	
	public MessagingNodesList(int numberNodes, String connectList) {
		this.type = Protocols.MESSAGING_NODES_LIST;
		this.connectList = connectList;
		this.numberNodes = numberNodes;
	}
	
	public MessagingNodesList(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		type = din.readInt();

		numberNodes = din.readInt();
		
		int connectListLength = din.readInt();
		byte[] connectListBytes = new byte[connectListLength];
		din.readFully(connectListBytes);
		
		connectList = new String(connectListBytes);
		
		baInputStream.close();
		din.close();
	}
	
	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		dout.writeInt(type);

		dout.writeInt(numberNodes);
		
		byte[] connectListBytes = connectList.getBytes();
		int connectListLength = connectListBytes.length;
		dout.writeInt(connectListLength);
		dout.write(connectListBytes);
		
		
		dout.flush();
		marshalledBytes = baOutputStream.toByteArray();
		
		baOutputStream.close();
		dout.close();
		
		return marshalledBytes;
	}
	
	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return this.type;
	}
	
	public int getNumberNodes() {
		return this.numberNodes;
	}
	
	public String getConnectList() {
		return this.connectList;
	}
}
