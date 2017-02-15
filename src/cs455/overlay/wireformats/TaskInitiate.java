package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TaskInitiate implements Event, Protocols{
	private int type;
	private int numberRounds;
	
	/**
	* Creates TaskInitiate object for marshalling bytes
	*
	* @param  numberRounds  Number of rounds the messaging node will complete
	*/
	public TaskInitiate(int numberRounds) {
		this.type = Protocols.TASK_INITIATE;
		this.numberRounds = numberRounds;
	}
	
	/**
	* Creates TaskInitiate object for unmarshalling bytes
	*
	* @param  marshalledBytes  Marshalled bytes to be unwrapped
	*/
	public TaskInitiate(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		type = din.readInt();

		numberRounds = din.readInt();
		
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

		dout.writeInt(numberRounds);
		
		dout.flush();
		marshalledBytes = baOutputStream.toByteArray();
		
		baOutputStream.close();
		dout.close();
		
		return marshalledBytes;
	}
	
	/**
	* Getter function for the numberRounds
	*
	* @return numberRounds
	*/
	public int getRoundNumber() {
		return this.numberRounds;
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
