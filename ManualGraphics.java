package a3;

import ray.rage.scene.*;
import ray.rage.Engine;
import ray.rage.rendersystem.RenderSystem;
import ray.rage.rendersystem.Renderable.*;
import ray.rage.rendersystem.shader.*;
import ray.rage.rendersystem.states.*;
import ray.rage.asset.texture.*;
import ray.rage.asset.material.*;
import ray.rage.util.BufferUtil;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.awt.Color;

class ManualGraphics {

	private static int number; // So that each object gets a unique name

	public static ManualObject makePyramid(String materialPath, String texturePath) throws IOException {
		MyGame game = MyGame.getGame();
		Engine eng = game.getEngine();
		SceneManager sm = MyGame.getGame().getSceneManager();
		ManualObject pyramid = sm.createManualObject("Pyramid" + number++);
		ManualObjectSection pyramidSec = pyramid.createManualSection("PyramidSection");
		pyramid.setGpuShaderProgram(sm.getRenderSystem().
			getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));
		float[] vertices = new float[]
		{ -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 0.0f, //front
		1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 0.0f, 1.0f, 0.0f, //right
		1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 0.0f, 1.0f, 0.0f, //back
		-1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 0.0f, //left
		-1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, //LF
		1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f //RR
		};
		float[] texcoords = new float[]
		{ 0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
		0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
		0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
		0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
		0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
		1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f
		};
		float[] normals = new float[]
		{ 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f,
		1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f,
		0.0f, 1.0f, -1.0f, 0.0f, 1.0f, -1.0f, 0.0f, 1.0f, -1.0f,
		-1.0f, 1.0f, 0.0f, -1.0f, 1.0f, 0.0f, -1.0f, 1.0f, 0.0f,
		0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f,
		0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f
		};
		int[] indices = new int[] { 0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17 };
		FloatBuffer vertBuf = BufferUtil.directFloatBuffer(vertices);
		FloatBuffer texBuf = BufferUtil.directFloatBuffer(texcoords);
		FloatBuffer normBuf = BufferUtil.directFloatBuffer(normals);
		IntBuffer indexBuf = BufferUtil.directIntBuffer(indices);
		pyramidSec.setVertexBuffer(vertBuf);
		pyramidSec.setTextureCoordsBuffer(texBuf);
		pyramidSec.setNormalsBuffer(normBuf);
		pyramidSec.setIndexBuffer(indexBuf);
		Material mat = eng.getMaterialManager().getAssetByPath(materialPath);
		Texture tex = eng.getTextureManager().getAssetByPath(texturePath);
		TextureState texState = (TextureState)sm.getRenderSystem().
			createRenderState(RenderState.Type.TEXTURE);
		texState.setTexture(tex);
		FrontFaceState faceState = (FrontFaceState) sm.getRenderSystem().
			createRenderState(RenderState.Type.FRONT_FACE);
		pyramidSec.setMaterial(mat);
		pyramid.setDataSource(DataSource.INDEX_BUFFER);
		pyramid.setRenderState(texState);
		pyramid.setRenderState(faceState);
		eng.getMaterialManager().removeAsset(mat);
		return pyramid;
	}

	public static ManualObject makePlane(String materialPath, String texturePath, Color color) throws IOException {
		Engine eng = MyGame.getGame().getEngine();
		SceneManager sm = MyGame.getGame().getSceneManager();
		RenderSystem rs = sm.getRenderSystem();
	    ZBufferState zstate = (ZBufferState) rs.createRenderState(RenderState.Type.ZBUFFER);
	    zstate.setTestEnabled(true);
		ManualObject plane = sm.createManualObject("Plane" + number++);
		ManualObjectSection planeSec = plane.createManualSection("PlaneSection");
		plane.setGpuShaderProgram(rs.getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));
		float[] vertices = { -1f,  0f,  1f,  1f,  0f, -1f, -1f,  0f, -1f,
				     -1f,  0f,  1f,  1f,  0f,  1f,  1f,  0f, -1f };
		float[] texCoords = { 0f,  1f,  0f,  0f,  1f,  0f,
			              1f,  0f,  1f,  1f,  0f,  1f };
		float[] normals = { 0f,  1f,  0f,  0f,  1f,  0f,  0f,  1f,  0f,
			            0f,  1f,  0f,  0f,  1f,  0f,  0f,  1f,  0f };
		int[] indices = { 0, 1, 2, 3, 4, 5 };
		FloatBuffer vertBuf = BufferUtil.directFloatBuffer(vertices);
		FloatBuffer texBuf = BufferUtil.directFloatBuffer(texCoords);
		FloatBuffer normBuf = BufferUtil.directFloatBuffer(normals);
		IntBuffer indexBuf = BufferUtil.directIntBuffer(indices);
		planeSec.setVertexBuffer(vertBuf);
		planeSec.setTextureCoordsBuffer(texBuf);
		planeSec.setNormalsBuffer(normBuf);
		planeSec.setIndexBuffer(indexBuf);
		MaterialManager mm = sm.getMaterialManager();
		Material mat = mm.getAssetByPath(materialPath);
		mat.setAmbient(color);
		Texture tex = eng.getTextureManager().getAssetByPath(texturePath);
		TextureState texState = (TextureState)sm.getRenderSystem().
			createRenderState(RenderState.Type.TEXTURE);
		texState.setTexture(tex);
		plane.setDataSource(DataSource.INDEX_BUFFER);
		plane.setRenderState(zstate);
		planeSec.setRenderState(texState);
		planeSec.setMaterial(mat);
		plane.setPrimitive(Primitive.TRIANGLES);
		mm.removeAsset(mat);
		return plane;
	}

	public static ManualObject makeLine(String materialPath, String texturePath, Color color) throws IOException { 
		Engine eng = MyGame.getGame().getEngine();
		SceneManager sm = MyGame.getGame().getSceneManager();
		ManualObject line = sm.createManualObject("Line" + number++);
		ManualObjectSection lineSec = line.createManualSection("LineSection");
		line.setGpuShaderProgram(sm.getRenderSystem().
				getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));
		float[] vertices = { -9999.0f, 0.0f, 0.0f, 9999.0f, 0.0f, 0.0f };
		float[] texcoords = { 0.0f, 0.0f, 1.0f, 0.0f };
		float[] normals = { 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f };
		int[] indices = { 0, 1 };
		FloatBuffer vertBuf = BufferUtil.directFloatBuffer(vertices);
		FloatBuffer texBuf = BufferUtil.directFloatBuffer(texcoords);
		FloatBuffer normBuf = BufferUtil.directFloatBuffer(normals);
		IntBuffer indexBuf = BufferUtil.directIntBuffer(indices);
		lineSec.setVertexBuffer(vertBuf);
		lineSec.setTextureCoordsBuffer(texBuf);
		lineSec.setNormalsBuffer(normBuf);
		lineSec.setIndexBuffer(indexBuf);
		MaterialManager mm = sm.getMaterialManager();
		Material mat = mm.getAssetByPath(materialPath);
		mat.setAmbient(color);
		Texture tex = eng.getTextureManager().getAssetByPath(texturePath);
		TextureState texState = (TextureState)sm.getRenderSystem().
			createRenderState(RenderState.Type.TEXTURE);
		texState.setTexture(tex);
		line.setDataSource(DataSource.INDEX_BUFFER);
		lineSec.setRenderState(texState);
		lineSec.setMaterial(mat);
		line.setPrimitive(Primitive.LINES);
		mm.removeAsset(mat);	// To ensure the material is reloaded for each line created
		return line;
	}

	public static Entity makeSphere(String materialPath, String texturePath, Color color) throws IOException {
		Engine eng = MyGame.getGame().getEngine();
		SceneManager sm = MyGame.getGame().getSceneManager();
		Entity sphereEntity = sm.createEntity("Sphere" + number++, "sphere.obj");
		MaterialManager mm = eng.getMaterialManager();
		Material mat = mm.getAssetByPath(materialPath);
		mat.setAmbient(color);
		Texture tex = eng.getTextureManager().getAssetByPath(texturePath);
		TextureState texState = (TextureState)sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
		texState.setTexture(tex);
		sphereEntity.setMaterial(mat);
		sphereEntity.setPrimitive(Primitive.TRIANGLES);
		mm.removeAsset(mat);
		return sphereEntity;
	}
}
