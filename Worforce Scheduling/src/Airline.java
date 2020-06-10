import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import jade.core.Agent;
import jade.core.ProfileImpl;
import jade.core.behaviours.CyclicBehaviour;
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
		setMyInterface(new AirlineGUI(this));
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
		addBehaviour(new bestSchedule());
	}
	
	public AirlineGUI getMyInterface() {
		return myInterface;
	}
	
	public void setMyInterface(AirlineGUI myInterface) {
		this.myInterface = myInterface;
	}
	private class bestSchedule extends CyclicBehaviour {
		MessageTemplate mt =MessageTemplate.and(MessageTemplate.MatchConversationId("best-schedule"),
				MessageTemplate.MatchPerformative(ACLMessage.INFORM));
		
		@Override
		public void action() {
			ACLMessage msg = myAgent.receive(mt);
			Object[] args;
			ArrayList<String[][]> schedule;
			Double FO;
			if(msg!=null) {
				try {
					args =  (Object[]) msg.getContentObject();
					schedule = (ArrayList<String[][]>) args[0];
					FO = (Double) args[1];
					System.out.println("I am Airline, best FO from CSS is: "+FO);
				} catch (UnreadableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}else {
				block();
			}
			
		}
		
	}
}
