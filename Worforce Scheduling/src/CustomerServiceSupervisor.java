import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.wrapper.ContainerController;
import jade.core.behaviours.*;

public class CustomerServiceSupervisor extends Agent{


	private static final long serialVersionUID = 1L;

	private HashMap<String, Integer> actA = new HashMap<String, Integer>();
	private HashMap<String, Integer> actB = new HashMap<String, Integer>();
	private HashMap<String, Integer> actC = new HashMap<String, Integer>();
	private HashMap<String, String> breaks = new HashMap<String, String>();
	private ArrayList<Fathers> fathersSelected;
	private CSSupervisorGUI myGui;
	private int population;
	private float mutationRate;
	private float crossoverRate;
	private float elitRate;
	private float threshold;
	private int maxIteration;
	private AID[] serviceAgents;
	private AID[] airline;
	private ArrayList<Chromosome> chromosomes;
	private ContainerController container;
	private ArrayList<Chromosome> bestChromosomes;

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

			br = new BufferedReader(new FileReader(new File("./Breaks.csv")));
			line = br.readLine();

			while(line != null) {
				String [] data = line.split(";");
				String init = data[0].trim();
				String pause = data[1].trim();
				this.breaks.put(init, pause);
				line = br.readLine();
			}

			br.close();

			setMyGui(new CSSupervisorGUI(this));
			System.out.println("Supervisor started...");
			//Subscribing the agent to the AMS Agent and Yellow Pages
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(getAID());
			//Offering different services
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

	public CSSupervisorGUI getMyGui() {
		return myGui;
	}

	public void setMyGui(CSSupervisorGUI myGui) {
		this.myGui = myGui;
	}

	public ContainerController getContainer() {
		return container;
	}

	public void setContainer(ContainerController container) {
		this.container = container;
	}

	public void InitiateGenetic(int pop, int iterations, float mutation, float crossover, float elitism, float threas) {
		addBehaviour(new OneShotBehaviour() {

			private static final long serialVersionUID = 1L;

			public void action() {
				population  = pop;
				maxIteration = iterations;
				mutationRate = mutation;
				crossoverRate = crossover;
				elitRate = elitism;
				threshold = threas;
				setContainer(getContainerController());
				chromosomes = new ArrayList<Chromosome>();
				bestChromosomes = new ArrayList<Chromosome>();
				//Search for the Customer Service Agents
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("report-timeslot");
				template.addServices(sd);
				try {
					DFAgentDescription[] result = DFService.search(myAgent, template);
					serviceAgents = new AID[result.length];
					for (int i = 0; i < result.length; ++i) {
						serviceAgents[i] = result[i].getName();
					}


				}catch (FIPAException fe) {
					fe.printStackTrace();
				}
				//Search for Airline Agent
				template= new DFAgentDescription();
				sd = new ServiceDescription();
				sd.setType("best-solution");
				template.addServices(sd);
				try {
					DFAgentDescription[] result = DFService.search(myAgent, template);
					airline = new AID[result.length];
					for (int i = 0; i < result.length; ++i) {
						airline[i] = result[i].getName();
					}


				}catch (FIPAException fe) {
					fe.printStackTrace();
				}
				myAgent.addBehaviour(new GeneticAlgorithm());
			}

		});
	}

	private class GeneticAlgorithm extends Behaviour {

