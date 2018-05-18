package a3;

import ray.ai.behaviortrees.BTBehavior;
import ray.ai.behaviortrees.BTStatus;

public class OneSecPassed extends BTBehavior {

	private NPCcontroller npcc;
	private NPC npc;
	private long lastUpdateTime;

	public OneSecPassed(NPCcontroller c, NPC n, boolean toNegate) {
		//super(toNegate);
		npcc = c;
		npc = n;
		lastUpdateTime = System.nanoTime();
	}

	protected boolean check() { 
		float elapsedMilliSecs = (System.nanoTime()-lastUpdateTime)/(1000000.0f);
		if ((elapsedMilliSecs >= 1000.0f) && (npc.getSize()==2.0)) { 
			lastUpdateTime = System.nanoTime();
			npcc.setNearFlag(false);
			return true;
		}
		else return false;
	}
	@Override
	protected BTStatus update(float arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
