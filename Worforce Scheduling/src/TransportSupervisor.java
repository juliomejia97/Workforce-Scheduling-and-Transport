import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
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
	private HashMap<String, ArrayList<Integer>> ida;
	private HashMap<String, ArrayList<Integer>> vuelta;
	private ArrayList<String[][]> timeSolt;
	private TransportSupervisorGUI myGui;
	private Double[][] distances;
	private AID[] agents;

	@Override
	protected void setup(){
		ida  = new HashMap<String, ArrayList<Integer>>();
		vuelta = new HashMap<String, ArrayList<Integer>>();
		distances = new Double[76][76];
		try {
			int countDistances = 0;
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

			br = new BufferedReader(new FileReader(new File("./Distances.csv")));
			line = br.readLine();

			while(line != null) {
				String []data = line.split(";");
				for(int i = 1; i < data.length; i++) {
					this.distances[countDistances][i - 1] = Double.parseDouble(data[i].trim());
				}
				line = br.readLine();
				countDistances++;
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

	public HashMap<String, ArrayList<Integer>> getIda() {
		return ida;
	}

	public void setIda(HashMap<String, ArrayList<Integer>> ida) {
		this.ida = ida;
	}

	public HashMap<String, ArrayList<Integer>> getVuelta() {
		return vuelta;
	}

	public void setVuelta(HashMap<String, ArrayList<Integer>> vuelta) {
		this.vuelta = vuelta;
	}

	public ArrayList<String[][]> getTimeSolt() {
		return timeSolt;
	}

	public void setTimeSolt(ArrayList<String[][]> timeSolt) {
		this.timeSolt = timeSolt;
	}

	private class routingAgent extends CyclicBehaviour{

		private static final long serialVersionUID = 1L;

		MessageTemplate mt =MessageTemplate.and(MessageTemplate.MatchConversationId("routing"),
				MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
		@SuppressWarnings("unchecked")
		@Override
		public void action() {
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				try {
					setTimeSolt((ArrayList<String[][]>) msg.getContentObject());
					extractPossibleRoutes();

				} catch (UnreadableException e) {
					e.printStackTrace();
				}
				//Get agents in the platform
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription serviceD = new ServiceDescription();
				serviceD.setType("transport");
				template.addServices(serviceD);
				try {
					DFAgentDescription[] result = DFService.search(myAgent, template);
					agents = new AID[result.length];
					for(int i=0; i < result.length;i++) {
						agents[i] = result[i].getName();
					}
				} catch (FIPAException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				addBehaviour(new routing());
			}else {
				block();
			}
		}

	}

	public void extractPossibleRoutes() {

		if(timeSolt != null) {
			ArrayList<Integer> agentsGoing;
			ArrayList<Integer> agentsReturn;
			for(int i=0; i < timeSolt.size();i++) {
				for(int j=0; j < 8;j++) {
					String info = timeSolt.get(i)[j][0];
					String day = timeSolt.get(i)[j][0].split(" ")[0];
					String perm = timeSolt.get(i)[j][1];
					String hour = timeSolt.get(i)[j][0].split(" ")[1];
					LocalTime hourLT = LocalTime.of(Integer.parseInt(hour.split(":")[0]), 
							Integer.parseInt(hour.split(":")[1]));

					if(needTransportForGoing(hourLT, perm)) {
						if(ida.get(info) != null) {
							agentsGoing = ida.get(info);
							agentsGoing.add((i+1));
							ida.put(info, agentsGoing);
						}else {
							agentsGoing = new ArrayList<Integer>();
							agentsGoing.add(i + 1);
							ida.put(info, agentsGoing);
						}
					}

					if(needTransportForReturn(hourLT, perm)) {

						hourLT = hourLT.plusHours(9);
						String newDay = day + " " + hourLT.toString();

						if(vuelta.get(newDay) != null) {
							agentsReturn = vuelta.get(newDay);
							agentsReturn.add((i+1));
							vuelta.put(newDay, agentsReturn);
						}else {
							agentsReturn = new ArrayList<Integer>();
							agentsReturn.add(i + 1);
							vuelta.put(newDay, agentsReturn);
						}
					}
				}
			}
		}
	}

	public boolean needTransportForGoing(LocalTime hour, String perm){

		if(hour.compareTo(LocalTime.of(21, 0)) >= 0 || 
				hour.compareTo(LocalTime.of(6, 30)) <= 0) {
			if(perm.equalsIgnoreCase("LLLL")) {
				return false;
			} else {
				return true;
			}
		}else {
			return false;
		}
	}

	public boolean needTransportForReturn(LocalTime hour, String perm) {

		hour = hour.plusHours(9);

		if(hour.compareTo(LocalTime.of(21, 0)) >= 0 || 
				hour.compareTo(LocalTime.of(6, 30)) <= 0) {
			if(perm.equalsIgnoreCase("LLLL")) {
				return false;
			} else {
				return true;
			}
		}else {
			return false;
		}
	}

	private class routing extends Behaviour{
		
		private int step = 0;
		private HashMap<String, ArrayList<Integer>> leadersGoing;
		private HashMap<String, ArrayList<Integer>> leadersReturn;
		@Override
		public void action() {
			switch (step) {
			case 0:
				//Generate leaders
				leadersGoing = GenerateLeadersGoing();
				leadersReturn = GenerateLeadersReturn();
				step=1;
				break;
			case 1:
				//Send to leaders and no leaders the quality of message that the must recive
				for(Map.Entry<String, ArrayList<Integer>> actual: ida.entrySet()) {
					System.out.print(actual.getKey() + " ");
					for(Integer agent: actual.getValue()) {
						System.out.print(agent + " ");
					}
					System.out.println();
				}
				sendMessageLeadersGoing();
				block();
				//sendMessageNoLeaders();
				break;
			case 2:
				//Recive the message of leaders
				//Going and return leaders
				break;
			case 3:
				//Fix if there is an agent without car
				break;
			case 4:
				//Send solution to airline
				break;
			default:
				break;
			}

		}

		@Override
		public boolean done() {
			return (step==5);
		}

		private HashMap<String, ArrayList<Integer>> GenerateLeadersGoing(){
			int numCars;
			HashMap<String, ArrayList<Integer>> leaders = new HashMap<String, ArrayList<Integer>>();
			for(Map.Entry<String, ArrayList<Integer>> actual:ida.entrySet()) {
				numCars = (int) Math.ceil((double)actual.getValue().size()/3);
				ArrayList<Integer> ld = getLeaderGoing(actual.getValue(), numCars);
				removeLeaders(actual.getKey(), ld, 1);
				leaders.put(actual.getKey(), ld);
			}
			return leaders;
		}

		private HashMap<String, ArrayList<Integer>> GenerateLeadersReturn(){
			int numCars;
			HashMap<String, ArrayList<Integer>> leaders = new HashMap<String, ArrayList<Integer>>();
			for(Map.Entry<String, ArrayList<Integer>> actual:vuelta.entrySet()) {
				numCars = (int) Math.ceil((double)actual.getValue().size()/3); 
				ArrayList<Integer> ld = getLeaderReturn(actual.getValue(), numCars);
				removeLeaders(actual.getKey(), ld, 0);
				leaders.put(actual.getKey(), ld);
			}
			return leaders;
		}

		private ArrayList<Integer> getLeaderGoing(ArrayList<Integer> agents, int cantLeaders){
			int leader = -1;
			ArrayList<Integer> lst = new ArrayList<Integer>();
			double farLeader = 0;
			int i=0;
			while(i < cantLeaders) {
				leader = -1;
				farLeader = 0;
				for(int actual:agents) {
					if(distances[actual][0]>farLeader && !lst.contains(actual)) {
						leader = actual;
						farLeader = distances[actual][0];
					}
				}
				
				lst.add(leader);
				i++;
			}
			return lst;
		}
		
		private ArrayList<Integer> getLeaderReturn(ArrayList<Integer> agents, int cantLeaders){
			int leader = -1;
			ArrayList<Integer> lst = new ArrayList<Integer>();
			double farLeader = 100000;
			int i=0;
			while(i < cantLeaders) {
				leader = -1;
				farLeader = 100000;
				for(int actual:agents) {
					if(distances[0][actual]<farLeader && !lst.contains(actual)) {
						leader = actual;
						farLeader = distances[0][actual];
					}
				}
				lst.add(leader);
				i++;
			}
			return lst;
		}
		
		public void removeLeaders(String dayHour, ArrayList<Integer> leaders, int caso) {
			
			if(caso == 1) {
				
				ArrayList<Integer> nonLeaders = new ArrayList<Integer>();
				
				ArrayList<Integer> agents = ida.get(dayHour);
				for(int i = 0; i < agents.size(); i++) {
					if(!leaders.contains(agents.get(i))) {
						nonLeaders.add(agents.get(i));
					}
				}
				
				ida.put(dayHour, nonLeaders);
				
			} else {
				
				ArrayList<Integer> nonLeaders = new ArrayList<Integer>();
				
				ArrayList<Integer> agents = vuelta.get(dayHour);
				for(int i = 0; i < agents.size(); i++) {
					if(!leaders.contains(agents.get(i))) {
						nonLeaders.add(agents.get(i));
					}
				}
				
				vuelta.put(dayHour, nonLeaders);
				
			}
			
		}
		
		private void sendMessageLeadersGoing() {

			for(Map.Entry<String, ArrayList<Integer>> actual:leadersGoing.entrySet()) {
				if(ida.get(actual.getKey()).size() > 0) {
					for(Integer agent:actual.getValue()) {
						AID recep= getAgent(agent);
						ACLMessage msg = new ACLMessage(ACLMessage.QUERY_REF);
						msg.setConversationId("route-as-leaderGoing");
						if(recep!=null) {
							msg.addReceiver(recep);
							Object[] args = {actual.getKey(), ida.get(actual.getKey())};
							try {
								msg.setContentObject(args);
								myAgent.send(msg);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}	
					}
				}
			}
			
		}
		
		private void sendMessageNonLeadersGoing() {
			
			for(Map.Entry<String, ArrayList<Integer>> actual: ida.entrySet()) {
				for(Integer agent: actual.getValue()) {
					AID recep= getAgent(agent);
					ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
					msg.setConversationId("inform-size-leaders-going");
					if(recep!=null) {
						msg.addReceiver(recep);
						Object[] args = {actual.getKey(), leadersGoing.get(actual.getKey()).size()};
						try {
							msg.setContentObject(args);
							myAgent.send(msg);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}	
				}
			}
			
		}

		private AID getAgent(int numAgente) {
			int idAgent;
			for(AID actual: agents) {
				idAgent = Integer.parseInt(actual.getName().split("@")[0].split(" ")[1]);
				if(idAgent == numAgente) {
					return actual;
				}
			}
			return null;
		}
	}

}
