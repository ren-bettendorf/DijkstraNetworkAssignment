package cs455.overlay.node;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cs455.overlay.wireformats.Event;

public class MessagingNode implements Node {
	private int port;
	
	public MessagingNode(int port)
	{
		this.port = port;
	}
	
	public void register()
	{
		
	}

	@Override
	public void onEvent(Event event) throws IOException {
		// TODO Auto-generated method stub
		
	}
}
