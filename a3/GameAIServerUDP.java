/**
 * The server will send the NPC locations.
 */

/*package a3;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

import ray.networking.server.GameConnectionServer;

public class GameAIServerUDP extends GameConnectionServer<UUID> {
 // game protocol as before, plus additional NPC protocol cases. i.e.,
 // messages regarding NPC’s sent to clients, such as:
	
	NPCcontroller npcCtrl = new NPCcontroller();
     
	public GameAIServerUDP(int localPort, ProtocolType protocolType) throws IOException {
		super(localPort, protocolType);
	}
	
	public void processPacket(Object o, InetAddress senderIP, int senderPort) {
		String message = (String) o;
		String[] messageTokens = message.split(",");
		
		if (messageTokens.length > 0) {
	
			if(messageTokens[0].compareTo("needNPC") == 0) {
				sendNPCinfo();
		    }
		    if(messageTokens[0].compareTo("collide") == 0)
		    { 
		    	//. . . 
		    }
		}
	}

	public void sendNPCinfo() { // informs clients of new NPC positions 
		for (int i=0; i<npcCtrl.getNumOfNPCs(); i++) { 
	        try { 
	        	String message = new String("mnpc," + Integer.toString(i));
	        	message += "," + (npcCtrl.getNPC(i)).getX();
	        	message += "," + (npcCtrl.getNPC(i)).getY();
	        	message += "," + (npcCtrl.getNPC(i)).getZ();
	        	sendPacketToAll(message);
	        	//. . .
	        	// also additional cases for receiving messages about NPCs, such as:
	        } catch (IOException e) {
				e.printStackTrace();
			}
	    }
	}
	
	/*public void sendCreateMessages(int npcID, String[] position) { // format: create, remoteId, x, y, z
 		try {
 			String message = new String("cnpc," + Integer.toString(npcID));
 			message += "," + position[0];
 			message += "," + position[1];
 			message += "," + position[2];
 			sendPacketToAll(message);
 		} catch (IOException e) {
 			e.printStackTrace();
 		}
 	}
}*/
           
