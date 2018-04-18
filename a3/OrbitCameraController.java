package a3;

import ray.rage.scene.*;
import ray.rml.*;

class OrbitCameraController extends ControlledEntity {
	public static final float AZIMUTH = 1f;
	public static final float ELEVATION = 3f;
	public static final float RADIUS = 2f;
	public static final float ZOOM_SPEED = 1f;
	public static final float ANGULAR_SPEED = Player.ANGULAR_SPEED;


	private SceneNode orbiter;
	private SceneNode orbitee;
	private Camera cam;
	private float azimuth;
	private float elevation;
	private float radius;
	private float zoomSpeed;
	private float angularSpeed;

	public OrbitCameraController(SceneNode orbiter, SceneNode orbitee, Camera cam) {
		this(orbiter, orbitee, cam, AZIMUTH, ELEVATION, RADIUS, ZOOM_SPEED, ANGULAR_SPEED);
	}

	public OrbitCameraController(SceneNode orbiter, SceneNode orbitee, Camera cam, float azimuth, float elevation, float radius, float zoomSpeed, float angularSpeed) {
		this.orbiter = orbiter;
		this.orbitee = orbitee;
		this.cam = cam;
		this.azimuth = azimuth;
		this.elevation = elevation;
		this.radius = radius;
		this.zoomSpeed = zoomSpeed;
		this.angularSpeed = angularSpeed;
	}

	public void attachCamera(Camera cam) {
		this.cam = cam;
		orbiter.attachObject(cam);
	}

	public void detachCamera() {
		orbiter.detachObject(cam);
		cam = null;
	}

	public float getAzimuth() {
		return azimuth;
	}

	public void setAzimuth(float azimuth) {
		this.azimuth = azimuth;
	}

	public float getElevation() {
		return elevation;
	}

	public void setElevation(float elevation) {
		this.elevation = elevation;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public void rotateLeft(float degrees) {
		azimuth -= degrees;
	}

	public void rotateRight(float degrees) {
		azimuth += degrees;
	}

	public void rotateUp(float degrees) {
		elevation += degrees;
	}

	public void rotateDown(float degrees) {
		elevation -= degrees;
	}

	public void moveCloser(float dist) {
		radius -= dist;
	}

	public void moveFurther(float dist) {
		radius += dist;
	}

	public void update(float seconds) {
		if(cam != null) {
			if(shouldMoveForward())
				moveCloser(zoomSpeed * seconds);
			else if(shouldMoveBackward())
				moveFurther(zoomSpeed * seconds);
			if(shouldRotateLeft())
				rotateLeft(angularSpeed * seconds);
			else if(shouldRotateRight())
				rotateRight(angularSpeed * seconds);
			if(shouldRotateUp())
				rotateUp(angularSpeed * seconds);
			else if(shouldRotateDown())
				rotateDown(angularSpeed * seconds);
			double theta = Math.toRadians(azimuth);
			double phi = Math.toRadians(elevation);
			float x = (float)(radius * Math.cos(phi) * Math.sin(theta));
			float y = (float)(radius * Math.sin(phi));
			float z = (float)(radius * Math.cos(phi) * Math.cos(theta));
			orbiter.setLocalPosition(Vector3f.createFrom(x, y, z).add(orbitee.getWorldPosition()));
			orbiter.lookAt(orbitee, orbiter.getParent().getLocalUpAxis());
		}
	}
}
