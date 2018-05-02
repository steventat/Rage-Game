package a3;

import ray.rage.scene.*;
import ray.rage.Engine;
import ray.rml.*;

class Map {
	static final String MAP_PATH = "map.png";
	static final String MAP_TEXTURE = "sand.png";

	private Tessellation map;

	public Map(Engine eng, SceneManager sm) {
		this(eng, sm, MAP_PATH, MAP_TEXTURE);
	}

	public Map(Engine eng, SceneManager sm, String mapPath, String texturePath) {
		map = sm.createTessellation("gameMap");
		map.setHeightMap(eng, mapPath);
		map.setTexture(eng, texturePath);
		SceneNode mapNode = sm.getRootSceneNode().createChildSceneNode("mapNode");
		mapNode.attachObject(map);
		mapNode.scale(100f, 100f, 100f);
	}

	public Vector3f adjustVelocity(Node node, Vector3f velocity, float seconds) {
		return velocity;
	}
}
