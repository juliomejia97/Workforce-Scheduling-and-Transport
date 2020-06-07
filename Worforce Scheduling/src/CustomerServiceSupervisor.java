import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

import jade.core.AID;
import jade.core.Agent;
import jade.core.ProfileImpl;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import jade.core.behaviours.*;

public class CustomerServiceSupervisor extends Agent{


	private static final long serialVersionUID = 1L;

	private HashMap<Dia, Integer> actA = new HashMap<Dia, Integer>();
	private HashMap<Dia, Integer> actB = new HashMap<Dia, Integer>();
	private HashMap<Dia, Integer> actC = new HashMap<Dia, Integer>();
	private HashMap<Dia, Integer> totalDemand = new HashMap<Dia, Integer>();
	private CSSupervisorGUI myGui;
	private int population;
	private float mutationRate;
	private float crossoverRate;
	private float elitRate;
	private float threshold;
	private int maxIteration;
	private AID[] serviceAgents;
	private ArrayList<Chromosome> chromosomes;
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
				String[] daySplit = hour.split(" ");
				String day = daySplit[0].trim();
				String[] hora = daySplit[1].split(":");
				LocalTime franja = LocalTime.of(Integer.parseInt(hora[0]), Integer.parseInt(hora[1]));
				int demand = Integer.parseInt(data[1].trim());
				Dia classDia = new Dia(day, franja);
				this.actA.put(classDia, demand);
				line = br.readLine();
			}

			br.close();

			br = new BufferedReader(new FileReader(new File("./ActivityBDemand.csv")));
			line = br.readLine();

			while(line != null) {
				String [] data = line.split(";");
				String hour = data[0].trim();
				String[] daySplit = hour.split(" ");
				String day = daySplit[0].trim();
				String[] hora = daySplit[1].split(":");
				LocalTime franja = LocalTime.of(Integer.parseInt(hora[0]), Integer.parseInt(hora[1]));
				int demand = Integer.parseInt(data[1].trim());
				Dia classDia = new Dia(day, franja);
				this.actB.put(classDia, demand);
				line = br.readLine();
			}

			br.close();

			br = new BufferedReader(new FileReader(new File("./ActivityCDemand.csv")));
			line = br.readLine();

			while(line != null) {
				String [] data = line.split(";");
				String hour = data[0].trim();
				String[] daySplit = hour.split(" ");
				String day = daySplit[0].trim();
				String[] hora = daySplit[1].split(":");
				LocalTime franja = LocalTime.of(Integer.parseInt(hora[0]), Integer.parseInt(hora[1]));
				int demand = Integer.parseInt(data[1].trim());
				Dia classDia = new Dia(day, franja);
				this.actC.put(classDia, demand);
				line = br.readLine();
			}

			br.close();

			br = new BufferedReader(new FileReader(new File("./TotalDemand.csv")));
			line = br.readLine();

			while(line != null) {
				String [] data = line.split(";");
				String hour = data[0].trim();
				String[] daySplit = hour.split(" ");
				String day = daySplit[0].trim();
				String[] hora = daySplit[1].split(":");
				LocalTime franja = LocalTime.of(Integer.parseInt(hora[0]), Integer.parseInt(hora[1]));
				int demand = Integer.parseInt(data[1].trim());
				Dia classDia = new Dia(day, franja);
				this.totalDemand.put(classDia, demand);
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
		addBehaviour(new OneShotBehaviour() {

			public void action() {
				population  = pop;
				maxIteration = iterations;
				mutationRate = mutation;
				crossoverRate = crossover;
				elitRate = elitism;
				threshold = threas;
				container = getContainerController();
				chromosomes = new ArrayList<Chromosome>();
				//TODO: Initiate the GeneticAlgorithm
				// TODO Obtener la lista de agentes de servicio al cliente que existen en el sistema
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
				myAgent.addBehaviour(new GeneticAlgorithm());
			}

		});
	}

	private class GeneticAlgorithm extends Behaviour {
		private int step = 0;
		private int repliesCnt;
		private MessageTemplate mt; // The template to receive replies
		@Override
		public void action() {
			//
			switch (step) {
			case 0:
				//Initiate the population
				for(int i=0; i <population;i++) {
					Chromosome chromosome = new Chromosome(serviceAgents.length);
					chromosomes.add(chromosome);
				}
				step = 1;
				break;
			case 1:
				//Enviar solitudes
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
			    	  String[][] config = new String[7][2];
			    	  try {
						info = (Object[]) reply.getContentObject();
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
			    	  chrom = (int) info[0];
			    	  config = (String[][]) info[1];
			    	  repliesCnt++;
			    	  if(repliesCnt >= serviceAgents.length*population) {
			    		  System.out.println("Entro");
			    	  }
			    	  System.out.println(repliesCnt);
			      }
			      else {
			    	  System.out.println("blocked");
			        block();
			      }
				break;
			case 3:
				//Calcular FO
				break;
			case 4:
				//Cruces
				break;
			case 5:
				//Mutación
				break;
			case 6:
				//Nueva generación y verificar las iteraciones
				break;
			default:
				block();
				break;
			}

		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}

		public void enviarHora(AID agente) {
			int idAgent;
			ACLMessage cfp = new ACLMessage(ACLMessage.REQUEST);
			cfp.addReceiver(agente);
			cfp.setConversationId("report-option");
			idAgent = Integer.parseInt(agente.getName().split(" ")[1].split("@")[0]) - 1;
			for(int i=0; i < chromosomes.size();i++) {
				cfp.setContent(i+" "+chromosomes.get(i).getSolution().get(idAgent));
				myAgent.send(cfp);
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("report-option"),
						MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			}
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


}
