List of the files used:

cs455.overlay.dijkstra.Edge: Creates an Edge between two vertices and sets the weight.
cs455.overlay.dijkstra.Vertex: Creates a Vertex for a MessagingNode with a maximum connections allowed.
cs455.overlay.dijkstra.Graph: Creates the Graph containing the Edges and Vertices. Allows searching for edge weights.
cs455.overlay.dijkstra.ShortestPath: Finds the Shortest Path using Dijkstra's Algorithm for every Vertex in a Graph and computes Path Distance also.

cs455.overlay.node.Node: Base class for MessagingNode and Registry
cs455.overlay.node.MessagingNode: MessagingNode deals with running rounds while collecting data and sending the data to the registry. Registers/Deregisters with the Registry.
cs455.overlay.node.Registry: Registry responsible for building overlay, sending information relative to the graph to the MessagingNodes, starting tasks, and collecting/displaying summary of tasks.

cs455.overlay.transport.TCPServerThread: Server thread for handling new connections.
cs455.overlay.transport.TCPReceiverThread: Receiver thread for connections already established and handling events.
cs455.overlay.transport.TCPSender: Sends marshalled data between Nodes.

cs455.overlay.wireformats.Event: Base Class for rest of Events.
cs455.overlay.wireformats.EventFactory: Singleton class that creates different events based on events being received.
cs455.overlay.wireformats.Protocols: Class that contains all the protocol types.
cs455.overlay.wireformats.DeregisterRequest: Class that contains all the information required for a deregistration request.
cs455.overlay.wireformats.DeregisterResponse: Class that contains all the information required for a deregistration response.
cs455.overlay.wireformats.LinkWeights: Class that contains all the information required for setting up edges for the graph.
cs455.overlay.wireformats.MessagingNodesList: Class that contains all the information required for setting up MessagingNode connections.
cs455.overlay.wireformats.RegistrationRequest: Class that contains all the information required for a registration request.
cs455.overlay.wireformats.RegistrationResponse: Class that contains all the information required for a registration response.
cs455.overlay.wireformats.RelayConnection: Class that contains all the information required for setting up a relay connection.
cs455.overlay.wireformats.RelayMessage: Class that contains all the information required for relaying a message between nodes.
cs455.overlay.wireformats.TaskComplete: Class that contains all the information required for a MessagingNode to let the Registry know it has completed the task.
cs455.overlay.wireformats.TaskInitiate: Class that contains all the information required for a Registry to let a MessagingNode start a task.
cs455.overlay.wireformats.TaskSummaryRequest: Class that contains all the information required for a Registry to ask a MessagingNode for a summary of a task.
cs455.overlay.wireformats.TaskSummaryResponse: Class that contains all the information required for a MessagingNode to send summary to Registry.

------------

Other important information:

I felt the information was not as clear as I would have liked it about exiting the program so I implemented it such that a MessagingNode exits only if it deregisters and Registry never exits
so if you are hoping to exit the programs then you must force close the process. The Setup-Overlay is implemented so that it can take a size 1 < size < |nodes| so if 10 nodes are connected 
setup-overlay (2<=9) will be accepted. Running the registry is as simple as "java cs455.overlay.node.Registry port-number" and MessagingNode (With Registry running) 
"java cs455.overlay.node.MessagingNode registry-address registry-port'


If you have any questions you can reach me via phone at (707) 599-1696 or email at renaldorini@gmail.com