		private static final long serialVersionUID = 1L;
		private int step = 0;
		private boolean firstIteration = false;
		private int repliesCnt;
		private int expectedReplies;
		private double totalFitness;
		private int iterations;
		private MessageTemplate mt; // The template to receive replies
		@Override
		public void action() {
			//
			switch (step) {
			case 0:
				//Initiate the population
				iterations = 1;
				for(int i=0; i <population;i++) {
					Chromosome chromosome = new Chromosome(serviceAgents.length);
					chromosomes.add(chromosome);
				}
				step = 1;
				break;
			case 1:
				//Enviar solitudes
				expectedReplies = 0;
				for (int i = 0; i < serviceAgents.length; ++i) {
					enviarHora(serviceAgents[i]);
				}
				step = 2;
				repliesCnt = 0;
				break;
			case 2:
				//recibir configuraciones
				//Armar una matriz o hashmap copia con los resultados
				ACLMessage reply = myAgent.receive(mt);

				if (reply != null) {
					int chrom;
					Object[] info = null;
					String[][] config = new String[8][2];
					try {
						info = (Object[]) reply.getContentObject();
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
					chrom = (int) info[0];
					config = (String[][]) info[1];
					int idAgent = Integer.parseInt(reply.getSender().getName().split(" ")[1].split("@")[0]) - 1;
					chromosomes.get(chrom).setSolutionToTimeslots(idAgent, config);
					repliesCnt++;
					if(!firstIteration) {
						if(repliesCnt == serviceAgents.length * population) {
							//System.out.println("Total messages received from agents: " + repliesCnt);
							step = 3;
						}
					}else {
						if(repliesCnt == expectedReplies) {
							//System.out.println("Total messages received from agents: " + repliesCnt);
							step = 6;
						}
					}
				}
				else {
					System.out.println("***************");
					block();
				}
				break;
			case 3:
				for(Chromosome actual: chromosomes) {
					if (!actual.isFoCalculated()) actual.calculateSchedulingFO(actA, actB, actC, breaks);
				}
				step = 4;
				break;
			case 4:
				//Prob fathers
				totalFitness = 0;
				for(Chromosome actual: chromosomes) {
					double currentFitness = actual.getFitness();
					totalFitness += currentFitness;
				}
				//Set Prob
				for(Chromosome actual: chromosomes) {
					actual.setFatherRate(actual.getFitness()/totalFitness);
				}
				fathersSelected = new ArrayList<Fathers>();
				selectFathers();
				generateKids();
				step = 5;
				//Cruces
				break;
			case 5:
				//Mutación
				mutateKids();
				if(!firstIteration) firstIteration = true;
				step = 1;
				break;
			case 6:
				//Nueva generación y verificar las iteraciones
				ArrayList<Chromosome> newGeneration = new ArrayList<Chromosome>();
				ArrayList<Chromosome> auxGeneration = new ArrayList<Chromosome>();
				ArrayList<Boolean> auxBoolean = new ArrayList<Boolean>();
				int elit = Math.round(population * elitRate);
				//double bestFO = 1000000000;
				Random rand = new Random();
				if(iterations == maxIteration) {
					System.out.println("Finished iterating...");
					System.out.println("The best chromosome is: "+bestChromosomes.get(bestChromosomes.size()-1).getFO());
					step = 7;
					
				} else {
					System.out.println("Starting generation " + (bestChromosomes.size()+1) + "...");
					for(Chromosome actual: chromosomes) {
						if (!actual.isFoCalculated()) actual.calculateSchedulingFO(actA, actB, actC, breaks);
					}
					
					//Sort by the fitness of each chromosome (High to low)
					Collections.sort(chromosomes, new Comparator<Chromosome>() {
						public int compare(Chromosome o1, Chromosome o2) {
							return Double.compare(o2.getFitness(), o1.getFitness()) ;
						}
					});
					
					//TODO: Asignar al vector de mejores el primer mancito
					bestChromosomes.add(chromosomes.get(0));
					if (bestChromosomes.size() > 1) {
						int pos = bestChromosomes.size()-1;
						double diff = Math.abs(bestChromosomes.get(pos).getFitness()- bestChromosomes.get(pos-1).getFitness())/
								bestChromosomes.get(pos).getFitness();
						if(diff<threshold) {
							System.out.println("I have "+iterations+" iterations");
							iterations++;
						}
						else {
							iterations = 0;
						}
					}else {
						
						iterations++;
					}
					//Create the first new generation with the elitism rate and population
					for(int i = 0; i < elit; i++) {
						newGeneration.add(chromosomes.get(i));
					}

					//Initiate sec generation and booleans
					for(int i = elit; i < population; i++) {
						auxGeneration.add(chromosomes.get(i));
						auxBoolean.add(false);
					}

					//Create the second new generation with the elitism rate, population and rand function
					int range = population - elit;
					for(int i = elit; i < population; i++) {
						boolean diff = false;
						while(!diff) {
							int pos = rand.nextInt(range);
							if(!auxBoolean.get(pos)) {
								diff = true;
								auxBoolean.set(pos, true);
								newGeneration.add(auxGeneration.get(pos));
							}
						}
					}
					chromosomes.clear();
					for(int i = 0; i < newGeneration.size(); i++) {
						chromosomes.add(newGeneration.get(i));
					}
					
					step = 4;
				}

				break;
			case 7:
				sendBestSolution();
				step = 8;
				break;
			}
		}

		@Override
		public boolean done() {
			return (step == 8);
		}

		public void enviarHora(AID agente) {
			int idAgent;
			ACLMessage cfp = new ACLMessage(ACLMessage.REQUEST);
			cfp.addReceiver(agente);
			cfp.setConversationId("report-option");
			idAgent = Integer.parseInt(agente.getName().split(" ")[1].split("@")[0]) - 1;
			for(int i=0; i < chromosomes.size(); i++) {
				if(!chromosomes.get(i).isFoCalculated()) {
					expectedReplies ++;
					cfp.setContent(i+" "+chromosomes.get(i).getSolution().get(idAgent));
					myAgent.send(cfp);
					mt = MessageTemplate.and(MessageTemplate.MatchConversationId("report-option"),
							MessageTemplate.MatchPerformative(ACLMessage.INFORM));
				}
			}
		}
		public void sendBestSolution() {
			ArrayList<String[][]> timeslots;
			Double bestFo;
			ACLMessage cfp = new ACLMessage(ACLMessage.INFORM);
			cfp.addReceiver(airline[0]);
			cfp.setConversationId("best-schedule");
			timeslots= bestChromosomes.get(bestChromosomes.size()-1).getTimesolts();
			bestFo = bestChromosomes.get(bestChromosomes.size()-1).getFO();
			System.out.println("Unatended A: " + bestChromosomes.get(bestChromosomes.size() - 1).getUnatendedDemandA());
			System.out.println("Unatended B: " + bestChromosomes.get(bestChromosomes.size() - 1).getUnatendedDemandB());
			System.out.println("Unatended C: " + bestChromosomes.get(bestChromosomes.size() - 1).getUnatendedDemandC());
			System.out.println("Max demand: " + bestChromosomes.get(bestChromosomes.size() - 1).getMaxDemand());
			Object[] params = {timeslots,bestFo};
			try {
				cfp.setContentObject(params);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			myAgent.send(cfp);
		}
		
	}

	public void selectFathers() {
		double ball;
		Random rand = new Random();
		ArrayList<Double> rulette = new ArrayList<Double>();
		//Creating the rulette with acum prob of the population
		rulette.add(chromosomes.get(0).getFatherRate());
		for(int i=1; i < population; i++) {
			rulette.add(rulette.get(i-1)+chromosomes.get(i).getFatherRate());
		}
		for (int i=0; i<chromosomes.size()/2;i++) {
			ball = rand.nextDouble();
			casino(rulette, ball, i);
		}
	}
	
	public void casino(ArrayList<Double> rulette, double ball, int pos) {
		int papa1, papa2;
		papa1 = 0;
		papa2 = 0;
		//Father 1
		//Case 1 
		if (0 < ball && ball<=rulette.get(rulette.size()-1)) papa1 = 0;
		//Case 2
		for(int i=0;i < rulette.size()-1;i++) {
			if (rulette.get(i) <= ball && ball <= rulette.get(i+1)) {
				papa1 = i;
				break;
			}
		}
		//Case 3
		if (rulette.get(rulette.size()-1)<ball && ball <=1) papa1 = rulette.size()-1;
		//Father 2
		if (ball< 0.5) {
			ball+=0.5;
		} else {
			ball += (ball+0.5)-1;
		}
		//Case 1 
		if (0 < ball && ball<=rulette.get(rulette.size()-1)) papa2 = 0;
		//Case 2
		for(int i=0;i < rulette.size()-1;i++) {
			if (rulette.get(i) <= ball && ball <= rulette.get(i+1)) {
				papa2 = i;
				break;
			}
		}
		//Case 3
		if (rulette.get(rulette.size()-1)<ball && ball <=1) papa2 = rulette.size()-1;
		fathersSelected.add(new Fathers(papa1,papa2));
	}

	public void generateKids() {
		Chromosome son1;
		Chromosome son2;
		double prob;
		Random rand = new Random();
		for(Fathers actual:fathersSelected) {
			prob = rand.nextDouble();
			if (prob < crossoverRate) {
				son1 = new Chromosome(serviceAgents.length);
				son2 = new Chromosome(serviceAgents.length);
				crossFathers(son1, son2, actual);
				chromosomes.add(son1);
				chromosomes.add(son2);
			}
		}
	}

	@SuppressWarnings("static-access")
	public void crossFathers(Chromosome son1, Chromosome son2, Fathers couple) {
		Chromosome father1 = chromosomes.get(couple.getFather1());
		Chromosome father2 = chromosomes.get(couple.getFather2());
		//Information solution of child 1
		ArrayList<Double> solution1 = new ArrayList<Double>();
		ArrayList<Integer> genoma = father1.getGenoma();
		for (int i=0; i < father1.getSolution().size();i++) {
			if(genoma.get(i)==0) {
				solution1.add(father1.getSolution().get(i));
			}else {
				solution1.add(father1.getSolution().get(i)+father2.getSolution().get(i));
			}
		}
		//Information solution of child 2
		ArrayList<Double> solution2 = new ArrayList<Double>();
		genoma = father2.getGenoma();
		for (int i=0; i < father2.getSolution().size();i++) {
			if(genoma.get(i)==0) {
				solution2.add(father2.getSolution().get(i));
			}else {
				solution2.add(father1.getSolution().get(i)-father2.getSolution().get(i));
			}
		}
		//Fix by circular
		//Child1 
		for(int i=0; i < solution1.size();i++) {
			if(solution1.get(i)>47) {
				solution1.set(i, son1.roundTwoDecimals(solution1.get(i)%47, 2));
			}
		}
		son1.setSolution(solution1);
		//Child2
		for(int i=0; i < solution2.size();i++) {
			if(solution2.get(i)<0) {
				solution2.set(i, son2.roundTwoDecimals(47+solution2.get(i), 2));
			}
		}
		son2.setSolution(solution2);
	}

	public void mutateKids() {

		double prob;
		Random rand = new Random();
		ArrayList<Double> solutionSwap;
		for(int i=population+1;i < chromosomes.size(); i++) {
			prob = rand.nextDouble();
			if(prob < mutationRate) {
				solutionSwap = mutate(chromosomes.get(i));
				chromosomes.get(i).setSolution(solutionSwap);
			}
		}
	}

	public ArrayList<Double> mutate(Chromosome child) {

		ArrayList<Double> swap = new ArrayList<Double>();
		ArrayList<Double> sol = child.getSolution();
		Random rand = new Random();
		int upper = serviceAgents.length;
		int pos = rand.nextInt(upper);
		for(int i=pos+1; i < sol.size();i++) {
			swap.add(sol.get(i));
		}
		for(int i=0; i < pos+1;i++) {
			swap.add(sol.get(i));
		}
		return swap;
	}

}
