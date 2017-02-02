package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

// Singleton Instance
public class EventFactory  {
	
	private static EventFactory instance = new EventFactory();
	
	private EventFactory() { }
	
	public static EventFactory getInstance() {
		return instance;
	}
	
	public synchronized Event getEvent(byte[] data, Socket socket)
	{
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        try {
            int type = din.readInt();
            System.out.println("Received message of type: " + type);
            baInputStream.close();
            din.close();

            switch (type) {
                case Protocols.REGISTER_REQUEST:
                    return new RegistrationRequest(data, socket);
                case Protocols.REGISTER_RESPONSE:
                	return new RegistrationResponse(data);
                case Protocols.DEREGISTER_REQUEST:
                	return new DeregisterRequest(data, socket);
                default:
                	return null;
            }
        } catch (IOException ioe) {
        	ioe.printStackTrace();
        }
		return null;
	}
}
