package a3;

import ray.rage.asset.texture.Texture;
import ray.rage.rendersystem.RenderSystem;
import ray.rage.rendersystem.Renderable.*;
import ray.rage.rendersystem.shader.*;
import ray.rage.rendersystem.states.*;
import ray.rage.scene.*;
import ray.rml.*;
import java.io.IOException;

import static ray.rage.scene.SkeletalEntity.EndType.*;

class Player extends Moveable {
	//static final String MODEL = "dolphinHighPoly.obj";
	//static final String MODEL = "walker.obj";
	static final float SPEED = 3f;
	static final float ANGULAR_SPEED = 30f;

	public Player(SceneManager sm) throws IOException {
		super(makeNode(sm), (Vector3f)Vector3f.createFrom(SPEED, 0f, SPEED), true, ANGULAR_SPEED);
	}

	private static SceneNode makeNode(SceneManager sm) throws IOException {
		//Entity playerEntity = sm.createEntity("playerEntity", MODEL);
		
		SkeletalEntity playerEntity = sm.createSkeletalEntity("walker", "walker.rkm", "walker.rks");
		SceneNode playerNode = sm.getRootSceneNode().createChildSceneNode("playerNode");
		playerNode.moveUp(0.1f);
		playerNode.scale(0.1f, 0.1f, 0.1f);
		playerNode.attachObject(playerEntity);
		playerEntity.loadAnimation("normal_walk", "normal_walk.rka");
		playerEntity.loadAnimation("attack", "attack.rka");
		RenderSystem rs = sm.getRenderSystem();
		Texture tex = sm.getTextureManager().getAssetByPath("walker.png");
		TextureState tstate = (TextureState) sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
		tstate.setTexture(tex);
		playerEntity.setRenderState(tstate);
		return playerNode;
		
		/*SkeletalEntity man4Entity = sm.createSkeletalEntity("man4", "man4.rkm", "man4.rks");
		SceneNode man4Node = sm.getRootSceneNode().createChildSceneNode("man4Node");
		man4Node.moveUp(0.1f);
		man4Node.scale(0.1f, 0.1f, 0.1f);
		man4Node.attachObject(man4Entity);
		man4Entity.loadAnimation("man4_walk", "man4_walk.rka");
		man4Entity.loadAnimation("man4_hit", "man4_hit.rka");
		RenderSystem rs = sm.getRenderSystem();
		Texture tex = sm.getTextureManager().getAssetByPath("man4.png");
		TextureState tstate = (TextureState) sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
		tstate.setTexture(tex);
		man4Entity.setRenderState(tstate);
		return man4Node;*/
	}
	
	
	
	
}
