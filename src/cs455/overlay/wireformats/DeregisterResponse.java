package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DeregisterResponse implements Event, Protocols {
	private int type;
	private String response;
	private byte result;
	
	/**
	* Creates DeregisterResponse object for marshalling bytes
	*
	* @param  result  Success or failure is held here
	* @param  response String holding reason for result
	*/
	public DeregisterResponse(byte result, String response) {
		this.type = Protocols.DEREGISTER_RESPONSE;
		this.result = result;
		this.response = response;
	}
	
	/**
	* Creates DeregisterResponse object for marshalling bytes
	*
	* @param  marshalledBytes  Marshalled bytes to be unwrapped
	*/
	public DeregisterResponse(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		type = din.readInt();

		result = din.readByte();
		int responseLength = din.readInt();
		byte[] responseBytes = new byte[responseLength];
		din.readFully(responseBytes);
		
		response = new String(responseBytes);
		
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

		dout.writeByte(result);
		
		byte[] responseBytes = response.getBytes();
		dout.writeInt(responseBytes.length);
		dout.write(responseBytes);
		
		dout.flush();
		marshalledBytes = baOutputStream.toByteArray();
		
		baOutputStream.close();
		dout.close();
		
		return marshalledBytes;
	}
	
	/**
	* Getter function for the response
	*
	* @return response
	*/
	public String getResponse() {
		return this.response;
	}
	
	/**
	* Getter function for the result
	*
	* @return result
	*/
	public byte getResult() {
		return this.result;
	}

	public byte getPort() {
		return this.result;
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
