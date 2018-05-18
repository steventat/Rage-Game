package a3;

import ray.rage.scene.Entity;
import ray.rage.scene.SceneNode;
import ray.rml.Vector3;

public class NPC {
	//Probably better as floats
	double locX, locY, locZ; // other state info goes here (FSM)
	private int id;
	private SceneNode node;
	private Entity entity;
    private Vector3 position;
	
	public NPC(double x, double y, double z) {
		locX = x;
		locY = y;
		locZ = z;
	}
	
	public double getX() { 
		return locX; 
	}
	public double getY() { 
		return locY; 
	}
	public double getZ() { 
		return locZ; 
	}
	
	public void setX(double x) {
		locX = locX + x;
	}
	
	protected void setID(int newID) {
		id = newID;
	}
	
	public int getID() {
		return id;
	}
    
	//. . .
    public void updateLocation() { 
    }
    

	public void goWalk() {
		// TODO Auto-generated method stub
		
	}

	public double getSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void getSmall() {
		// TODO Auto-generated method stub
		
	}

	public void goBig() {
		// TODO Auto-generated method stub
		
	}
	
	public void setPosition(Vector3 position) { 
   	 node.setLocalPosition(position);
   }
     
   public Vector3 getPosition() { 
	   return position;
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
