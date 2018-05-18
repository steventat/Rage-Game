package myGameEngine;

import ray.input.action.AbstractInputAction;
import ray.physics.PhysicsObject;
import ray.rage.scene.*;
import ray.rage.game.*;
import ray.rml.*;
import a3.ProtocolClient;
import net.java.games.input.Event;

public class MoveLeftAction extends AbstractInputAction {
	
	private SceneNode mainNode;
	private ProtocolClient protClient;
	private boolean physOn;
	private PhysicsObject playerPhys;
	
	public MoveLeftAction(SceneNode node, ProtocolClient p, boolean phys) {
		mainNode = node;
		protClient = p;
		physOn = phys;
	}
	
	public MoveLeftAction(PhysicsObject physObj, ProtocolClient p, boolean phys) {
		playerPhys = physObj;
		protClient = p;
		physOn = phys;
	}

	public void performAction(float arg0, Event arg1) {
		if(physOn) {
			
		}
		else {
			mainNode.moveLeft(-0.01f);
			protClient.sendMoveMessage(mainNode.getWorldPosition());
		}
		System.out.println("Moving left");
	}
}
