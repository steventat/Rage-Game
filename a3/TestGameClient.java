package a3;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;
import java.util.Vector;

import ray.networking.client.GameConnectionClient;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class TestGameClient extends GameConnectionClient { // same as before, plus code to handle additional NPC messages
	
	private MyGame game;
	private UUID id;
    private Vector<GhostNPC> ghostNPCs;
    private int numNPCs;
     
    public TestGameClient(InetAddress remoteAddr, int remotePort,
			ProtocolType protocolType, MyGame game) throws IOException {
		super(remoteAddr, remotePort, protocolType);
		this.game = game;
		this.id = UUID.randomUUID();
		this.ghostNPCs = new Vector<GhostNPC>();
	}
    
    private void createGhostNPC(int id, Vector3 position) throws IOException { 
         GhostNPC newNPC = new GhostNPC(id, position);
         ghostNPCs.add(newNPC);
         game.addGhostNPCtoGameWorld(newNPC);
    }
      
    private void updateGhostNPC(int id, Vector3 position) { 
    	ghostNPCs.get(id).setPosition(position);
    }
     
      
    public void askForNPCinfo() { 
    	try { 
    		sendPacket(new String("needNPC," + id.toString()));
    	} catch (IOException e) { 
    		e.printStackTrace();
    	}
    }
     
     public void processPacket(Object message) {
    	 
    	 System.out.print("Processing NPC packet...\n");
    	 String strMessage = (String) message;
    	 String[] messageTokens = strMessage.split(",");
    	 if(messageTokens.length > 0) {
	    	 // handle updates to NPC positions
	         // format: (mnpc,npcID,x,y,z)
	         if(messageTokens[0].compareTo("mnpc") == 0)
	         { 
	            int ghostID = Integer.parseInt(messageTokens[1]);
	            Vector3 ghostPosition = Vector3f.createFrom(
	            Float.parseFloat(messageTokens[2]),
	            Float.parseFloat(messageTokens[2]),
	            Float.parseFloat(messageTokens[2]));
	            
	            //Checking to see if there are any NPCs.
	            if(ghostNPCs.size() != 0) {
	            	for(GhostNPC npc: ghostNPCs) {
	            		//if
	            	}
	            }
	            updateGhostNPC(ghostID, ghostPosition);
	         }
    	 }
    	 /*if (messageTokens[0].compareTo("create") == 0) {
				System.out.println("Obtained NPC create message");
				int ghostID = Integer.parseInt(messageTokens[1]);
				Vector3 ghostPosition = Vector3f.createFrom(
					Float.parseFloat(messageTokens[2]),
					Float.parseFloat(messageTokens[3]),
					Float.parseFloat(messageTokens[4]));
				try {
					System.out.println("Creating NPC ghost avatar");
					createGhostNPC(ghostID, ghostPosition);
				} catch (IOException e) { 
					System.out.println("error creating NPC ghost avatar");
				} 
			}*/
     }
     
}
