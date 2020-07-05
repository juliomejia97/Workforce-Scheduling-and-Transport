import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import jade.core.AID;
import jade.core.Agent;
import jade.core.ProfileImpl;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class Airline extends Agent{

	private static final long serialVersionUID = 1L;
	private ContainerController container;
	private AirlineGUI myInterface;
	private AID transport;
	private AID scheduler;
	private ArrayList<String[][]> timeslots;
	private double schedulingFO;
	private double maxDemand;
	private double unatendedDemand;
	private double wellnessFO;
	private double nRoutes;
	@Override
	protected void setup() {

		try {
			
			jade.core.Runtime runtime = jade.core.Runtime.instance();
			container = runtime.createAgentContainer(new ProfileImpl());
			
			System.out.println("Airline started...");
			
			BufferedReader br;
			br = new BufferedReader(new FileReader(new File("./Agent parameters.csv")));
			String line = br.readLine();
			line = br.readLine();
			int cont = 1;

			while(line != null) {
				
				String name = "Agente ";
				Object [] params = {line};
				container.createNewAgent(name + cont, CustomerServiceAgent.class.getName(), params).start();
				cont++;
				line = br.readLine();
				
			}
			br.close();
			
			
		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IOException: " + e.getMessage());
		} catch (StaleProxyException e) {
			System.out.println("StaleProxyException: " + e.getMessage());
		}
		//Offer service of agent
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("best-solution");
		sd.setName("JADE-scheduling");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		
		myInterface = new AirlineGUI(this, new ArrayList<String[][]>(), 0, 0, 0, 0, 0);
		addBehaviour(new bestSchedule());
		addBehaviour(new resultRouting());
		addBehaviour(new peakDemandResult());
	}
	
	public AirlineGUI getMyInterface() {
		return myInterface;
	}
	
	public void setMyInterface(AirlineGUI myInterface) {
		this.myInterface = myInterface;
	}
	
	public void peakDemand(String day, String hour, String activity, int increment) {
		addBehaviour(new OneShotBehaviour() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void action() {
				//Read from GUI parameters
				ACLMessage cfp = new ACLMessage(ACLMessage.REQUEST);
				cfp.addReceiver(scheduler);
				cfp.setConversationId("peak-demand");
				Object[] params = {day,hour,activity,increment};
				try {
					cfp.setContentObject(params);
					myAgent.send(cfp);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private class peakDemandResult extends CyclicBehaviour{
		
		private static final long serialVersionUID = 1L;
		MessageTemplate mt =MessageTemplate.and(MessageTemplate.MatchConversationId("peak-demand-result"),
				MessageTemplate.MatchPerformative(ACLMessage.INFORM));
		
		@SuppressWarnings("unchecked")
		@Override
		public void action() {
			ACLMessage msg = myAgent.receive(mt);
			Object[] args;
			ArrayList<String[][]> schedule = null;
			if(msg!=null) {
				try {
					args =  (Object[]) msg.getContentObject();
					schedule = (ArrayList<String[][]>) args[0];
					timeslots = schedule;
					schedulingFO = (Double) args[1];
					maxDemand = (Double) args[2];
					unatendedDemand = (Double) args[3];
					myInterface.displayFO(timeslots, schedulingFO, wellnessFO, nRoutes, maxDemand, unatendedDemand);
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
			}else {
				block();
			}
		}
	}
	
	private class bestSchedule extends CyclicBehaviour {
		
		private static final long serialVersionUID = 1L;
		MessageTemplate mt =MessageTemplate.and(MessageTemplate.MatchConversationId("best-schedule"),
				MessageTemplate.MatchPerformative(ACLMessage.INFORM));
		
		@SuppressWarnings("unchecked")
		@Override
		public void action() {
			ACLMessage msg = myAgent.receive(mt);
			Object[] args;
			ArrayList<String[][]> schedule = null;
			if(msg!=null) {
				try {
					args =  (Object[]) msg.getContentObject();
					schedule = (ArrayList<String[][]>) args[0];
					timeslots = schedule;
					schedulingFO = (Double) args[1];
					maxDemand = (Double) args[2];
					unatendedDemand = (Double) args[3];
					scheduler = msg.getSender();
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription serviceD = new ServiceDescription();
				serviceD.setType("route");
				template.addServices(serviceD);
				try {
					DFAgentDescription[] result = DFService.search(myAgent, template);
					transport = result[0].getName();
				} catch (FIPAException e) {
					e.printStackTrace();
				}
				addBehaviour(new requestRouting());
			}else {
				block();
			}
			
		}
		
	}
	private class requestRouting extends OneShotBehaviour{

		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			ACLMessage cfp = new ACLMessage(ACLMessage.REQUEST);
			cfp.addReceiver(transport);
			cfp.setConversationId("routing");
			try {
				Object[] params = {schedulingFO, timeslots, maxDemand, unatendedDemand};
				cfp.setContentObject(params);
				myAgent.send(cfp);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	private class resultRouting extends CyclicBehaviour {

		private static final long serialVersionUID = 1L;
		MessageTemplate mt =MessageTemplate.and(MessageTemplate.MatchConversationId("display"),
				MessageTemplate.MatchPerformative(ACLMessage.INFORM));
		@Override
		public void action() {
			ACLMessage msg = myAgent.receive(mt);
			if(msg!=null) {
				try {
					Object[] params = (Object[]) msg.getContentObject();
					wellnessFO = (double) params[0];
					nRoutes = (double) params[1];
					myInterface.displayFO(timeslots, schedulingFO, wellnessFO, nRoutes, maxDemand, unatendedDemand);
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
				
			}else {
				block();
			}
		}
	}
	
	public void agentsNoPresent() {
		addBehaviour(new OneShotBehaviour() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void action() {
				Boolean isSelected;
				Boolean isNotFree;
				ArrayList<Boolean> selection;
				Object[] infoAgent;
				ArrayList<ArrayList<Boolean>> agentsState = new ArrayList<ArrayList<Boolean>>();
				for(int i = 0; i < timeslots.size(); i++) {
					selection = new ArrayList<Boolean>();
					for(int j=0;j < 8; j++) {
						selection.add(false);
					}
					agentsState.add(selection);
				}				

				isSelected = false;
				isNotFree = true;
				infoAgent = null;
				while(!isSelected && isNotFree) {
					infoAgent = agentRandom();
					if(agentsState.get((int)infoAgent[1]).get((int)infoAgent[0]) == false) {
						isSelected = true;
						agentsState.get((int)infoAgent[1]).set((int)infoAgent[0],true);
					}
					if(!timeslots.get((int)infoAgent[1])[(int) infoAgent[0]][1].equals("LLLL")){
						isNotFree = false;
					}
				}
				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
				msg.setConversationId("agents-absences");
				msg.addReceiver(scheduler);
				try {
					msg.setContentObject(infoAgent);
					myAgent.send(msg);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private Object[] agentRandom() {
		Object[] params = new Object[2];
		int day = (int) Math.floor(Math.random() * 7);
		int agent = (int) Math.floor(Math.random() * 74);
		params[0] = day;
		params[1] = agent;
		return params;
	}
}
