package a3;

import java.io.IOException;
import ray.networking.IGameConnection.ProtocolType;

public class NetworkingServer {
	private GameServerUDP thisUDPServer;
//	private GameServerTCP thisTCPServer;
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