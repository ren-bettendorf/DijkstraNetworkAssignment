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
	
	public DeregisterResponse(byte result, String response) {
		this.type = Protocols.DEREGISTER_RESPONSE;
		this.result = result;
		this.response = response;
	}
	
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
	
	public String getResponse() {
		return this.response;
	}
	
	public byte getResult() {
		return this.result;
	}

	public byte getPort() {
		return this.result;
	}
	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return this.type;
	}
}
