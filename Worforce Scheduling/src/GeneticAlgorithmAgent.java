import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;

public class GeneticAlgorithmAgent extends Agent{
	
	private CSSupervisorGUI myGui;
	private int population;
	private float mutationRate;
	private float crossoverRate;
	private float elitRate;
	private float threshold;
	private int maxIteration;
	private HashMap<Dia, Integer> demand = new HashMap<Dia, Integer>();
	
	protected void setup() {
		super.setup();
		System.out.println("Genetic Algorithm Agent have start....");
		Object[] args = getArguments();
		population = Integer.parseInt(args[0].toString());
		mutationRate = Float.parseFloat(args[1].toString());
		crossoverRate = Float.parseFloat(args[2].toString());
		elitRate = Float.parseFloat(args[3].toString());
		demand = (HashMap<Dia, Integer>) args[4];
		
		
		for(Map.Entry<Dia, Integer> entry: demand.entrySet()) {
			Dia key = entry.getKey();
			System.out.println(key.getDay() + " - " + key.getFranja());
		}
		
		/*
		ACLMessage mess = new ACLMessage(ACLMessage.REQUEST);
		mess.setContentObject((Object) object);
		*/

	}

}
