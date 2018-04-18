package a3;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import ray.rage.*;
import ray.rage.util.*;
import ray.rage.game.*;
import ray.rage.scene.*;
import ray.rage.rendersystem.*;
import ray.rage.rendersystem.Renderable.*;
import ray.rage.scene.Camera.Frustum.*;
import ray.rage.scene.controllers.*;
import ray.rml.*;
import ray.rage.rendersystem.gl4.GL4RenderSystem;
import ray.input.*;
import ray.input.action.*;
import ray.rage.asset.material.*;
import ray.rage.asset.texture.*;
import ray.rage.rendersystem.states.*;
import ray.rage.rendersystem.shader.*;
import net.java.games.input.Component.Identifier.Key;
import java.awt.event.KeyEvent;
import java.awt.geom.*;
import javax.script.*;
import java.net.InetAddress;

import ray.networking.IGameConnection.ProtocolType;	// import networking

import java.util.Iterator;
import java.util.UUID;								// import networking
import java.io.IOException;							// import networking
import java.net.InetAddress;						// import networking
import java.net.UnknownHostException;				// import networking
import java.util.Vector;

class MyGame extends VariableFrameRateGame {
	
	private static MyGame game;

	public static String MAP_TEXTURE_SCRIPT = "scripts\\map_texture.js";
	public static String MAP_FILE_SCRIPT = "scripts\\map_file.js";

	private Player player;
	private OrbitCameraController orbitCamera;
	private Camera cam;
	private Map map;
	private SceneManager sm;
	
	private String serverAddress;			// network
	private int serverPort;					// network
	private ProtocolType serverProtocol;	// network
	private ProtocolClient protClient;		// network
	private boolean isClientConnected;		// network
	private Vector<UUID> gameObjectsToRemove;	// network

	//I'll leave this static because I wouldn't want two MyGames
	public static MyGame getGame() {
		return game;
	}

	//Might make this protected to ensure people can't use it.
	protected MyGame(String serverAddr, int sPort) { 
		super();
		this.serverAddress = serverAddr;
		this.serverPort = sPort;
		this.serverProtocol = ProtocolType.UDP;
	}

	public static void main(String[] args) {
		//game = new MyGame(args[0], Integer.parseInt(args[1]));	//Needs to have assets and a3 in the same directory.
		game = new MyGame("130.86.15.156", 8000);
		//Client client;
		try {
			game.startup();
			game.run();
		} catch(Exception e) {
			e.printStackTrace(System.err);
		} finally {
			game.shutdown();
			game.exit();
		}
	}

	public SceneManager getSceneManager() {
		return sm;
	}

	public Vector3f getPlayerPosition() {
		return player.getPosition();
	}

	@Override
	protected void setupWindow(RenderSystem rs, GraphicsEnvironment ge) {
		rs.createRenderWindow(new DisplayMode(1000, 700, 24, 60), false);
	}

	@Override
	protected void setupCameras(SceneManager sm, RenderWindow rw) {
		SceneNode root = sm.getRootSceneNode();
		cam = sm.createCamera("MainCamera", Projection.PERSPECTIVE);
		rw.getViewport(0).setCamera(cam);
		cam.setRt((Vector3f)Vector3f.createFrom(1.0f, 0.0f, 0.0f));
		cam.setUp((Vector3f)Vector3f.createFrom(0.0f, 1.0f, 0.0f));
		cam.setFd((Vector3f)Vector3f.createFrom(0.0f, 0.0f, -1.0f));
		cam.setPo((Vector3f)Vector3f.createFrom(0.0f, 0.0f, 0.0f));
		cam.setMode('n');
	}

	@Override
	protected void setupScene(Engine eng, SceneManager sm) throws IOException {
		setupNetworking();
		this.sm = sm;
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine jsEngine = factory.getEngineByName("js");
		sm.getAmbientLight().setIntensity(new Color(0.5f, 0.5f, 0.5f));
		SceneNode cameraNode = sm.getRootSceneNode().createChildSceneNode("CameraNode");
		cameraNode.attachObject(cam);
		player = new Player(sm);
		map = new Map(eng, sm, readScript(jsEngine, MAP_FILE_SCRIPT), 
				readScript(jsEngine, MAP_TEXTURE_SCRIPT));
		orbitCamera = new OrbitCameraController(cameraNode, player.getNode(), cam);
		setupSkybox(eng, sm);
		ManualObject sea = ManualGraphics.makePlane("default.mtl", "default.png", Color.BLUE);
		SceneNode seaNode = sm.getRootSceneNode().createChildSceneNode("SeaNode");
		seaNode.attachObject(sea);
		seaNode.scale(9999f, 9999f, 9999f);
		seaNode.moveUp(0.05f);
	}

