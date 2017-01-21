package cs455.overlay.node;

import java.io.IOException;

import cs455.overlay.wireformats.Event;

public interface Node {
	void onEvent(Event event) throws IOException;
}
