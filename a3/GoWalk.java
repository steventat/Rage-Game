package a3;

import ray.ai.behaviortrees.BTAction;
import ray.ai.behaviortrees.BTStatus;

public class GoWalk extends BTAction {
	private NPC npc;

	public GoWalk(NPC n) { 
		npc = n; 
	}
	
	protected BTStatus update(float elapsedTime) { 
		npc.goWalk();
		return BTStatus.BH_SUCCESS;
	} 
}
