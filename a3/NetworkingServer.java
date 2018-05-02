package a3;

import java.io.IOException;
import ray.networking.IGameConnection.ProtocolType;

public class NetworkingServer {
	private GameServerUDP thisUDPServer;
//	private GameServerTCP thisTCPServer;
	
	/*private NPCcontroller npcCtrl;
	//GameAIServerTCP tcpServer;
	GameAIServerUDP udpServer;*/
	
	public NetworkingServer(int serverPort, String protocol) { 
		try { 
//			if(protocol.toUpperCase().compareTo("TCP") == 0) { 	// TCP NOT ADDED
//				thisTCPServer = new GameServerTCP(serverPort);
//			}
//			else { 
				thisUDPServer = new GameServerUDP(serverPort);
//			}
		}
		catch (IOException e) { 
			e.printStackTrace();
		} 
	}

	 /*public TestNetworkingServer(int id) // constructor
	 { startTime = System.nanoTime();
	 lastUpdateTime = startTime;
	 npcCtrl = new NPCcontroller();
	 . . .
	 // start networking TCP server (as before)
	 . . .
	 // start NPC control loop
	 npcCtrl.setupNPCs();
	 npcLoop();
	 }
	 public void npcLoop() // NPC control loop
	 { while (true)
	 { long frameStartTime = System.nanoTime();
	 float elapMilSecs = (frameStartTime-lastUpdateTime)/(1000000.0f);
	 if (elapMilSecs >= 50.0f)
	 { lastUpdateTime = frameStartTime;
	 npcCtrl.updateNPCs();
	 tcpServer.sendNPCinfo();
	 }
	 Thread.yield();*/
	
	public static void main(String[] args) { 
		//if(args.length > 1) { 
			//NetworkingServer app = new NetworkingServer(Integer.parseInt(args[0]), args[1]);
			NetworkingServer app = new NetworkingServer(8000, "UDP");
			while(true) {
				//System.out.println("Running server");
			}
		//} 
	} 
}