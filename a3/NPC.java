package a3;

import ray.rml.Vector3;

public class NPC {
	double locX, locY, locZ; // other state info goes here (FSM)
	//Probably better as floats
	
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
    
	//. . .
    public void updateLocation() { 
    	 //. . . 
    }
    
    public Vector3 getPosition() {
		return null;
    	
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
}
