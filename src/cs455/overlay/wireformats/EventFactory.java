package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

// Singleton Instance
public class EventFactory {

	private static EventFactory instance = new EventFactory();

	private EventFactory() {
	}

	public static EventFactory getInstance() {
		return instance;
	}

	public synchronized Event getEvent(byte[] data, Socket socket) {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

		try {
			int type = din.readInt();

			baInputStream.close();
			din.close();

			switch (type) {
			case Protocols.REGISTER_REQUEST:
				System.out.println("Returned Registration Request");
				return new RegistrationRequest(data, socket);
			case Protocols.REGISTER_RESPONSE:
				System.out.println("Returned Registraion Response");
				return new RegistrationResponse(data);
			case Protocols.DEREGISTER_REQUEST:
				System.out.println("Returned Deregistration Request");
				return new DeregisterRequest(data, socket);
			case Protocols.DEREGISTER_RESPONSE:
				System.out.println("Returned Deregistration Response");
				return new DeregisterResponse(data);
			case Protocols.MESSAGING_NODES_LIST:
				System.out.println("Returned MessagingNodesList");
				return new MessagingNodesList(data);
			case Protocols.LINK_WEIGHTS:
				System.out.println("Returned Link Weights");
				return new LinkWeights(data);
			case Protocols.RELAY_CONNECTION:
				System.out.println("Returned Relay Connection");
				return new RelayConnection(data, socket);
			case Protocols.TASK_INITIATE:
				System.out.println("Returned Task Initiate");
				return new TaskInitiate(data);
			case Protocols.RELAY_MESSAGE:
				System.out.println("Returned Relay Message");
				return new RelayMessage(data);
			default:
				System.out.println("Returned default null");
				return null;
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return null;
	}
}
