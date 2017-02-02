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
	
	public LinkWeights(int numberLinks, String connectList) {
		this.type = Protocols.LINK_WEIGHTS;
		this.connectList = connectList;
		this.numberLinks = numberLinks;
	}
	
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

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return this.type;
	}
}
