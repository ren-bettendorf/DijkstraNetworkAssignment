package cs455.overlay.transport;

public class TCPServerThread implements Runnable {
	
	private int port;
	private ServerSocket serverSocket;
	private MessagingNode node;
	
	public TCPServerThread(int port, MessagingNode node) throws IOException {
		this.port = port;
		this.node = node;
		try {
			this.serverSocket = new ServerSocket(getPort())
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			break;
		}
	}
	
	public void run() {
		System.out.println("Starting thread for node: " + node.toString() + ". Listening on port: " + getPort());
		
		while(socket != null) {
			try {
				Socket s = serverSocket.accept();
			} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			break;
			}
		}
		
		System.out.println("Ending thread for node: " + node.toString() + ". Closing on port: " + getPort());
	}
	
	private int getPort() {
		return this.port;
	}
	
	public ServerSocket getServerSocket() {
		return this.serverSocket;
	}
}
