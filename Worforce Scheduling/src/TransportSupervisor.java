import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jade.core.Agent;
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
	private HashMap<String, ArrayList<Double>> ida;
	private HashMap<String, ArrayList<Double>> vuelta;
	private ArrayList<String[][]> timeSolt;
	private TransportSupervisorGUI myGui;
	private Double[][] distances;

	@Override
	protected void setup(){
		ida  = new HashMap<String, ArrayList<Double>>();
		vuelta = new HashMap<String, ArrayList<Double>>();
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
			
			for(int i = 0; i < 76; i++) {
				System.out.println();
				for(int j = 0; j < 76; j++) {
					System.out.print(this.distances[i][j] + " ");
				}
			}


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
					
					System.out.println("Rutas ida");
					System.out.println();
					for(Map.Entry<String, ArrayList<Double>> actual: ida.entrySet()) {
						System.out.print(actual.getKey() + " ");
						for(double agent: actual.getValue()) {
							System.out.print(agent + " ");
						}
						System.out.println();
					}
					
					System.out.println("Rutas vuelta");
					System.out.println();
					for(Map.Entry<String, ArrayList<Double>> actual: vuelta.entrySet()) {
						System.out.print(actual.getKey() + " ");
						for(double agent: actual.getValue()) {
							System.out.print(agent + " ");
						}
						System.out.println();
					}
					
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
			}else {
				block();
			}
		}

	}

	public void extractPossibleRoutes() {
		
		if(timeSolt != null) {
			ArrayList<Double> agentsGoing;
			ArrayList<Double> agentsReturn;
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
							agentsGoing.add((double) (i+1));
							ida.put(info, agentsGoing);
						}else {
							agentsGoing = new ArrayList<Double>();
							agentsGoing.add((double)i + 1);
							ida.put(info, agentsGoing);
						}
					}

					if(needTransportForReturn(hourLT, perm)) {
						
						hourLT = hourLT.plusHours(9);
						String newDay = day + " " + hourLT.toString();
						
						if(vuelta.get(newDay) != null) {
							agentsReturn = vuelta.get(newDay);
							agentsReturn.add((double) (i+1));
							vuelta.put(newDay, agentsReturn);
						}else {
							agentsReturn = new ArrayList<Double>();
							agentsReturn.add((double)i + 1);
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

}
