package a3;

public class NPCcontroller
{
	private NPC[] NPClist = new NPC[1];
     //. . .
     
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
		NPClist[0] = new NPC(0.0, 0.0, 0.0);
		System.out.println("NPCs: " + NPClist);
	}
	
}
