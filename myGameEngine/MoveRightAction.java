package myGameEngine;

import ray.input.action.AbstractInputAction;
import ray.physics.PhysicsObject;
import ray.rage.scene.*;
import ray.rage.game.*;
import ray.rml.*;
import a3.ProtocolClient;
import net.java.games.input.Event;

public class MoveRightAction extends AbstractInputAction {
	
	private SceneNode mainNode;
	private ProtocolClient protClient;
	private boolean physOn;
	private PhysicsObject playerPhys;
	
	public MoveRightAction(SceneNode node, PhysicsObject physObj, ProtocolClient p, boolean phys) {
		mainNode = node;
		playerPhys = physObj;
		protClient = p;
		physOn = phys;
	}

	public void performAction(float arg0, Event arg1) {
		if(physOn) {
			Matrix3f dir = (Matrix3f) mainNode.getWorldRotation();
			Vector3 force = (Vector3) Vector3f.createFrom(-5.0f, 0.0f, 5.0f);
			Vector3f dirForce = (Vector3f) dir.mult(force);
			playerPhys.applyForce(dirForce.x(), dirForce.y(), dirForce.z(), 0.0f, 0.0f, 0.0f);
		}
		else {
			mainNode.moveRight(-0.01f);
			protClient.sendMoveMessage(mainNode.getWorldPosition());
		}
		System.out.println("Moving right");
		protClient.sendMoveMessage(mainNode.getWorldPosition());
	}
}
