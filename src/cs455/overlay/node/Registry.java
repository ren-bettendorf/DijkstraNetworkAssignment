package cs455.overlay.node;

import java.util.ArrayList;
import cs455.overlay.transport.TCPServerThread;

public class Registry implements Node {
	
	private int port;
	private ArrayList<Node> registeredNodes = new ArrayList<Node>();
	private TCPServerThread serverThread;
	
	public Registry(int port)
	{
		this.port = port;
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
	
	public void setServerThread(TCPServerThread serverThread) {
		this.serverThread = serverThread;
	}
	
	public void startServerThread() {
		new Thread(this.serverThread).start();
	}
	
	public int getPort() {
		return this.port;
	}
	
	// java cs455.overlay.node.MessagingNode local_port
	public static void main(String[] args) {
		Registry registry = null;
		if(args.length == 1) {
			registry = new Registry(Integer.parseInt(args[0]));
		}
		
		registry.setServerThread(new TCPServerThread(registry.getPort(), registry));
		registry.startServerThread();
		
		
	}
	
	@Override
	public void onEvent(Event event) throws IOException {
		// TODO Auto-generated method stub

	}
}
