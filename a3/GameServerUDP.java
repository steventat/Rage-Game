package a3;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import ray.networking.server.GameConnectionServer;
import ray.networking.server.IClientInfo;
public class GameServerUDP extends GameConnectionServer < UUID > {
	
	NPCcontroller npcCtrl = new NPCcontroller();
  
	public GameServerUDP(int localPort) throws IOException {
		super(localPort, ProtocolType.UDP);
	}
  
	@Override
	public void processPacket(Object o, InetAddress senderIP, int senderPort) {
		String message = (String) o;
		String[] msgTokens = message.split(",");
		if (msgTokens.length > 0) {
			// case where server receives a JOIN message
			// format: join,localid
			if (msgTokens[0].compareTo("join") == 0) {
				try {
					IClientInfo ci;
					ci = getServerSocket().createClientInfo(senderIP, senderPort);
					UUID clientID = UUID.fromString(msgTokens[1]);
					System.out.println("Adding client to client list " + clientID);
					addClient(ci, clientID);	//Adding new client to the client table. Important for lookup.
					System.out.println("Obtained join message from " + clientID + "\n");
					sendJoinedMessage(clientID, true);
			 } 	catch (IOException e) {
					e.printStackTrace();
				}
			}
			// case where server receives a CREATE message
			// format: create,localid,x,y,z
			if (msgTokens[0].compareTo("create") == 0) {
				System.out.println("Obtained create message");
				UUID clientID = UUID.fromString(msgTokens[1]);
				String[] pos = {
				msgTokens[2],
				msgTokens[3],
				msgTokens[4]
				};
				System.out.println("Sending out create messages to clients");
				System.out.println("Sending wants details messages to clients");
				sendCreateMessages(clientID, pos);
				sendWantsDetailsMessages(clientID);
			}
			// case where server receives a BYE message
			// format: bye,localid
			if (msgTokens[0].compareTo("bye") == 0) {
				UUID clientID = UUID.fromString(msgTokens[1]);
				sendByeMessages(clientID);
				removeClient(clientID);
			}
			// case where server receives a DETAILS-FOR message
			if (msgTokens[0].compareTo("dsfr") == 0) { 
				System.out.println("Received from Details for message from " + msgTokens[5] + "for \n" + msgTokens[1]);
				UUID clientID = UUID.fromString(msgTokens[1]);
				UUID remoteId = UUID.fromString(msgTokens[5]);
				String[] position = {
						msgTokens[2],
						msgTokens[3],
						msgTokens[4]
				};
				System.out.println(remoteId);
				this.sendDetailsMsg(clientID, remoteId, position);
			}
			// case where server receives a MOVE message
			if (msgTokens[0].compareTo("move") == 0) { 
				System.out.println("Received move message");
				UUID clientID = UUID.fromString(msgTokens[1]);
				String[] position = {
						msgTokens[2],
						msgTokens[3],
						msgTokens[4]
				};
				this.sendMoveMessages(clientID, position);
			}
			if(msgTokens[0].compareTo("needNPC") == 0) {
				System.out.println("Received needNPC message");
				sendNPCinfo();
		    }
		    if(msgTokens[0].compareTo("collide") == 0)
		    { 
		    	//. . . 
		    }
		    if(msgTokens[0].compareTo("follow") == 0) {
		    	
		    }
		}
	}
	public void sendJoinedMessage(UUID clientID, boolean success) { // format: join, success or join, failure
		System.out.println("Sending joined message\n");
		try {
			String message = new String("join,");
			if (success) 
				message += "success";
			else 
				message += "failure";
			sendPacket(message, clientID);
			System.out.print("Sending " + message + " message back to " + clientID+ "\n");
	}	catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void sendCreateMessages(UUID clientID, String[] position) { // format: create, remoteId, x, y, z
		try {
			String message = new String("create," + clientID.toString());
			message += "," + position[0];
			message += "," + position[1];
			message += "," + position[2];
			forwardPacketToAll(message, clientID);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void sendDetailsMsg(UUID clientID, UUID remoteId, String[] position) {
		System.out.println("Sending details for message for: " + clientID);
		try {
			String message = new String("dsfr," + remoteId.toString());
			message += "," + position[0];
			message += "," + position[1];
			message += "," + position[2];
			sendPacket(message, clientID);
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	public void sendWantsDetailsMessages(UUID clientID) { 
		try {
			String message = new String("wsds," + clientID.toString());
			//sendPacket(message, clientID);
			forwardPacketToAll(message, clientID);
		} catch (IOException e) {
			e.printStackTrace();
		}
  
	}
	public void sendMoveMessages(UUID clientID, String[] position) { 
		System.out.println("Sending move messages to other clients");
		try {
			String message = new String("move," + clientID.toString());
			message += "," + position[0];
			message += "," + position[1];
			message += "," + position[2];
			forwardPacketToAll(message, clientID);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public void sendByeMessages(UUID clientID) { 
	// etc….. 

	}
	
	public void sendNPCinfo() { // informs clients of new NPC positions 
		for (int i=0; i<npcCtrl.getNumOfNPCs(); i++) { 
	        try { 
	        	String message = new String("mnpc," + Integer.toString(i));
	        	message += "," + (npcCtrl.getNPC(i)).getX();
	        	message += "," + (npcCtrl.getNPC(i)).getY();
	        	message += "," + (npcCtrl.getNPC(i)).getZ();
	        	//System.out.println("Sending NPC info to clients...");
	        	//System.out.println(message);
	        	sendPacketToAll(message);
	        	//. . .
	        	// also additional cases for receiving messages about NPCs, such as:
	        } catch (IOException e) {
				e.printStackTrace();
			}
	    }
	}
	
	public NPCcontroller getController() {
		return npcCtrl;
	}

	public void sendCheckForAvatarNear() {
		// TODO Auto-generated method stub
		
	}
}