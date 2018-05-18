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
	private int numGhosts;
	private Vector<GhostNPC> ghostNPCs;
	
	public ProtocolClient(InetAddress remAddr, int remPort,
	ProtocolType pType, MyGame game) throws IOException { 
		super(remAddr, remPort, pType);
		this.game = game;
		this.id = UUID.randomUUID(); //Client ID
		this.ghostAvatars = new Vector<GhostAvatar>();
		this.ghostNPCs = new Vector<GhostNPC>();
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
			
			// format: create, remoteId, x,y,z or dsfr, remoteId, x,y,z
			if (messageTokens[0].compareTo("create") == 0) {
				System.out.println("Obtained create message");
				UUID ghostID = UUID.fromString(messageTokens[1]);
				Vector3 ghostPosition = Vector3f.createFrom(
					Float.parseFloat(messageTokens[2]),
					Float.parseFloat(messageTokens[3]),
					Float.parseFloat(messageTokens[4]));
				try {
					System.out.println("Creating ghost avatar");
					createGhostAvatar(ghostID, ghostPosition);
				} catch (IOException e) { 
					System.out.println("error creating ghost avatar");
				} 
			}
			if(messageTokens[0].compareTo("dsfr") == 0) { // receive "dsfr"	//Should receive the UUID of the other players.
				System.out.println("Obtained details for message");
				System.out.println(messageTokens);
				UUID ghostID = UUID.fromString(messageTokens[1]);	//Can be either 1 or 5. Only 1 works for now.
				System.out.println(ghostID);
				System.out.println(id);
				System.out.println("Comparing UUIDs...: " + ghostID.compareTo(id));
				if(ghostID.compareTo(id) != 0) {
					System.out.println("IDs do not match");
					Vector3 ghostPosition = Vector3f.createFrom(
						Float.parseFloat(messageTokens[2]),
						Float.parseFloat(messageTokens[3]),
						Float.parseFloat(messageTokens[4]));
					try {
						System.out.println("Creating ghost avatar");
						createGhostAvatar(ghostID, ghostPosition);
					} catch (IOException e) {
						System.out.println("error creating ghost avatar");
						e.printStackTrace();
					}
				}
			}
			if(messageTokens[0].compareTo("wsds") == 0) { // receive "want"
				System.out.println("Got wants details message");
				this.sendDetailsForMessage(UUID.fromString(messageTokens[1]), game.getPlayerPosition());
			}
			if(messageTokens[0].compareTo("move") == 0) { // receive "move" 
				System.out.println("Received move message");
				UUID ghostID = UUID.fromString(messageTokens[1]);
				try {
					GhostAvatar avatar = game.getGhostAvatarByID(ghostID);
					Vector3 ghostPosition = Vector3f.createFrom(
							Float.parseFloat(messageTokens[2]),
							Float.parseFloat(messageTokens[3]),
							Float.parseFloat(messageTokens[4]));
					avatar.setLocalPosition(ghostPosition);
				} catch (Exception e) {
					System.out.println("Could not find Ghost to move");
				}
			}
			if(messageTokens[0].compareTo("mnpc") == 0)
	         {
				//System.out.println("Receiving NPC info...");
	            int ghostID = Integer.parseInt(messageTokens[1]);
	            Vector3 ghostPosition = Vector3f.createFrom(
	            Float.parseFloat(messageTokens[2]),
	            Float.parseFloat(messageTokens[2]),
	            Float.parseFloat(messageTokens[2]));
	            
	            updateGhostNPC(ghostID, ghostPosition);
	         }
		} 
	}
	
	private void removeGhostAvatar(UUID ghostID) {
		// TODO Auto-generated method stub
		//game.removeGhostAvatarFromGameWorld(avatar);
		
	}

	private void createGhostAvatar(UUID ghostID, Vector3 ghostPosition) throws IOException {
		// TODO Auto-generated method stub
		numGhosts++;
		try {
			GhostAvatar ghost = new GhostAvatar(ghostID, ghostPosition);
			ghostAvatars.add(ghost);
			game.addGhostAvatarToGameWorld(ghost);
		} catch (IOException e) { 
			e.printStackTrace();
		}
	}
	
	public int getNumGhosts() {
		return numGhosts;
	}
	
	private void createGhostNPC(int id, Vector3 position) throws IOException
    { 
        GhostNPC newNPC = new GhostNPC(id, position);
        ghostNPCs.add(newNPC);
        game.addGhostNPCtoGameWorld(newNPC);
    }
     
    private void updateGhostNPC(int id, Vector3 position)
    {
    	//Checking to see if there are any NPCs.
        /*if(ghostNPCs.size() != 0) {
        	for(GhostNPC npc: ghostNPCs) {
        		//if
        	}
        }*/
    	if(ghostNPCs.size() == id) {
    		try {
				this.createGhostNPC(id, position);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		ghostNPCs.get(id).setPosition(position);
    	}
    }
    
     
    public void askForNPCinfo() { 
    	try {
    		System.out.println("Asking for NPC info...");
    		sendPacket(new String("needNPC," + id.toString()));
   	 	} catch (IOException e) { 
   	 		e.printStackTrace();
   	 	}
    }

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
		try { 	
			String message = new String("dsfr," + remId.toString());
			message += "," + pos.x()+"," + pos.y() + "," + pos.z();
			message += "," + id.toString();
			System.out.println("Sending Details For message\n");
			sendPacket(message);
		}
		catch (IOException e) { 
			e.printStackTrace();
		}
	}
	public void sendMoveMessage(Vector3 pos) { 
		try { 	
			String message = new String("move," + id.toString());
			message += "," + pos.x()+"," + pos.y() + "," + pos.z();
			System.out.println("Sending move message\n");
			sendPacket(message);
		}
		catch (IOException e) { 
			e.printStackTrace();
		}
	}
	
	public UUID getID() {
		return id;
	}
}