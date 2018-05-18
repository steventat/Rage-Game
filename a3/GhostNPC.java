package a3;

import java.util.UUID;

import ray.rage.scene.Entity;
import ray.rage.scene.SceneNode;
import ray.rml.Vector3;

public class GhostNPC { 
     private int id;
     private SceneNode node;
     private Entity entity;
     private Vector3 position;
     
    public GhostNPC(int id, Vector3 position) { // constructor 
        this.id = id;
        this.position = position;
    }
     
    public void setPosition(Vector3 position) { 
    	 node.setLocalPosition(position);
    }
      
    public Vector3 getPosition() { 
    	return position;
    }
     
    public int getID() {
 		return this.id;
 	}
 	public void setNode(SceneNode ghostN) {
 		node = ghostN;
 		
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
 	public void setLocalPosition(Vector3 position) {
 		node.setLocalPosition(position.x(), position.y(), position.z());
 	}
 	
}
