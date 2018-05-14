package myGameEngine;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import ray.rage.game.*;
import ray.rml.*;
import net.java.games.input.Event;

public class YawLeftAction extends AbstractInputAction {
	
	private SceneNode mainNode;
	
	public YawLeftAction(SceneNode node) {
		mainNode = node;
	}

	public void performAction(float arg0, Event arg1) {
		mainNode.yaw(Degreef.createFrom(0.8f));
		System.out.println("Yaw");
	}
}
