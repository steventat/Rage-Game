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

import static ray.rage.scene.SkeletalEntity.EndType.*;
import java.util.Vector;

import ray.physics.PhysicsEngine;           // import physics
import ray.physics.PhysicsObject;           // import physics
import ray.physics.PhysicsEngineFactory;    // import physics
 

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

    private SceneNode ball1Node, ball2Node, groundNode; // physics
    private SceneNode cameraPositionNode;               //  physics
    private final static String GROUND_E = "Ground";    //  physics
    private final static String GROUND_N = "GroundNode";//  physics
    private PhysicsEngine physicsEngine;                    //  physics
    private PhysicsObject ball1PhysObj, ball2PhysObj, groundPlaneP; // physics
    private boolean running = false;                    //  physics
     
	
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
		game = new MyGame("130.86.65.78", 8000);
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
		
		/*ManualObject sea = ManualGraphics.makePlane("default.mtl", "default.png", Color.BLUE);
		SceneNode seaNode = sm.getRootSceneNode().createChildSceneNode("SeaNode");
		seaNode.attachObject(sea);
		seaNode.scale(9999f, 9999f, 9999f);
		seaNode.moveUp(0.05f);*/
		//Entity wall = sm.createEntity("brickwall", "wall.obj");
		/*SceneNode wallNode = sm.getRootSceneNode().createChildSceneNode("wallNode");
		wallNode.scale(0.1f, 0.1f, 0.1f);
		RenderSystem rs = sm.getRenderSystem();
		Texture tex = sm.getTextureManager().getAssetByPath("WornBrownBrickwork_1024.png");
		TextureState tstate = (TextureState) sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
		tstate.setTexture(tex);
		wall.setRenderState(tstate);
		wallNode.attachObject(wall);*/
		
		SkeletalEntity man4Entity = sm.createSkeletalEntity("man4", "man4.rkm", "man4.rks");
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
		
		
		man4Entity.playAnimation("man4_walk", 0.5f, LOOP, 0);
		
		
        // physics
        SceneNode rootNode = sm.getRootSceneNode();         
        // Ball 1
        Entity ball1Entity = sm.createEntity("ball1", "earth.obj");
        ball1Node = rootNode.createChildSceneNode("Ball1Node");
        ball1Node.attachObject(ball1Entity);
        //ball1Node.setLocalPosition(0, 2, -2);  // original position
        ball1Node.setLocalPosition(0, 3, -2);
        // Ball 2
        Entity ball2Entity = sm.createEntity("Ball2", "cone.obj"); // cone.obj as 2nd ball
        ball2Node = rootNode.createChildSceneNode("Ball2Node");
        ball2Node.attachObject(ball2Entity);
        //ball2Node.setLocalPosition(-1,10,-2); // original position 
        ball2Node.setLocalPosition(-1,2,-2); 
        // Ground plane
        Entity groundEntity = sm.createEntity(GROUND_E, "cube.obj");
        groundNode = rootNode.createChildSceneNode(GROUND_N);
        groundNode.attachObject(groundEntity);
        //groundNode.setLocalPosition(0, -7, -2); // original position
        groundNode.setLocalPosition(0, 0, 0);       // set ground to xyz to 0
        initPhysicsSystem();
        createRagePhysicsWorld();
         
        System.out.println("Press P to start the physics engine!");
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
			//this.doNWalk();
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
		case KeyEvent.VK_P:                     // Press 'P' to enable Physics
            System.out.println("Starting Physics!");
            running = true;
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
			this.doNWalk();
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
		case KeyEvent.VK_SPACE:
			this.doAttack();
			break;
		}
	}
	
	private void doAttack() { 
		SkeletalEntity manSE = (SkeletalEntity) game.getEngine().getSceneManager().getEntity("walker");
		//SkeletalEntity manSE = (SkeletalEntity) game.getEngine().getSceneManager().getEntity("man4");
		manSE.playAnimation("attack", 0.5f, STOP, 0);
		//manSE.stopAnimation();
		//manSE.playAnimation("man4_hit", 0.5f, LOOP, 0);
		
	}
	
	private void doNWalk() { 
		SkeletalEntity manSE = (SkeletalEntity) game.getEngine().getSceneManager().getEntity("walker");
		//SkeletalEntity manSE = (SkeletalEntity) game.getEngine().getSceneManager().getEntity("man4");
		manSE.stopAnimation();
		manSE.playAnimation("normal_walk", 0.5f, STOP, 0);
		//manSE.playAnimation("man4_walk", 0.5f, LOOP, 0);
	}

	@Override
	protected void update(Engine engine) {
		float elapsTime = engine.getElapsedTimeMillis();
		float seconds = elapsTime / 1000f;
		player.update(map, seconds);
		orbitCamera.update(seconds);
		processNetworking(elapsTime);
		
		//Updating skeletalentity animations
		SkeletalEntity playerEntity = (SkeletalEntity) engine.getSceneManager().getEntity("walker");
		SkeletalEntity manEntity = (SkeletalEntity) game.getEngine().getSceneManager().getEntity("man4");
		playerEntity.update();
		manEntity.update();

        // physics
        if (running)    
        { 
            Matrix4 mat;
            physicsEngine.update(elapsTime);
            for (SceneNode s : engine.getSceneManager().getSceneNodes())
            { 
                if (s.getPhysicsObject() != null)
                { 
                    mat = Matrix4f.createFrom(toFloatArray(s.getPhysicsObject().getTransform()));
                s.setLocalPosition(mat.value(0,3),mat.value(1,3),mat.value(2,3));
                } 
            } 
        } 
		
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
	
	public int getNumGhosts() {
		if (protClient != null)
			return protClient.getNumGhosts();
		else
			System.out.print("Not connected");
			return 0;
	}
	
	public void addGhostAvatarToGameWorld(GhostAvatar avatar) throws IOException { 
		if (avatar != null) { 
			Entity ghostE = sm.createEntity("ghost", "dolphinHighPoly.obj");
			ghostE.setPrimitive(Primitive.TRIANGLES);
			SceneNode ghostN = sm.getRootSceneNode().
			createChildSceneNode(avatar.getID().toString());
			ghostN.attachObject(ghostE);
			ghostN.setLocalPosition(avatar.getPosition());
			avatar.setNode(ghostN);
			avatar.setEntity(ghostE);
			//avatar.setPosition(node’s position... maybe redundant);
		}
	}
	
	public void removeGhostAvatarFromGameWorld(GhostAvatar avatar) { 
		if(avatar != null) gameObjectsToRemove.add(avatar.getID());
	}
	
	   private void initPhysicsSystem()
	    { 
	        String engine = "ray.physics.JBullet.JBulletPhysicsEngine";
	        float[] gravity = {0, -3f, 0};
	        physicsEngine = PhysicsEngineFactory.createPhysicsEngine(engine);
	        physicsEngine.initSystem();
	        physicsEngine.setGravity(gravity);
	    }
	 
	    private void createRagePhysicsWorld()
	    { 
	        float mass = 1.0f;
	        float up[] = {0,1,0};
	        double[] temptf;
	         
	        temptf = toDoubleArray(ball1Node.getLocalTransform().toFloatArray());
	        ball1PhysObj = physicsEngine.addSphereObject(physicsEngine.nextUID(),mass, temptf, 2.0f);
	        ball1PhysObj.setBounciness(1.0f);
	        ball1Node.setPhysicsObject(ball1PhysObj);
	        temptf = toDoubleArray(ball2Node.getLocalTransform().toFloatArray());
	        ball2PhysObj = physicsEngine.addSphereObject(physicsEngine.nextUID(),mass, temptf, 2.0f);
	        ball2PhysObj.setBounciness(1.0f);
	        ball2Node.setPhysicsObject(ball2PhysObj);
	        temptf = toDoubleArray(groundNode.getLocalTransform().toFloatArray());
	        groundPlaneP = physicsEngine.addStaticPlaneObject(physicsEngine.nextUID(),temptf, up, 0.0f);
	        groundPlaneP.setBounciness(1.0f);
	        groundNode.scale(3f, .05f, 3f);
	        groundNode.setLocalPosition(0, -7, -2);
	        groundNode.setPhysicsObject(groundPlaneP);
	        // can also set damping, friction, etc.
	    }
	 
	 
	 
	    private float[] toFloatArray(double[] arr)
	    { 
	        if (arr == null) 
	            return null;
	        int n = arr.length;
	        float[] ret = new float[n];
	        for (int i = 0; i < n; i++)
	        { 
	            ret[i] = (float)arr[i];
	        }
	        return ret;
	    }
	 
	    private double[] toDoubleArray(float[] arr)
	    { 
	        if (arr == null) 
	            return null;
	        int n = arr.length;
	        double[] ret = new double[n];
	        for (int i = 0; i < n; i++)
	        { 
	            ret[i] = (double)arr[i];
	        }
	        return ret;
	    }
}


/*private class SendCloseConnectionPacketAction extends AbstractInputAction { // for leaving the game... need to attach to an input device
	
	@Override
	public void performAction(float time, Event evt) { 
		if(protClient != null && isClientConnected == true) { 
			protClient.sendByeMessage();
		} 
	}
}*/
