package myGameEngine;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import ray.rage.game.*;
import ray.rml.*;
import net.java.games.input.Event;

public class PitchForwardAction extends AbstractInputAction {
	
	private SceneNode mainNode;
	
	public PitchForwardAction(SceneNode node) {
		mainNode = node;
	}

	public void performAction(float arg0, Event arg1) {
		mainNode.pitch(null);
		System.out.println("Pitch Forward");
	}
}
