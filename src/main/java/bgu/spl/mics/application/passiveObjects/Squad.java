package bgu.spl.mics.application.passiveObjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Passive data-object representing a information about an agent in MI6.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add ONLY private fields and methods to this class.
 */
public class Squad {

	private Map<String, Agent> agents;
	private static class SquadHolder {
		private static Squad instance = new Squad();
	}

	private Squad (){
		agents = new HashMap<>();

	}
	/**
	 * Retrieves the single instance of this class.
	 */
	public static Squad getInstance() {
		return SquadHolder.instance;
	}

	/**
	 * Initializes the squad. This method adds all the agents to the squad.
	 * <p>
	 * @param agents 	Data structure containing all data necessary for initialization
	 * 						of the squad.
	 */
	public void load (Agent[] agents) {
		if (agents != null) {
			for (Agent agent : agents) {
				agent.release();
				this.agents.put(agent.getSerialNumber(), agent);
			}
		}
	}

	/**
	 * Releases agents.
	 */
	public void releaseAgents(List<String> serials){
		synchronized (this) {
			for (String s : serials){
				agents.get(s).release();
			}
			notifyAll();
		}
	}

	/**
	 * simulates executing a mission by calling sleep.
	 * @param time   time ticks to sleep
	 */
	public void sendAgents(List<String> serials, int time){
		try {
			Thread.sleep(time*100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		releaseAgents(serials);
	}

	/**
	 * acquires an agent, i.e. holds the agent until the caller is done with it
	 * @param serials   the serial numbers of the agents
	 * @return â€˜falseâ€™ if an agent of serialNumber â€˜serialâ€™ is missing, and â€˜trueâ€™ otherwise
	 */
	public synchronized boolean getAgents(List<String> serials){
		java.util.Collections.sort(serials);
		for (String serial : serials){
			if(agents.get(serial) == null)
				return false;
		}

			while (!areAgentsAvailable(serials)) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			for (String s : serials) {
				if (agents.get(s).isAvailable())
					agents.get(s).acquire();

			}


		return true;
	}

	private boolean areAgentsAvailable(List<String> serials){

		for(String serial: serials){
			if (!agents.get(serial).isAvailable()){
				return false;
			}
		}
		return true;
	}
	/**
	 * gets the agents names
	 * @param serials the serial numbers of the agents
	 * @return a list of the names of the agents with the specified serials.
	 */
	public List<String> getAgentsNames(List<String> serials){
		List<String> agentsNames = new ArrayList<String>();
		for (String s : serials){
			if(agents.get(s) != null)
			agentsNames.add(agents.get(s).getName());
		}
		return agentsNames;
	}

}
