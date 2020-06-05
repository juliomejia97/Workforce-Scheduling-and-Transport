import jade.core.Agent;

public class GeneticAlgorithmAgent extends Agent{
	
	private CSSupervisorGUI myGui;
	private int population;
	private float mutationRate;
	private float crossoverRate;
	private float elitRate;
	private float threshold;
	private int maxIteration;
	
	protected void setup() {
		super.setup();
		System.out.println("Genetic Algorithm Agent have start....");
		Object[] args = getArguments();
		population = Integer.parseInt(args[0].toString());
		mutationRate = Float.parseFloat(args[1].toString());
		crossoverRate = Float.parseFloat(args[2].toString());
		elitRate = Float.parseFloat(args[3].toString());

	}

}
