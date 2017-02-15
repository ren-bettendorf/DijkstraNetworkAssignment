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
	private int payload;
	
	/**
	* Creates RelayMessage object for marshalling bytes
	*
	* @param  payload  Random integer to sum at final desitnation
	* @param  connectList Path the payload will go along
	*/
	public RelayMessage(int payload, String connectList) {
		this.type = Protocols.RELAY_MESSAGE;
		this.connectList = connectList;
		this.payload = payload;
	}
	
	/**
	* Creates RelayMessage object for unmarshalling bytes
	*
	* @param  marshalledBytes  Marshalled bytes to be unwrapped
	*/
	public RelayMessage(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		type = din.readInt();
		
		int connectLinksLength = din.readInt();
		byte[] connectLinksBytes = new byte[connectLinksLength];
		din.readFully(connectLinksBytes);
		
		connectList = new String(connectLinksBytes);
		
		payload = din.readInt();
		
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
		
		byte[] connectLinksBytes = connectList.getBytes();
		int connectLinksLength = connectLinksBytes.length;
		dout.writeInt(connectLinksLength);
		dout.write(connectLinksBytes);

		dout.writeInt(payload);
		
		dout.flush();
		marshalledBytes = baOutputStream.toByteArray();
		
		baOutputStream.close();
		dout.close();
		
		return marshalledBytes;
	}
	
	/**
	* Getter function for the payload
	*
	* @return payload
	*/
	public int getPayload() {
		return this.payload;
	}
	
	/**
	* Getter function for the connections list
	*
	* @return connect list
	*/
	public String getConnections() {
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
