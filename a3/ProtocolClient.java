package a3;

import ray.networking.IGameConnection.ProtocolType;	// import networking
import ray.networking.client.GameConnectionClient;
import ray.rml.Vector3;
import ray.rml.Vector3f;

import java.util.UUID;								// import networking
import java.util.Vector;

//import graphicslib3D.Vector3D;

import java.io.IOException;							// import networking
import java.net.InetAddress;						// import networking


public class ProtocolClient extends GameConnectionClient { 
	private MyGame game;
	private UUID id;
	private Vector<GhostAvatar> ghostAvatars;
	
	public ProtocolClient(InetAddress remAddr, int remPort,
	ProtocolType pType, MyGame game) throws IOException { 
		super(remAddr, remPort, pType);
		this.game = game;
		this.id = UUID.randomUUID();
		this.ghostAvatars = new Vector<GhostAvatar>();
	}
	
	@Override
	protected void processPacket(Object message) { 
		System.out.print("Processing packet...\n");
		String strMessage = (String)message;
		String[] messageTokens = strMessage.split(",");
		if(messageTokens.length > 0) {
			if(messageTokens[0].compareTo("join") == 0) { // receive "join"
				// format: join, success or join, failure
				if(messageTokens[1].compareTo("success") == 0) {
					System.out.println("Got connection to server\n");
					game.setIsConnected(true);
					sendCreateMessage(game.getPlayerPosition());
				}
				if(messageTokens[1].compareTo("failure") == 0) { 
					game.setIsConnected(false);
				}
			}
			if(messageTokens[0].compareTo("bye") == 0) { // receive "bye"  
				// format: bye, remoteId
				UUID ghostID = UUID.fromString(messageTokens[1]);
				removeGhostAvatar(ghostID);
			}
			if ((messageTokens[0].compareTo("dsfr") == 0 ) // receive "dsfr"
			 || (messageTokens[0].compareTo("create")==0)) { // format: create, remoteId, x,y,z or dsfr, remoteId, x,y,z
				UUID ghostID = UUID.fromString(messageTokens[1]);
				Vector3 ghostPosition = Vector3f.createFrom(
					Float.parseFloat(messageTokens[2]),
					Float.parseFloat(messageTokens[3]),
					Float.parseFloat(messageTokens[4]));
				try { 
					createGhostAvatar(ghostID, ghostPosition);
				} catch (IOException e) { 
					System.out.println("error creating ghost avatar");
				} 
			}
			if(messageTokens[0].compareTo("create") == 0) { // receive "create"  
				// etc….. 
			}
			if(messageTokens[0].compareTo("wsds") == 0) { // receive "want"
				// etc….. 
			}
			if(messageTokens[0].compareTo("move") == 0) { // receive "move" 
				// etc….. 
			}
		} 
	}
	
	private void removeGhostAvatar(UUID ghostID) {
		// TODO Auto-generated method stub
		
	}

	private void createGhostAvatar(UUID ghostID, Vector3 ghostPosition) throws IOException {
		// TODO Auto-generated method stub
		ghostAvatars.add(new GhostAvatar(ghostID, ghostPosition));
		
	}

	/*Also need functions to instantiate ghost avatar, remove a ghost avatar,
	look up a ghost in the ghost table, update a ghost's position, and
	accessors as needed.*/
	public void sendJoinMessage() { // format: join, localId
		try { 
			System.out.println("Sending join message");
			sendPacket(new String("join," + id.toString()));
		} catch (IOException e) { 
			e.printStackTrace();
		} 
	}
	
	public void sendCreateMessage(Vector3 vector3) { // format: (create, localId, x,y,z) // VECTOR3D use to be VECTOR3
		try { 	
			String message = new String("create," + id.toString());
			message += "," + vector3.x()+"," + vector3.y() + "," + vector3.z();
			System.out.println("Sending create messages\n");
			sendPacket(message);

		}
		catch (IOException e) { 
			e.printStackTrace();
		} 
	}
	
	public void sendByeMessage() { 
	// etc….. 
	}
	public void sendDetailsForMessage(UUID remId, Vector3 pos) { 
	// etc….. 
	}
	public void sendMoveMessage(Vector3 pos) { 
	// etc….. 
	}
}