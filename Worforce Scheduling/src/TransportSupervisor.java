import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
		
		private static final long serialVersionUID = 1L;
		private int step = 0;
		private int cantLeaders = 0;
		private int msgReceived = 0;
		private HashMap<String, ArrayList<Integer>> leadersGoing;
		private HashMap<String, ArrayList<Integer>> leadersReturn;
		private HashMap<String, ArrayList<ArrayList<Integer>>> vehiclesGoing = new HashMap<String, ArrayList<ArrayList<Integer>>>();
		private HashMap<String, ArrayList<ArrayList<Integer>>> vehiclesReturn = new HashMap<String, ArrayList<ArrayList<Integer>>>();
		
		
		@Override
		public void action() {
			switch (step) {
			case 0:
				//Generate leaders
				leadersGoing = GenerateLeadersGoing();
				leadersReturn = GenerateLeadersReturn();
				step = 1;
				break;
			case 1:
				//Send to leaders and no leaders the quality of message that the must recive
				sendMessageNonLeadersGoing();
				sendMessageLeadersGoing();
				sendMessageNonLeadersReturn();
				sendMessageLeadersReturn();
				step = 2;
				break;
			case 2:
				//Recive the message of leaders
				MessageTemplate mt = MessageTemplate.or(MessageTemplate.and(MessageTemplate.MatchConversationId("route-as-leaderGoing"),
				MessageTemplate.MatchPerformative(ACLMessage.INFORM)), MessageTemplate.and(MessageTemplate.MatchConversationId("route-as-leaderReturn"),
						MessageTemplate.MatchPerformative(ACLMessage.INFORM)));
				
				ACLMessage msg = myAgent.receive(mt);
				
				if(msg != null) {
					msgReceived++;
					try {
						Object[] params = (Object[]) msg.getContentObject();
						String dayHour = params[0].toString();
						@SuppressWarnings("unchecked")
						ArrayList<Integer> vehicle = (ArrayList<Integer>) params[1];
						if(msg.getConversationId().equalsIgnoreCase("route-as-leaderGoing")) {
							if(vehiclesGoing.get(dayHour) == null) {
								ArrayList<ArrayList<Integer>> vehicles = new ArrayList<ArrayList<Integer>>();
								vehicles.add(vehicle);
								vehiclesGoing.put(dayHour, vehicles);
								//System.out.println("Se asigno primer vehiculo ida para el día y hora " + dayHour);
							}else {
								ArrayList<ArrayList<Integer>> vehicles = vehiclesGoing.get(dayHour);
								vehicles.add(vehicle);
								vehiclesGoing.put(dayHour, vehicles);
								//System.out.println("Se asigno vehiculo ida para el día y hora " + dayHour);
							}
						} else {
							if(vehiclesReturn.get(dayHour) == null) {
								ArrayList<ArrayList<Integer>> vehicles = new ArrayList<ArrayList<Integer>>();
								vehicles.add(vehicle);
								vehiclesReturn.put(dayHour, vehicles);
								//System.out.println("Se asigno primer vehiculo vuelta para el día y hora " + dayHour);

							}else {
								ArrayList<ArrayList<Integer>> vehicles = vehiclesReturn.get(dayHour);
								vehicles.add(vehicle);
								vehiclesReturn.put(dayHour, vehicles);
								//System.out.println("Se asigno vehiculo vuelta para el día y hora " + dayHour);
							}			
						}
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
					
					if(cantLeaders == msgReceived) {
						step = 3;
					}
					
				} else {
					block();
				}
								
				break;
			case 3:
				
				step = 4;
				break;
			case 4:
				double FO = calculateRoutingFO();
				//System.out.println("FO: " + FO);
				block();
				break;
			default:
				break;
			}

		}

		@Override
		public boolean done() {
			return (step == 5);
		}
		
		private double calculateRoutingFO() {
			
			double FO = 0;
			double N = 0; //Employees that the airline transport
			double NRoutes = 0; //How many vehicles i have
			double efficiency = 0;
			double totalKmAgent = 0;
			double additionalKm = 0;
			double idealKm = 0;
			double indirectRoutes = 0;
			
			for(Map.Entry<String, ArrayList<ArrayList<Integer>>> allVehiclesGoing: vehiclesGoing.entrySet()) {
				ArrayList<ArrayList<Integer>> vehicles = allVehiclesGoing.getValue();
				System.out.println("Rutas IDA ");
				System.out.println();
				for(ArrayList<Integer> vehicleDay: vehicles) {
					System.out.println("DIA Y HORA: " + allVehiclesGoing.getKey());
					NRoutes++;
					indirectRoutes += vehicleDay.size() - 1;
					for(Integer person: vehicleDay) {
						System.out.print(person + " ");
						N++;
						totalKmAgent = calculateKmAgentGoing(person, vehicleDay);
						additionalKm += totalKmAgent - distances[person][0];
						idealKm += distances[person][0];
					}
					System.out.println();
					System.out.println();
				}
			}
			
			for(Map.Entry<String, ArrayList<ArrayList<Integer>>> allVehiclesReturn: vehiclesReturn.entrySet()) {
				ArrayList<ArrayList<Integer>> vehicles = allVehiclesReturn.getValue();
				System.out.println("Rutas VUELTA ");
				System.out.println();
				for(ArrayList<Integer> vehicleDay: vehicles) {
					System.out.println("DIA Y HORA: " + allVehiclesReturn.getKey());
					NRoutes++;
					indirectRoutes += vehicleDay.size() - 1;
					for(Integer person: vehicleDay) {
						System.out.print(person + " ");
						N++;
						totalKmAgent = calculateKmAgentReturn(person, vehicleDay);
						additionalKm += totalKmAgent - distances[0][person];
						idealKm += distances[0][person];
					}
					System.out.println();
					System.out.println();
				}
			}
			
			efficiency = N / NRoutes;
			System.out.println("Efficiency: " + efficiency);
			System.out.println("Additional Km: " + additionalKm / indirectRoutes);
			System.out.println("Ideal Km: " + idealKm / N);
			System.out.println("Indirect Routes: " + indirectRoutes);
			
			return FO;
			
		}
		
		private double calculateKmAgentGoing(int idAgent, ArrayList<Integer> vehicle) {
			
			double km = 0;
			boolean start = false;
			
			if(vehicle.size() == 1) {
				return distances[idAgent][0];
			}
			
			for(int i = 0; i < vehicle.size() - 1; i++) {
				if(vehicle.get(i) == idAgent) {
					start = true;
				}
				if(start) {
					km += distances[vehicle.get(i)][vehicle.get(i + 1)];
				}
			}
			
			km += distances[vehicle.get(vehicle.size() - 1)][0];
						
			return km;
		}
		
		private double calculateKmAgentReturn(int idAgent, ArrayList<Integer> vehicle) {
			
			double km = 0;
			boolean start = false;
			
			if(vehicle.size() == 1) {
				return distances[0][idAgent];
			}
			
			km += distances[0][vehicle.get(0)];

			
			for(int i = 1; i < vehicle.size() - 1; i++) {
				if(vehicle.get(i) == idAgent) {
					start = true;
				}
				if(start) {
					km += distances[vehicle.get(i)][vehicle.get(i + 1)];
				}
			}
									
			return km;
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
					if(distances[0][actual] < farLeader && !lst.contains(actual)) {
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
								cantLeaders++;
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
		
		private void sendMessageLeadersReturn() {

			for(Map.Entry<String, ArrayList<Integer>> actual: leadersReturn.entrySet()) {
				if(vuelta.get(actual.getKey()).size() > 0) {
					for(Integer agent:actual.getValue()) {
						AID recep= getAgent(agent);
						ACLMessage msg = new ACLMessage(ACLMessage.QUERY_REF);
						msg.setConversationId("route-as-leaderReturn");
						if(recep!=null) {
							msg.addReceiver(recep);
							Object[] args = {actual.getKey(), vuelta.get(actual.getKey())};
							try {
								msg.setContentObject(args);
								myAgent.send(msg);
								cantLeaders++;
							} catch (IOException e) {
								e.printStackTrace();
							}
						}	
					}
				}
			}
		}
		
		private void sendMessageNonLeadersReturn() {
			
			for(Map.Entry<String, ArrayList<Integer>> actual: vuelta.entrySet()) {
				for(Integer agent: actual.getValue()) {
					AID recep= getAgent(agent);
					ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
					msg.setConversationId("inform-size-leaders-return");
					if(recep!=null) {
						msg.addReceiver(recep);
						Object[] args = {actual.getKey(), leadersReturn.get(actual.getKey()).size()};
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
