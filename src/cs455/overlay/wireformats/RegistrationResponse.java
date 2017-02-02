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
	
	public RegistrationResponse(byte result, String additionalInfo) {
		this.type = Protocols.REGISTER_RESPONSE;
		this.result = result;
		this.additionalInfo = additionalInfo;
	}
	
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
	
	public byte getResult() {
		return this.result;
	}
	
	public String getResponse() {
		return this.additionalInfo;
	}
	
	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return this.type;
	}
}
