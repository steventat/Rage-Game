package a3;

import ray.rage.scene.*;
import ray.rml.*;
import java.io.IOException;

class Player extends Moveable {
	static final String MODEL = "dolphinHighPoly.obj";
	static final float SPEED = 3f;
	static final float ANGULAR_SPEED = 30f;

	public Player(SceneManager sm) throws IOException {
		super(makeNode(sm), (Vector3f)Vector3f.createFrom(SPEED, 0f, SPEED), true, ANGULAR_SPEED);
	}

	private static SceneNode makeNode(SceneManager sm) throws IOException {
		Entity playerEntity = sm.createEntity("playerEntity", MODEL);
		SceneNode playerNode = sm.getRootSceneNode().createChildSceneNode("playerNode");
		playerNode.attachObject(playerEntity);
		return playerNode;
	}
}
