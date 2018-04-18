package a3;

import ray.rage.scene.*;
import ray.rml.*;

class Moveable extends ControlledEntity {
	private SceneNode node;
	private Vector3f speed;
	private float angularSpeed;
	private boolean collider;

	public Moveable(SceneNode node, Vector3f speed, boolean isCollider, float angularSpeed) {
		this.node = node;
		this.speed = speed;
		this.angularSpeed = angularSpeed;
		collider = isCollider;
	}

	private void addVelocity(Vector3f velocity, float seconds) {
		Vector3f pos = getPosition();
		node.setLocalPosition(pos.x() + velocity.x() * seconds, 
				pos.y() + velocity.y() * seconds, 
				pos.z() + velocity.z() * seconds);
	}

	private void addAngularVelocity(float angularVelocity, float seconds) {
		node.yaw(Degreef.createFrom(angularVelocity * seconds));
	}

	public SceneNode getNode() {
		return node;
	}

	public Vector3f getPosition() {
		return (Vector3f)node.getLocalPosition();
	}

	public boolean isCollider() {
		return collider;
	}

	public void setCollider(boolean isCollider) {
		collider = isCollider;
	}

	private Vector3f getFVector(float speed) {
		Vector3f axis = (Vector3f)node.getWorldForwardAxis();
		return (Vector3f)Vector3f.createFrom(axis.x() * speed, axis.y() * speed, axis.z() * speed);
	}

	private Vector3f getUVector(float speed) {
		Vector3f axis = (Vector3f)node.getWorldUpAxis();
		return (Vector3f)Vector3f.createFrom(axis.x() * speed, axis.y() * speed, axis.z() * speed);
	}

	private Vector3f getRVector(float speed) {
		Vector3f axis = (Vector3f)node.getWorldRightAxis();
		return (Vector3f)Vector3f.createFrom(axis.x() * speed, axis.y() * speed, axis.z() * speed);
	}


	public void update(Map map, float seconds) {
		Vector3f velocity = (Vector3f)Vector3f.createFrom(0f, 0f, 0f);
		float angularVelocity = 0f;
		if(shouldMoveLeft())
			velocity = getRVector(-speed.x());
		else if(shouldMoveRight())
			velocity = getRVector(speed.x());
		if(shouldMoveForward())
			velocity = getFVector(speed.z());
		else if(shouldMoveBackward())
			velocity = getFVector(-speed.z());
		if(shouldRotateLeft())
			angularVelocity = angularSpeed;
		else if(shouldRotateRight())
			angularVelocity = -angularSpeed;
		if(collider)
			velocity = map.adjustVelocity(node, velocity, seconds);
		addVelocity(velocity, seconds);
		addAngularVelocity(angularVelocity, seconds);
	}
}
