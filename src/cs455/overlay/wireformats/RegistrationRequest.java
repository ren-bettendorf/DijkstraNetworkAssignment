package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class RegistrationRequest implements Event, Protocols {
	private int type;
	private String hostname;
	private int port;
	private Socket socket;
	
	/**
	* Creates RegisterRequest object and marshalls the data
	*
	* @param hostname Hostname to be registered
	* @param port Port to be registered
	*/
	public RegistrationRequest(String hostname, int port) {
		this.type = Protocols.REGISTER_REQUEST;
		this.hostname = hostname;
		this.port = port;
	}
	
	/**
	* Creates RegisterRequest object and unmarshalls the data
	*
	* @param marshalledBytes Bytes containing data
	* @param socket Socket to be used to verify sender
	*/
	public RegistrationRequest(byte[] marshalledBytes, Socket socket) throws IOException {
		this.socket = socket;
		
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		type = din.readInt();
		
		int hostnameLength = din.readInt();
		byte[] hostnameBytes = new byte[hostnameLength];
		din.readFully(hostnameBytes);
		
		hostname = new String(hostnameBytes);
		
		port = din.readInt();
		baInputStream.close();
		din.close();
	}
	
	/**
	* Marshalls the data for a TCPSender
	*
	* @return Returns the marshalled data
	*/
	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		dout.writeInt(type);
		
		byte[] hostnameBytes = hostname.getBytes();
		int hostnameLength = hostnameBytes.length;
		dout.writeInt(hostnameLength);
		dout.write(hostnameBytes);
		
		dout.writeInt(port);
		
		dout.flush();
		marshalledBytes = baOutputStream.toByteArray();
		
		baOutputStream.close();
		dout.close();
		
		return marshalledBytes;
	}
	
	/**
	* Getter for hostname
	*
	* @return Returns the hostname to be registered
	*/
	public String getHostname() {
		return this.hostname;
	}
	
	/**
	* Getter for port
	*
	* @return Returns the port to be registered
	*/
	public int getPort() {
		return this.port;
	}
	
	/**
	* Getter for socket
	*
	* @return Returns the socket associated with registration
	*/
	public Socket getSocket() {
		return this.socket;
	}

	/**
	* Getter for event type
	*
	* @return Returns the event type
	*/
	@Override
	public int getType() {
		return this.type;
	}
}
