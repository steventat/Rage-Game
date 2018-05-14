package myGameEngine;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import ray.rage.game.*;
import ray.rml.*;
import net.java.games.input.Event;

public class MoveRightAction extends AbstractInputAction {
	
	private SceneNode mainNode;
	
	public MoveRightAction(SceneNode node) {
		mainNode = node;
	}

	public void performAction(float arg0, Event arg1) {
		mainNode.moveRight(-0.01f);
		System.out.println("Moving right");
	}
}
