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
	
	/**
	* Creates MessagingNodesList object for marshalling bytes
	*
	* @param  numberNodes  Integer for the number of nodes to be connected to
	* @param  connectList The ID's of the nodes to be connected to
	*/
	public MessagingNodesList(int numberNodes, String connectList) {
		this.type = Protocols.MESSAGING_NODES_LIST;
		this.connectList = connectList;
		this.numberNodes = numberNodes;
	}
	
	/**
	* Creates MessagingNodesList object for unmarshalling bytes
	*
	* @param  marshalledBytes  Marshalled bytes to be unwrapped
	*/
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
	
	/**
	* Getter function for the numberNodes
	*
	* @return numberNodes
	*/
	public int getNumberNodes() {
		return this.numberNodes;
	}
	
	/**
	* Getter function for the connectList
	*
	* @return connectList
	*/
	public String getConnectList() {
		return this.connectList;
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
}
