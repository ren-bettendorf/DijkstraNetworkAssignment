package cs455.overlay.node;

import java.net.InetAddress;
import java.io.IOException;
import java.util.ArrayList;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.Event;

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
	
	// java cs455.overlay.node.Registry local_port
	public static void main(String[] args) {
		Registry registry = null;
		if(args.length == 1) {
			registry = new Registry(Integer.parseInt(args[0]));
		}
		try {
			registry.setServerThread(new TCPServerThread(registry.getPort(), registry));
			registry.startServerThread();
		} catch(IOException ioe) {
			System.out.println(ioe.getMessage());
			System.exit(1);
		}
		
	}
	
	@Override
	public void onEvent(Event event) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString() {
		try {
			return InetAddress.getLocalHost().getHostAddress() + ":" + this.serverThread.getPort();
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
}
