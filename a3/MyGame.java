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

import java.util.Scanner;		// get user input from command line

import myGameEngine.*;

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
 
import ray.audio.*;							// import audio
import com.jogamp.openal.ALFactory;			// import audio

import javax.script.ScriptEngine;			// import script
import javax.script.ScriptEngineFactory;	// import script
import javax.script.ScriptEngineManager;	// import script
import javax.script.ScriptException;		// import script


class MyGame extends VariableFrameRateGame {
	
	private static MyGame game;

	public static String MAP_TEXTURE_SCRIPT = "scripts\\map_texture.js";	// script for map texture
	public static String MAP_FILE_SCRIPT = "scripts\\map_file.js";			// script for map file
	public static String CONFIG_SCRIPT = "scripts\\config.js";				// script for configuration

	String configScript;						// script after reading
	
	private Player player;
	private OrbitCameraController orbitCamera;
	private Camera cam;
	private Map map;
	private SceneManager sm;
	
	//Action Classes and Input Manager
	private InputManager im;
    //private Action quitGameAction;
	private MoveForwardAction dMoveF;
	private MoveBackwardAction dMoveB;
	private MoveLeftAction dMoveL;
	private MoveRightAction dMoveR;
	private YawLeftAction dYawL;
	private YawRightAction dYawR;
	
	//May not need this because we have OrbitCameraController. 
	//private Camera3PController orbitController, orbitController2;
	
	private String serverAddress;			// network
	private int serverPort;					// network
	private ProtocolType serverProtocol;	// network
	private ProtocolClient protClient;		// network
	private boolean isClientConnected;		// network
	private Vector<UUID> gameObjectsToRemove;	// network
	private Vector<GhostAvatar> ghostAvatarList; //network
	private Vector<GhostNPC> ghostNPCList; //AI

    private SceneNode earthNode, coneNode, groundNode; // physics
    private SceneNode cameraPositionNode;               //  physics
    private final static String GROUND_E = "Ground";    //  physics
    private final static String GROUND_N = "GroundNode";//  physics
    private PhysicsEngine physicsEngine;                    //  physics
    private PhysicsObject earthPhysObj,conePhysObj, groundPlaneP; // physics
    private boolean running = false;                    //  physics
    
    private static SceneNode playerNode;
    private PhysicsObject playerPhysObj; 
    
    private SceneNode robotNode; // set to gloabl for sound
    
    private IAudioManager audioMgr;					// sound
    private Sound oceanSound, hereSound;			// sound     
	
    private int maxscore;							// maxscore read from JavaScript file
    
    
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

		System.out.print("Enter Networking Server IP Address: "); 
/*		Scanner in 	= new Scanner (System.in);			// input IP address
		game = new MyGame(in.next(), 8000);		
*/		game = new MyGame("130.86.65.78", 8000);  // hardcored IP

		
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
	
	public static void setPlayerNode(SceneNode node) {
		playerNode = node;
	}

