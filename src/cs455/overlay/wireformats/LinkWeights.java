package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LinkWeights implements Event, Protocols {
	private int type;
	private String connectList;
	private int numberLinks;
	
	/**
	* Creates LinkWeights object for marshalling bytes
	*
	* @param  numberLinks  Integer for the number of links that are contained in the connectList
	* @param  connectList Contains all the link data for creating Dijkstra's Shortest path.
	*/
	public LinkWeights(int numberLinks, String connectList) {
		this.type = Protocols.LINK_WEIGHTS;
		this.connectList = connectList;
		this.numberLinks = numberLinks;
	}
	
	/**
	* Creates LinkWegihts object for unmarshalling bytes
	*
	* @param  marshalledBytes  Marshalled bytes to be unwrapped
	*/
	public LinkWeights(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		type = din.readInt();
		
		numberLinks = din.readInt();
		
		int connectLinksLength = din.readInt();
		byte[] connectLinksBytes = new byte[connectLinksLength];
		din.readFully(connectLinksBytes);
		
		connectList = new String(connectLinksBytes);
		
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
		
		dout.writeInt(numberLinks);
		
		byte[] connectLinksBytes = connectList.getBytes();
		int connectLinksLength = connectLinksBytes.length;
		dout.writeInt(connectLinksLength);
		dout.write(connectLinksBytes);
		
		
		dout.flush();
		marshalledBytes = baOutputStream.toByteArray();
		
		baOutputStream.close();
		dout.close();
		
		return marshalledBytes;
	}
	
	/**
	* Getter function for the numberLinks
	*
	* @return numberLinks
	*/
	public int getNumberLinks() {
		return this.numberLinks;
	}
	
	/**
	* Getter function for the connectList
	*
	* @return connectList
	*/
	public String getListWeights() {
		return this.connectList;
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
