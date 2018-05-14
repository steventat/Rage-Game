package myGameEngine;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import ray.rage.game.*;
import ray.rml.*;
import a3.ProtocolClient;
import net.java.games.input.Event;

public class MoveLeftAction extends AbstractInputAction {
	
	private SceneNode mainNode;
	private ProtocolClient protClient;
	
	public MoveLeftAction(SceneNode node, ProtocolClient p) {
		mainNode = node;
		protClient = p;
	}

	public void performAction(float arg0, Event arg1) {
		mainNode.moveLeft(-0.01f);
		protClient.sendMoveMessage(mainNode.getWorldPosition());
		System.out.println("Moving left");
	}
}
