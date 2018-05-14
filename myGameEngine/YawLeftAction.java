package myGameEngine;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import ray.rage.game.*;
import ray.rml.*;
import a3.ProtocolClient;
import net.java.games.input.Event;

public class YawLeftAction extends AbstractInputAction {
	
	private SceneNode mainNode;
	private ProtocolClient protClient;
	
	public YawLeftAction(SceneNode node, ProtocolClient p) {
		mainNode = node;
		protClient = p;
	}

	public void performAction(float arg0, Event arg1) {
		mainNode.yaw(Degreef.createFrom(0.8f));
		System.out.println("Yaw");
	}
}