	private void setupSkybox(Engine eng, SceneManager sm) throws IOException {
		Configuration conf = eng.getConfiguration();
		TextureManager tm = eng.getTextureManager();
		tm.setBaseDirectoryPath(conf.valueOf("assets.skyboxes.path"));
		Texture front = tm.getAssetByPath("front.jpeg");
		Texture back = tm.getAssetByPath("back.jpeg");
		Texture left = tm.getAssetByPath("left.jpeg");
		Texture right = tm.getAssetByPath("right.jpeg");
		Texture top = tm.getAssetByPath("top.jpeg");
		Texture bottom = tm.getAssetByPath("bottom.jpeg");
		tm.setBaseDirectoryPath(conf.valueOf("assets.textures.path"));
		AffineTransform xform = new AffineTransform();
		xform.translate(0, front.getImage().getHeight());
		xform.scale(1d, -1d);
		front.transform(xform);
		back.transform(xform);
		left.transform(xform);
		right.transform(xform);
		top.transform(xform);
		bottom.transform(xform);
		SkyBox sb = sm.createSkyBox("SkyBox");
		sb.setTexture(front, SkyBox.Face.FRONT);
		sb.setTexture(back, SkyBox.Face.BACK);
		sb.setTexture(left, SkyBox.Face.LEFT);
		sb.setTexture(right, SkyBox.Face.RIGHT);
		sb.setTexture(top, SkyBox.Face.TOP);
		sb.setTexture(bottom, SkyBox.Face.BOTTOM);
		sm.setActiveSkyBox(sb);
	}

	private String readScript(ScriptEngine js, String path) {
		String result = "";
		try {
			FileReader fileReader = new FileReader(path);
			result = (String)(js.eval(fileReader));
			fileReader.close();
		}
	       	catch(FileNotFoundException e) {
			System.out.println(path + " not found");
		}
		catch(IOException e) {
			System.out.println(path + " IO error");
		}
		catch(ScriptException e) {
			System.out.println(path + " script exception");
		}
		catch(NullPointerException e) {
			System.out.println(path + " null exception");
		}
		System.out.println(result);
		return result;
	}

	@Override
	public void keyPressed(KeyEvent evt) {
		int keyCode = evt.getKeyCode();
		switch(keyCode) {
		case KeyEvent.VK_A:
			player.setRotateLeft(true);
			orbitCamera.setRotateRight(true);
			break;
		case KeyEvent.VK_D:
			player.setRotateRight(true);
			orbitCamera.setRotateLeft(true);
			break;
		case KeyEvent.VK_W:
			player.setMoveForward(true);
			break;
		case KeyEvent.VK_S:
			player.setMoveBackward(true);
			break;
		case KeyEvent.VK_F:
			orbitCamera.setMoveForward(true);
			break;
		case KeyEvent.VK_B:
			orbitCamera.setMoveBackward(true);
			break;
		case KeyEvent.VK_RIGHT:
			orbitCamera.setRotateRight(true);
			break;
		case KeyEvent.VK_LEFT:
			orbitCamera.setRotateLeft(true);
			break;
		case KeyEvent.VK_UP:
			orbitCamera.setRotateUp(true);
			break;
		case KeyEvent.VK_DOWN:
			orbitCamera.setRotateDown(true);
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent evt) {
		int keyCode = evt.getKeyCode();
		switch(keyCode) {
		case KeyEvent.VK_A:
			player.setRotateLeft(false);
			orbitCamera.setRotateRight(false);
			break;
		case KeyEvent.VK_D:
			player.setRotateRight(false);
			orbitCamera.setRotateLeft(false);
			break;
		case KeyEvent.VK_W:
			player.setMoveForward(false);
			break;
		case KeyEvent.VK_S:
			player.setMoveBackward(false);
			break;
		case KeyEvent.VK_F:
			orbitCamera.setMoveForward(false);
			break;
		case KeyEvent.VK_B:
			orbitCamera.setMoveBackward(false);
			break;
		case KeyEvent.VK_RIGHT:
			orbitCamera.setRotateRight(false);
			break;
		case KeyEvent.VK_LEFT:
			orbitCamera.setRotateLeft(false);
			break;
		case KeyEvent.VK_UP:
			orbitCamera.setRotateUp(false);
			break;
		case KeyEvent.VK_DOWN:
			orbitCamera.setRotateDown(false);
			break;
		}
	}

	@Override
	protected void update(Engine engine) {
		float elapsTime = engine.getElapsedTimeMillis();
		float seconds = elapsTime / 1000f;
		player.update(map, seconds);
		orbitCamera.update(seconds);
		processNetworking(elapsTime);
	}
	
	public void setIsConnected(boolean b) {
		isClientConnected = b;
		
	}
	
	private void setupNetworking() { 
		gameObjectsToRemove = new Vector<UUID>();
		isClientConnected = false;
		System.out.println("Setting up networking...\n");
		try { 
			protClient = new ProtocolClient(InetAddress.getByName(serverAddress), serverPort, serverProtocol, game);
		} catch (UnknownHostException e) { 
			e.printStackTrace();
		} catch (IOException e) { 
			e.printStackTrace();
		}
		if (protClient == null) { 
			System.out.println("missing protocol host"); }
		else { // ask client protocol to send initial join message
				//to server, with a unique identifier for this client
			protClient.sendJoinMessage();
		} 
	}
	
	protected void processNetworking(float elapsTime) { 
		// Process packets received by the client from the server
		if (protClient != null)
			protClient.processPackets();
			// remove ghost avatars for players who have left the game
			Iterator<UUID> it = gameObjectsToRemove.iterator();
		while(it.hasNext()) { 
			sm.destroySceneNode(it.next().toString());
		}
		gameObjectsToRemove.clear();
	}
}
