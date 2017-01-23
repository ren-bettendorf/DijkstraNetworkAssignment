package cs455.overlay.node;

import java.util.ArrayList;

public class Registry {
	
	private String host;
	private int port;
	private int numOverlay;
	private ArrayList<Node> registeredNodes = new ArrayList<Node>();
	
	public Registry(String host, int port)
	{
		this.host = host;
		this.port = port;
		this.numOverlay = 10;
	}
	
	public Registry(String host, int port, int numOverlay) throws Exception
	{
		if(numOverlay < 10)
		{
			throw new Exception("Number Overlay must be greater than 10: " + numOverlay);
		}
		this.host = host;
		this.port = port;
		this.numOverlay = numOverlay;
	}
	
	public void registerNode(Node node)
	{
		if(!registeredNodes.contains(node))
		{
			registeredNodes.add(node);
		}
	}
	
	public void deregisterNode(Node node)
	{
		if(registeredNodes.contains(node))
		{
			registeredNodes.remove(node);
		}
	}
	
	// java cs455.overlay.node.MessagingNode local_port
	public static void main(String[] args) {
		
	}
}
