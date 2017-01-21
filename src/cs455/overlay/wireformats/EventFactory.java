package cs455.overlay.wireformats;

// Singleton Instance
public class EventFactory  {
	
	private static EventFactory instance = new EventFactory();
	
	private EventFactory() { }
	
	public static EventFactory getInstance() {
		return instance;
	}
	
	public Event getEvent(byte[] data)
	{
		Event event = null;
		
		return event;
	}
}
