package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RegistrationResponse implements Event, Protocols {
	private int type;
	private byte result;
	private String additionalInfo;
	
	/**
	* Creates RegistrationResponse object for marshalling bytes
	*
	* @param  result  Success or failure is held here
	* @param  response String holding reason for result
	*/
	public RegistrationResponse(byte result, String additionalInfo) {
		this.type = Protocols.REGISTER_RESPONSE;
		this.result = result;
		this.additionalInfo = additionalInfo;
	}
	
	/**
	* Creates RelayMessage object for unmarshalling bytes
	*
	* @param  marshalledBytes  Marshalled bytes to be unwrapped
	*/
	public RegistrationResponse(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		type = din.readInt();
		
		this.result = din.readByte();
		
		int additonalInfoLength = din.readInt();
		byte[] additionalInfoByteArray = new byte[additonalInfoLength];
		din.readFully(additionalInfoByteArray);
		
		additionalInfo = new String(additionalInfoByteArray);
		
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
		
		byte[] additionalInfoByteArray = additionalInfo.getBytes();
		int addiontalInfoLength = additionalInfoByteArray.length;
		dout.writeInt(addiontalInfoLength);
		dout.write(additionalInfoByteArray);
		
		dout.flush();
		marshalledBytes = baOutputStream.toByteArray();
		
		baOutputStream.close();
		dout.close();
		
		return marshalledBytes;
	}
	
	/**
	* Getter function for the result
	*
	* @return result
	*/
	public byte getResult() {
		return this.result;
	}
	
	/**
	* Getter function for the additionalInfo
	*
	* @return additionalInfo
	*/
	public String getResponse() {
		return this.additionalInfo;
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
