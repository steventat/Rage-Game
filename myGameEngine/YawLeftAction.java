package myGameEngine;

import ray.input.action.AbstractInputAction;
import ray.physics.PhysicsObject;
import ray.rage.scene.*;
import ray.rage.game.*;
import ray.rml.*;
import a3.ProtocolClient;
import net.java.games.input.Event;

public class YawLeftAction extends AbstractInputAction {
	
	private SceneNode mainNode;
	private ProtocolClient protClient;
	private boolean physOn;
	private PhysicsObject playerPhys;
	
	public YawLeftAction(SceneNode node, PhysicsObject physObj, ProtocolClient p, boolean phys) {
		mainNode = node;
		playerPhys = physObj;
		protClient = p;
		physOn = phys;
	}
	
	public YawLeftAction(PhysicsObject physObj, ProtocolClient p, boolean phys) {
		playerPhys = physObj;
		protClient = p;
		physOn = phys;
	}

	public void performAction(float arg0, Event arg1) {
		if(physOn) {
			
		}
		else {
			mainNode.yaw(Degreef.createFrom(0.8f));
		}
		System.out.println("Yaw");
	}
}
