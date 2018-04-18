package a3;

import ray.networking.IGameConnection.ProtocolType;	// import networking
import ray.rage.scene.Entity;
import ray.rage.scene.SceneManager;
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
	private MyGame game = MyGame.getGame();
	private SceneManager sm = game.getSceneManager();
	static final String MODEL = "dolphinHighPoly.obj";
	
	
	public GhostAvatar(UUID id, Vector3 position) { 
		this.id = id;
		this.position = position;
		try {
			this.setEntity(sm.createEntity("ghostEntity" + game.getNumGhosts() + 1, MODEL));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.setNode(sm.getRootSceneNode().createChildSceneNode("ghostNode" + game.getNumGhosts() + 1));
	}
	
	// accessors and setters for id, node, entity, and position . . .
	
	public UUID getID() {
		return this.id;
	}
	public void setNode(SceneNode ghostN) {
		node = ghostN;
		node.attachObject(entity);
		
	}
	public void setEntity(Entity ghostE) {
		entity = ghostE;
	}
	
	public SceneNode getNode( ) {
		return node;
	}

	public Entity getEntity() {
		return entity;
	}
	public Vector3 getPosition(Vector3 position) {
		return position;
	}
}