	@Override
	protected void setupWindow(RenderSystem rs, GraphicsEnvironment ge) {
		rs.createRenderWindow(new DisplayMode(1000, 700, 24, 60), false);
		//rs.createRenderWindow(new DisplayMode(1000, 700, 24, 60), true);
		//rs.createRenderWindow(true);
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
		im = new GenericInputManager();	//Initializing input manager for controllers

		// Java Script
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine jsEngine = factory.getEngineByName("js");		
		
		this.sm = sm;
		
        // ambient light
		//sm.getAmbientLight().setIntensity(new Color(0.5f, 0.5f, 0.5f));
		sm.getAmbientLight().setIntensity(new Color(.1f, .1f, .1f));
		
		// Positional Light
        Light plight = sm.createLight("testLamp1", Light.Type.POINT);
        plight.setAmbient(new Color(.3f, .3f, .3f));
        plight.setDiffuse(new Color(.7f, .7f, .7f));
        plight.setSpecular(new Color(1.0f, 1.0f, 1.0f));
        plight.setRange(5f);
        SceneNode plightNode = sm.getRootSceneNode().createChildSceneNode("plightNode");
        plightNode.attachObject(plight);

        // Spot Light
        Light spotLight = sm.createLight("spotLight",  Light.Type.SPOT);
        spotLight.setAmbient(new Color(.4f, .3f, .5f));
        spotLight.setDiffuse(new Color(.7f, .3f, .5f));
        spotLight.setSpecular(new Color(1.0f, 1.0f, 1.0f));
        spotLight.setRange(3f);
        SceneNode spotLightNode = sm.getRootSceneNode().createChildSceneNode("spotLightNode");
        spotLightNode.attachObject(spotLight);
        
		SceneNode cameraNode = sm.getRootSceneNode().createChildSceneNode("CameraNode");
		cameraNode.attachObject(cam);
		
//		LightManager lightMggr = new LightManager(this);
//		lightMgr.putLightSpotFocusOnNode(sm.getSceneNode("hatoflifeNode"), "L1", new Color(75,72,25));
//		LightMgr.putLightSpotFocusOnNode(sm.getSceneNode("hatofLifeNode"), "L2", new Color(255, 55, 35));
		
		//Initialize Player
		player = new Player(sm);
		
		// Load map
		map = new Map(eng, sm, readScript(jsEngine, MAP_FILE_SCRIPT), 
				readScript(jsEngine, MAP_TEXTURE_SCRIPT));
		
		// Load configuration file
		configScript = readScript(jsEngine, CONFIG_SCRIPT);		
		maxscore = (int) jsEngine.get("maxscore");
		System.out.println("MAXSCORE: " + maxscore);		// print to command line the maxscore
		
		
		//Initialize Orbit Camera
		orbitCamera = new OrbitCameraController(cameraNode, player.getNode(), cam);
		setupSkybox(eng, sm);
		
		//Initializing actions and connecting to nodes.
		//SceneNode playerN = sm.getSceneNode("playerNode");
		
        dMoveF = new MoveForwardAction(playerNode, protClient);
        dMoveB = new MoveBackwardAction(playerNode, protClient);
        dMoveL = new MoveLeftAction(playerNode, protClient);
        dMoveR = new MoveRightAction(playerNode, protClient);
        dYawL = new YawLeftAction(playerNode, protClient);
        dYawR = new YawRightAction(playerNode, protClient);
		setupInputs(sm);
		
		//Creating the sea
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
		man4Node.moveLeft(2.0f);
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
		
		SkeletalEntity robotEntity = sm.createSkeletalEntity("robot", "robot.rkm", "robot.rks");
		robotNode = sm.getRootSceneNode().createChildSceneNode("robotNode");  
		robotNode.moveUp(0.5f);
		robotNode.moveRight(3.0f);
		robotNode.scale(0.1f, 0.1f, 0.1f);
		robotNode.attachObject(robotEntity);
		robotEntity.loadAnimation("robot_walk", "robot_walk.rka");
		robotEntity.loadAnimation("robot_fly", "robot_fly.rka");
		robotEntity.loadAnimation("robot_tPlane", "robot_transformPlane.rka");
		robotEntity.loadAnimation("robot_tRobot", "robot_transformRobot.rka");
		Texture robotText = sm.getTextureManager().getAssetByPath("robot.png");
		TextureState rTstate = (TextureState) sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
		rTstate.setTexture(robotText);
		robotEntity.setRenderState(rTstate);
		
		//robotEntity.playAnimation("robot_walk", 0.5f, STOP, 0);
		robotEntity.playAnimation("robot_tPlane", 0.5f, LOOP, 0);
		//robotEntity.playAnimation("robot_fly", 0.5f, LOOP, 0);
		//robotEntity.playAnimation("robot_tRobot", 0.5f, STOP, 0);
		
		
        // physics
        SceneNode rootNode = sm.getRootSceneNode();         
        
        // Ball 1
        Entity earthEntity = sm.createEntity("earth", "earth.obj");
        earthNode = rootNode.createChildSceneNode("earthNode");
        earthNode.attachObject(earthEntity);
        earthNode.setLocalPosition(0, 2, -2);  // original position
        //earthNode.setLocalPosition(0, 3, -2);
        
        // Ball 2
        Entity ball2Entity = sm.createEntity("cone", "cone.obj"); // cone.obj as 2nd ball
        coneNode = rootNode.createChildSceneNode("coneNode");
        coneNode.attachObject(ball2Entity);
        coneNode.setLocalPosition(-1,10,-2); // original position 
        //coneNode.setLocalPosition(-1,2,-2); 
        
        // Ground plane       
        Entity groundEntity = sm.createEntity(GROUND_E, "cube.obj");
        groundNode = rootNode.createChildSceneNode(GROUND_N);
        groundNode.attachObject(groundEntity);
        //groundNode.setLocalPosition(0, -7, -2); // original position
        groundNode.setLocalPosition(0, 0, 0);       // set ground to xyz to 0
        initPhysicsSystem();
        createRagePhysicsWorld();
         
        System.out.println("Press P to start the physics engine!");
        
        initAudio(sm);	// SOUND
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
	
	//Setting up Gamepads and Controolers
	protected void setupInputs(SceneManager sm) { 
    	String kbName = im.getKeyboardName();
	    //String gpName = im.getFirstGamepadName();
	    //SceneNode playerNode = getEngine().getSceneManager().getSceneNode("playerNode");
	    try {
		    im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.W, dMoveF, 
		    		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		    im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.S, dMoveB, 
		    		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		    im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.A, dMoveL, 
		    		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		    im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.D, dMoveR, 
		    		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		    im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.LEFT, dYawL, 
		    		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		    im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.RIGHT, dYawR, 
		    		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
	    } catch (RuntimeException re) {
	    	System.out.println("No controller plugged in");
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
		im.update(elapsTime);	//Need to update input manager for controller inputs
		orbitCamera.update(seconds);
		processNetworking(elapsTime);
		
		//Updating skeletalentity animations
		SkeletalEntity playerEntity = (SkeletalEntity) engine.getSceneManager().getEntity("walker");
		SkeletalEntity manEntity = (SkeletalEntity) game.getEngine().getSceneManager().getEntity("man4");
		SkeletalEntity robotEntity = (SkeletalEntity) game.getEngine().getSceneManager().getEntity("robot");
		playerEntity.update();
		manEntity.update();
		robotEntity.update();

        // physics
        if (running)    
        { 
            Matrix4 mat;
            physicsEngine.update(elapsTime);
            for (SceneNode s : engine.getSceneManager().getSceneNodes()) { 
                if (s.getPhysicsObject() != null)
                { 
                    mat = Matrix4f.createFrom(toFloatArray(s.getPhysicsObject().getTransform()));
                s.setLocalPosition(mat.value(0,3),mat.value(1,3),mat.value(2,3));
                } 
            } 
        } 
        
        // sound
		hereSound.setLocation(robotNode.getWorldPosition());	
		oceanSound.setLocation(earthNode.getWorldPosition());	
		setEarParameters(sm);
		
	}
	
	public void setIsConnected(boolean b) {
		isClientConnected = b;
		
	}
	
	private void setupNetworking() { 
		gameObjectsToRemove = new Vector<UUID>();
		ghostAvatarList = new Vector<GhostAvatar>();
		ghostNPCList = new Vector<GhostNPC>();
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
			ghostAvatarList.add(avatar);
			//avatar.setPosition(node’s position... maybe redundant);
		}
	}
	
	public void addGhostNPCtoGameWorld(GhostNPC npc) throws IOException {
		//Has a problem with id. Should it be an int or an UUID? Prof code has as an int. 
		if (npc != null) { 
			Entity ghostE = sm.createEntity("ghost", "dolphinHighPoly.obj");
			ghostE.setPrimitive(Primitive.TRIANGLES);
			SceneNode ghostN = sm.getRootSceneNode().
			createChildSceneNode(Integer.toString(npc.getID()));
			ghostN.attachObject(ghostE);
			ghostN.setLocalPosition(npc.getPosition());
			npc.setNode(ghostN);
			npc.setEntity(ghostE);
			//avatar.setPosition(node’s position... maybe redundant);
		}
	}
	
	public void removeGhostAvatarFromGameWorld(GhostAvatar avatar) { 
		if(avatar != null) gameObjectsToRemove.add(avatar.getID());
	}
	
	public GhostAvatar getGhostAvatarByID(UUID ghostID) throws Exception {
		for(GhostAvatar ghost: this.ghostAvatarList) {
			if(ghost.getID().compareTo(ghostID) == 0) {
				return ghost;
			}
		}
		throw new Exception("Could not find the Ghost by ID"); //Should create own classes for exception later.	
	}
	
	private void initPhysicsSystem() { 
		String engine = "ray.physics.JBullet.JBulletPhysicsEngine";
		float[] gravity = {0, -3f, 0};
		physicsEngine = PhysicsEngineFactory.createPhysicsEngine(engine);
		physicsEngine.initSystem();
		physicsEngine.setGravity(gravity);
    }
 
	private void createRagePhysicsWorld() { 
		float mass = 1.0f;
		float up[] = {0,1,0};
		double[] temptf;
		
		/*System.out.println("Adding player Physics");
		temptf = toDoubleArray(playerNode.getLocalTransform().toFloatArray());
		playerPhysObj = physicsEngine.addBoxObject(physicsEngine.nextUID(), mass, temptf, up);
		//playerPhysObj.setBounciness(1.0f);
		playerPhysObj.setFriction(1.0f);
		playerNode.setPhysicsObject(playerPhysObj);*/
         
		temptf = toDoubleArray(earthNode.getLocalTransform().toFloatArray());
		earthPhysObj = physicsEngine.addSphereObject(physicsEngine.nextUID(),mass, temptf, 2.0f);
       	earthPhysObj.setBounciness(1.0f);
       	earthNode.setPhysicsObject(earthPhysObj);
        
       	temptf = toDoubleArray(coneNode.getLocalTransform().toFloatArray());
       	conePhysObj = physicsEngine.addSphereObject(physicsEngine.nextUID(),mass, temptf, 2.0f);
       	conePhysObj.setBounciness(1.0f);
       	coneNode.setPhysicsObject(conePhysObj);
        
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
 
   private double[] toDoubleArray(float[] arr) { 
	   if (arr == null) 
		   return null;
	   int n = arr.length;
	   double[] ret = new double[n];
	   for (int i = 0; i < n; i++) { 
		   ret[i] = (double)arr[i];
	   }
	   return ret;
   }
   
   public void setEarParameters(SceneManager sm)
   { 
 		Vector3 avDir = playerNode.getWorldForwardAxis();
 		//  note - should get the camera's forward direction
 		//     - avatar direction plus azimuth 
 		audioMgr.getEar().setLocation(playerNode.getWorldPosition());
 		audioMgr.getEar().setOrientation(avDir, Vector3f.createFrom(0,1,0));
   } 

   
   public void initAudio(SceneManager sm)   { 
	 AudioResource resource1, resource2;
	 audioMgr = AudioManagerFactory.createAudioManager("ray.audio.joal.JOALAudioManager");
     if (!audioMgr.initialize())     { 
		   System.out.println("Audio Manager failed to initialize!");
		   return;
     } 
     resource1 = audioMgr.createAudioResource("Cartoon Hop-SoundBible.com-553158131.wav",AudioResourceType.AUDIO_SAMPLE);
     resource2 = audioMgr.createAudioResource("Water Splash-SoundBible.com-800223477.wav",AudioResourceType.AUDIO_SAMPLE);
     hereSound = new Sound(resource1,SoundType.SOUND_EFFECT, 100, true);
     oceanSound = new Sound(resource2,SoundType.SOUND_EFFECT, 100, true);
     hereSound.initialize(audioMgr);
     oceanSound.initialize(audioMgr);
     hereSound.setMaxDistance(10.0f);
     hereSound.setMinDistance(0.5f);
     hereSound.setRollOff(5.0f);
     oceanSound.setMaxDistance(10.0f);
     oceanSound.setMinDistance(0.5f);
     oceanSound.setRollOff(5.0f);
//     SceneNode robotN = sm.getSceneNode("robotNode");
//     SceneNode earthN = sm.getSceneNode("earthNode");
     hereSound.setLocation(robotNode.getWorldPosition());
     oceanSound.setLocation(earthNode.getWorldPosition());
     setEarParameters(sm);
     hereSound.play();
     oceanSound.play();
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


/*@Override
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
		break;*/
	//case KeyEvent.VK_W:
		/*if(running) {
			System.out.println("Moving forward");
			playerPhysObj.applyForce(0.0f, 0.0f, 10.0f, playerNode.getLocalPosition().x(), 
					playerNode.getLocalPosition().y(), playerNode.getLocalPosition().z());
		}
		else {*/
		//player.setMoveForward(false);
		//}
		//this.doNWalk();
		//break;
	/*case KeyEvent.VK_S:
		if(running) {
			System.out.println("Moving backward");
			playerPhysObj.applyForce(0.0f, 0.0f, -10.0f, playerNode.getLocalPosition().x(), 
					playerNode.getLocalPosition().y(), playerNode.getLocalPosition().z());
		}
		else {
			player.setMoveBackward(true);
		//}
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
	case KeyEvent.VK_Q:
		orbitCamera.moveFurther(0.5f);
		break;
	case KeyEvent.VK_E:
		orbitCamera.moveCloser(0.5f);
		break;
	case KeyEvent.VK_P:                     // Press 'P' to enable Physics
        System.out.println("Starting Physics!");
        running = true;
        break;
	}
}*/

/*@Override
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
	case KeyEvent.VK_W:*/
		/*if(running) {
			System.out.println("Moving forward");
			playerPhysObj.applyForce(0.0f, 0.0f, 10.0f, playerNode.getLocalPosition().x(), 
					playerNode.getLocalPosition().y(), playerNode.getLocalPosition().z());
		}
		else {
		/*player.setMoveForward(false);
		//}
		this.doNWalk();
		break;
	case KeyEvent.VK_S:
		if(running) {
			System.out.println("Moving backward");
			playerPhysObj.applyForce(0.0f, 0.0f, -10.0f, playerNode.getLocalPosition().x(), 
					playerNode.getLocalPosition().y(), playerNode.getLocalPosition().z());
		}
		else {
			player.setMoveBackward(true);
		//}
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
	case KeyEvent.VK_Q:
		orbitCamera.moveFurther(0.5f);
		break;
	case KeyEvent.VK_E:
		orbitCamera.moveCloser(0.5f);
		break;
	case KeyEvent.VK_SPACE:
		this.doAttack();
		break;
	}
}*/
