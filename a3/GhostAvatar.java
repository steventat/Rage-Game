package a3;

import ray.networking.IGameConnection.ProtocolType;	// import networking
import ray.rage.scene.Entity;
import ray.rage.scene.SceneNode;
import ray.rml.Vector3;

import java.util.UUID;								// import networking

//import graphicslib3D.Vector3D;

import java.io.IOException;							// import networking
import java.net.InetAddress;						// import networking

public class GhostAvatar { 
	private UUID id;
	private SceneNode node;
	private Entity entity;
	Vector3 position;
	
	public GhostAvatar(UUID id, Vector3 position) { 
		this.id = id;
		this.position = position;
	}
	
	// accessors and setters for id, node, entity, and position . . .
	
	public UUID getID() {
		// TODO Auto-generated method stub
		return this.id;
	}
	public void setNode(SceneNode ghostN) {
		// TODO Auto-generated method stub
		
	}
	public void setEntity(Entity ghostE) {
		// TODO Auto-generated method stub
		
	}
	

	public void setPosition(Vector3 position) {
		// TODO Auto-generated method stub
		this.position = position;
	}
}