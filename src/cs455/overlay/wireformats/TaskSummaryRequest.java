package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TaskSummaryRequest implements Event, Protocols {
	private int type;
	
	/**
	* Sends a request message to the registry and marshalls the bytes for the request
	*/
	public TaskSummaryRequest() {
		this.type = Protocols.TASK_SUMMARY_REQUEST;
	}
	
	/**
	* Creates TaskSummaryRequest object for marshalling bytes
	*
	* @param  marshalledBytes  Marshalled bytes to be unwrapped
	*/
	public TaskSummaryRequest(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		type = din.readInt();
		
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

}
