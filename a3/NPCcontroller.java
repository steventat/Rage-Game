package a3;

import ray.ai.behaviortrees.BTCompositeType;
import ray.ai.behaviortrees.BTSequence;
import ray.ai.behaviortrees.BehaviorTree;

public class NPCcontroller
{
	private NPC[] NPClist = new NPC[2];
	private NPC person;
	private BehaviorTree bt = new BehaviorTree(BTCompositeType.SELECTOR);
	private long thinkStartTime;
	private long tickStartTime;
	private long lastThinkUpdateTime;
	private long lastTickUpdateTime; 
	private GameServerUDP thisUDPServer;
	
	public void start () { 
		thinkStartTime = System.nanoTime();
		tickStartTime = System.nanoTime();
		lastThinkUpdateTime = thinkStartTime;
		lastTickUpdateTime = tickStartTime;
		setUpNPCs();
		setupBehaviorTree();
		npcLoop();
	 }
	
	public void updateNPCs() {
		int numNPCs = this.getNumOfNPCs();
		for (int i=0; i<numNPCs; i++) { 
			NPClist[i].updateLocation();
        } 
    }
    //. . .
	
	public int getNumOfNPCs() {
		return NPClist.length;
	}
	
	public NPC getNPC(int index) {
		return NPClist[index];
	}
	 
	 public void setUpNPCs() {
		person = new NPC(1.0, 1.0, 1.0);
		NPClist[0] = new NPC(0.0, 0.0, 0.0);
		NPClist[1] = person;
		for(int i = 0; i < NPClist.length; i++) {
			NPClist[i].setID(i);
		}
		System.out.println("NPCs: " + NPClist);
	 }
	 
	 public void npcLoop() { 
		 while (true) { 
			 long currentTime = System.nanoTime();
			 float elapsedThinkMilliSecs = (currentTime-lastThinkUpdateTime)/(1000000.0f);
			 float elapsedTickMilliSecs = (currentTime- lastTickUpdateTime)/(1000000.0f);
			 if (elapsedTickMilliSecs >= 50.0f) { // “TICK”
				 lastTickUpdateTime = currentTime;
				 person.updateLocation();
				 thisUDPServer.sendNPCinfo(); // Already doing this in NetworkingServer.
			 }
			 if (elapsedThinkMilliSecs >= 500.0f) { // “THINK”
				 lastThinkUpdateTime = currentTime;
				 bt.update(elapsedThinkMilliSecs);
			 }
			 Thread.yield();
		 }
	 }
	
	 public void setupBehaviorTree() { 
		 bt.insertAtRoot(new BTSequence(10));
		 bt.insertAtRoot(new BTSequence(20));
		 bt.insert(10, new OneSecPassed(this,person,false));
		 //bt.insert(10, new GoWalk(person));
		 bt.insert(10, new GetSmall(person));
		 bt.insert(20, new AvatarNear(thisUDPServer,this,person,false));
		 bt.insert(20, new GetBig(person));
	 }

	public boolean getNearFlag() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setNearFlag(boolean b) {
		// TODO Auto-generated method stub
		
	}
}
