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
	}
	
	public AirlineGUI getMyInterface() {
		return myInterface;
	}
	
	public void setMyInterface(AirlineGUI myInterface) {
		this.myInterface = myInterface;
	}
	
	public void peakDemand(String day, String hour, String activity, int increment) {
		addBehaviour(new demandPerturbation("Mar","13:00","A",10));
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
			// TODO Auto-generated method stub
			ACLMessage msg = myAgent.receive(mt);
			if(msg!=null) {
				try {
					Object[] params = (Object[]) msg.getContentObject();
					double wellnessFO = (double) params[0];
					double nRoutes = (double) params[1];
					myInterface.displayFO(timeslots, schedulingFO, wellnessFO, nRoutes, maxDemand, unatendedDemand);
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
				
			}else {
				block();
			}
		}
	}
	
	private class demandPerturbation extends OneShotBehaviour{
		private static final long serialVersionUID = 1L;
		String day;
		String hour;
		String activity;
		int increment;

		public demandPerturbation(String pDay, String pHour, String pActivity, int pIncrement) {
			day = pDay;
			hour = pHour;
			activity = pActivity;
			increment = pIncrement;
		}

		@Override
		public void action() {
			//Read from GUI parameters
			ACLMessage cfp = new ACLMessage(ACLMessage.REQUEST);
			cfp.addReceiver(scheduler);
			cfp.setConversationId("peak-demand");
			Object[] params = {day,hour,activity+increment};
			try {
				cfp.setContentObject(params);
				myAgent.send(cfp);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
