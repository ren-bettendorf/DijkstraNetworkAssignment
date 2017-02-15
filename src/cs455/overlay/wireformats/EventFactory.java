package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

// Singleton Instance
public class EventFactory {

	private static EventFactory instance = new EventFactory();

	/**
	* Creates EventFactory object for the singleton instance
	*/
	private EventFactory() {
	}

	/**
	* Creates RelayMessage object for marshalling bytes
	*
	* @return  Returns Singleton Instance
	*/
	public static EventFactory getInstance() {
		return instance;
	}

	/**
	* Returns the event based on the type of Event
	*
	* @param  data  Byte[] that holds the marshalled data
	* @param  socket Keeps track of the socket because some Events need that data to be passed along
	* @return Returns the Event based on the input data
	*/
	public synchronized Event getEvent(byte[] data, Socket socket) {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

		try {
			int type = din.readInt();

			baInputStream.close();
			din.close();

			switch (type) {
			case Protocols.REGISTER_REQUEST:
				return new RegistrationRequest(data, socket);
			case Protocols.REGISTER_RESPONSE:
				return new RegistrationResponse(data);
			case Protocols.DEREGISTER_REQUEST:
				return new DeregisterRequest(data, socket);
			case Protocols.DEREGISTER_RESPONSE:
				return new DeregisterResponse(data);
			case Protocols.MESSAGING_NODES_LIST:
				return new MessagingNodesList(data);
			case Protocols.LINK_WEIGHTS:
				return new LinkWeights(data);
			case Protocols.RELAY_CONNECTION:
				return new RelayConnection(data, socket);
			case Protocols.RELAY_MESSAGE:
				return new RelayMessage(data);
			case Protocols.TASK_INITIATE:
				return new TaskInitiate(data);
			case Protocols.TASK_COMPLETE:
				return new TaskComplete(data);
			case Protocols.TASK_SUMMARY_REQUEST:
				return new TaskSummaryRequest(data);
			case Protocols.TASK_SUMMARY_RESPONSE:
				return new TaskSummaryResponse(data);

			default:
				return null;
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return null;
	}
}
