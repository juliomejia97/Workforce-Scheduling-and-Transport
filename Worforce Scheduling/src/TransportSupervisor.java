import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import org.omg.CORBA.TIMEOUT;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class TransportSupervisor extends Agent {

	private static final long serialVersionUID = 1L;
	private HashMap<String, String[]> coordenates = new HashMap<String, String[]>();
	private HashMap<String, ArrayList<Double>> ida;
	private HashMap<String, ArrayList<Double>> vuelta;
	private ArrayList<String[][]> timeSolt;
	private TransportSupervisorGUI myGui;
	
	@Override
	protected void setup(){
		ida  = new HashMap<String, ArrayList<Double>>();
		vuelta = new HashMap<String, ArrayList<Double>>();
		try {
			BufferedReader br;
			br = new BufferedReader(new FileReader(new File("./Coordenates.csv")));
			String line = br.readLine();

			while(line != null) {
				String []data = line.split(";");
				String ag = data[0];
				String [] coor = new String[2];
				coor[0] = data[1]; //X
				coor[1] = data[2]; //Y
				this.coordenates.put(ag, coor);

				line = br.readLine();
			}

			br.close();


		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException: " + e.getMessage());

		} catch (IOException e) {
			System.out.println("IOException: " + e.getMessage());
		}
		myGui = new TransportSupervisorGUI(this);
		//Offer service of agent
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("route");
		sd.setName("JADE-routing");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		addBehaviour(new routingAgent());
	}

	public HashMap<String, ArrayList<Double>> getIda() {
		return ida;
	}

	public void setIda(HashMap<String, ArrayList<Double>> ida) {
		this.ida = ida;
	}

	public HashMap<String, ArrayList<Double>> getVuelta() {
		return vuelta;
	}

	public void setVuelta(HashMap<String, ArrayList<Double>> vuelta) {
		this.vuelta = vuelta;
	}

	public ArrayList<String[][]> getTimeSolt() {
		return timeSolt;
	}

	public void setTimeSolt(ArrayList<String[][]> timeSolt) {
		this.timeSolt = timeSolt;
	}

	private class routingAgent extends CyclicBehaviour{
		MessageTemplate mt =MessageTemplate.and(MessageTemplate.MatchConversationId("routing"),
				MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
		@Override
		public void action() {
			ACLMessage msg = myAgent.receive(mt);
			if (msg!=null) {
				System.out.println("Recibi alguito jiji");
				try {
					setTimeSolt((ArrayList<String[][]>) msg.getContentObject());
					extractPossibleRoutes();
					for(Map.Entry<String, ArrayList<Double>> actual:ida.entrySet()) {
						System.out.print(actual.getKey()+" ");
						for(Double agent:actual.getValue()) {
							System.out.print(agent);
						}
						System.out.println();
					}
				} catch (UnreadableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else {
				block();
			}
		}
		
	}
	
	public void extractPossibleRoutes() {
		if(timeSolt!=null) {
			ArrayList<Double> agentsGoing;
			ArrayList<Double> agentsReturn;
			String info;
			for(int i=0; i < timeSolt.size();i++) {
				for(int j=0; j < 8;j++) {
					info = timeSolt.get(i)[j][0];
					if(needTransportForGoing(info)) {
						if(ida.get(info) != null) {
							agentsGoing = ida.get(info);
							agentsGoing.add((double) (i+1));
							ida.put(info, agentsGoing);
						}else {
							agentsGoing = new ArrayList<Double>();
							agentsGoing.add((double)i + 1);
							ida.put(info, agentsGoing);
						}
					}
				}
			}
		}
		
	}
	public boolean needTransportForGoing(String infoDay){
		String day;
		LocalTime hour;
		day = infoDay.split(" ")[0];
		hour = LocalTime.of(Integer.parseInt(infoDay.split(" ")[1].split(":")[0]), 
				Integer.parseInt(infoDay.split(" ")[1].split(":")[1]));
		if(hour.compareTo(LocalTime.of(21, 0)) > 0 && 
				hour.compareTo(LocalTime.of(6, 30)) < 0) {
			return true;
		}else {
			return false;
		}
	}
//	public boolean needTransportForReturn(String infoDay) {
//		
//	}
}
