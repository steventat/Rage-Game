package a3;

import ray.ai.behaviortrees.BTAction;
import ray.ai.behaviortrees.BTStatus;

public class GetBig extends BTAction {
	private NPC npc;

	public GetBig(NPC n) { 
		npc = n; 
	}
	
	protected BTStatus update(float elapsedTime) { 
		npc.goBig();
		return BTStatus.BH_SUCCESS;
	} 
}
