import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import jade.core.AID;
import jade.core.Agent;
import jade.core.ProfileImpl;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import jade.core.behaviours.*;

public class CustomerServiceSupervisor extends Agent{
	

	private static final long serialVersionUID = 1L;
	
	private HashMap<String, Integer> actA = new HashMap<String, Integer>();
	private HashMap<String, Integer> actB = new HashMap<String, Integer>();
	private HashMap<String, Integer> actC = new HashMap<String, Integer>();
	private CSSupervisorGUI myGui;
	private int population;
	private float mutationRate;
	private float crossoverRate;
	private float elitRate;
	private float threshold;
	private int maxIteration;
	private AID[] slaves;
	private ContainerController container;
	@Override
	protected void setup() {
		
		try {
			BufferedReader br;
			br = new BufferedReader(new FileReader(new File("./ActivityADemand.csv")));
			String line = br.readLine();
			
			while(line != null) {
				String [] data = line.split(";");
				String hour = data[0].trim();
				int demand = Integer.parseInt(data[1].trim());
				this.actA.put(hour, demand);
				line = br.readLine();
			}
			
			br.close();
			
			br = new BufferedReader(new FileReader(new File("./ActivityBDemand.csv")));
			line = br.readLine();
			
			while(line != null) {
				String [] data = line.split(";");
				String hour = data[0].trim();
				int demand = Integer.parseInt(data[1].trim());
				this.actB.put(hour, demand);
				line = br.readLine();
			}
			
			br.close();
			
			br = new BufferedReader(new FileReader(new File("./ActivityCDemand.csv")));
			line = br.readLine();
			
			while(line != null) {
				String [] data = line.split(";");
				String hour = data[0].trim();
				int demand = Integer.parseInt(data[1].trim());
				this.actC.put(hour, demand);
				line = br.readLine();
			}
			
			br.close();
			
			myGui = new CSSupervisorGUI(this);
			System.out.println("Supervisor started...");
			//Subscribing the agent to the AMS Agent and Yellow Pages
			DFAgentDescription dfd = new DFAgentDescription();
		    dfd.setName(getAID());
		    //Offering different services
		    //TODO: Look for other services that the agent offers
		    ServiceDescription sd = new ServiceDescription();
		    sd.setType("report-schedule");
		    sd.setName("JADE-scheduling");
		    dfd.addServices(sd);
		    
		    try {
		      DFService.register(this, dfd);
		    }
		    catch (FIPAException fe) {
		      fe.printStackTrace();
		    }
		    
		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IOException: " + e.getMessage());
		}
	}
	
	/*
	 * Behaviour that initiate a genetic parallel algorithm
	 * */
	public void InitiateGenetic(int pop, int iterations, float mutation, float crossover, float elitism, float threas) {
		addBehaviour(new OneShotBehaviour(){
			public void action() {
				population  = pop;
				maxIteration = iterations;
				mutationRate = mutation;
				crossoverRate = crossover;
				elitRate = elitism;
				threshold = threas;
				container = getContainerController();
				AgentController ac;
				Object[] args = {population, mutationRate, crossoverRate, elitRate};
				//TODO: Initiate the GA with the two SubAgents
				for(int i=0; i < 2; i++) {
					try {
						ac = container.createNewAgent("GeneticAlgorithm"+i+1, GeneticAlgorithmAgent.class.getName(), args);
						ac.start();
					} catch (StaleProxyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		});
	}
	
	//TODO: Create a method that waits for the best 5 chromosomes of the generations of each agent.
	
	public int getPopulation() {
		return population;
	}

	public void setPopulation(int population) {
		this.population = population;
	}

	public float getMutationRate() {
		return mutationRate;
	}

	public void setMutationRate(float mutationRate) {
		this.mutationRate = mutationRate;
	}

	public float getCrossoverRate() {
		return crossoverRate;
	}

	public void setCrossoverRate(float crossoverRate) {
		this.crossoverRate = crossoverRate;
	}

	public float getElitRate() {
		return elitRate;
	}

	public void setElitRate(float elitRate) {
		this.elitRate = elitRate;
	}

	public float getThreshold() {
		return threshold;
	}

	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}

	public int getMaxIteration() {
		return maxIteration;
	}

	public void setMaxIteration(int maxIteration) {
		this.maxIteration = maxIteration;
	}
	
	 
}
