package cs455.overlay.wireformats;

import java.io.IOException;

public interface IMessage {
	
	public byte[] getBytes() throws IOException;
}