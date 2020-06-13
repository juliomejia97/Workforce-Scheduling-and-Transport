import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class CustomerServiceAgent extends Agent{

	private static final long serialVersionUID = 1L;

	private String name;
	private double coorX;
	private double coorY;
	private boolean actA;
	private boolean actB;
	private boolean actC;
	private HashMap<String, Boolean> days = new HashMap<String, Boolean>();
	private HashMap<String, Integer> expectedProposesGoing = new HashMap<String, Integer>();
	private ArrayList<Double> distances = new ArrayList<Double>();
	private String activities;
	private ArrayList<String> opcions;
	
	@Override
	protected void setup() {
		activities = "";
		opcions = new ArrayList<String>();
		Object[] params = getArguments();
		String param = params[0].toString();

		String [] data = param.split(";");
		this.name = "Agente " + data[0].trim();
		this.setCoorX(Double.parseDouble(data[1].trim()));
		this.setCoorY(Double.parseDouble(data[2].trim()));
		this.actA = Boolean.parseBoolean(data[3].trim());
		if(actA) activities = activities + "A";
		this.actB = Boolean.parseBoolean(data[4].trim());
		if(actB) activities = activities + "B";
		this.actC = Boolean.parseBoolean(data[5].trim());
		if(actC) activities = activities + "C";
		this.days.put("Mar", Boolean.parseBoolean(data[6].trim()));
		this.days.put("Mie", Boolean.parseBoolean(data[7].trim()));
		this.days.put("Jue", Boolean.parseBoolean(data[8].trim()));
		this.days.put("Vie", Boolean.parseBoolean(data[9].trim()));
		this.days.put("Sab", Boolean.parseBoolean(data[10].trim()));
		this.days.put("Dom", Boolean.parseBoolean(data[11].trim()));
		this.days.put("Lun", Boolean.parseBoolean(data[12].trim()));
		this.days.put("Mar2", Boolean.parseBoolean(data[13].trim()));
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("./Distances.csv")));
			String line = br.readLine();
			while(line != null) {
				String [] distLine = line.split(";");
				if(distLine[0].equalsIgnoreCase(this.name)) {
					for(int i = 1; i < distLine.length; i++) {
						this.distances.add(Double.parseDouble(distLine[i].trim()));
					}
				}
				line = br.readLine();
			}
			
			br.close();
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		

		System.out.println(this.name + " started...");

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		//Offering different services
		ServiceDescription sd = new ServiceDescription();
		sd.setType("report-timeslot");
		sd.setName("JADE-scheduling");
		dfd.addServices(sd);
		ServiceDescription sd2 = new ServiceDescription();
		sd2.setType("transport");
		sd2.setName("JADE-transport");
		dfd.addServices(sd2);
		ServiceDescription sd3 = new ServiceDescription();
		sd3.setType("situation");
		sd3.setName("JADE-transport");
		dfd.addServices(sd3);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		GeneratePossiblesActivities();
		addBehaviour(new TimeSlotConfiguration());
		addBehaviour(new asLeaderGoing());
		addBehaviour(new ReceiveMessageFromLeaders());
		addBehaviour(new ReceiveExpectedProposesGoing());
	}

	public double getCoorX() {
		return coorX;
	}

	public void setCoorX(double coorX) {
		this.coorX = coorX;
	}

	public double getCoorY() {
		return coorY;
	}

	public void setCoorY(double coorY) {
		this.coorY = coorY;
	}

	public void GeneratePossiblesActivities()  {
		addBehaviour(new OneShotBehaviour() {

			private static final long serialVersionUID = 1L;

			public void action() {		        
				permutationWithRepeation(activities);		    
			}
		} );

	}

	public void permutationWithRepeation(String str1) {
		showPermutation(str1, "");
	}

	public void showPermutation(String str1, String NewStringToPrint) {
		if (NewStringToPrint.length() == 4) {
			opcions.add(NewStringToPrint);
			return;
		}
		for (int i = 0; i < str1.length(); i++) {

			showPermutation(str1, NewStringToPrint + str1.charAt(i));
		}
	}

	private class TimeSlotConfiguration extends CyclicBehaviour {

		private static final long serialVersionUID = 1L;
		MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);

		public void action() {
			ACLMessage msg = myAgent.receive(mt);
			Double proposal;
			Double permutation;
			int hour;
			int position;
			int chr;
			String[][] config;
			String message;
			String selection;
			String date;
			if (msg != null) {
				message = (String) msg.getContent();
				chr = Integer.parseInt(message.split(" ")[0]);
				proposal = Double.parseDouble(message.split(" ")[1]);
				permutation = proposal%1;
				hour = (int) (proposal - permutation);
				position = (int) Math.round(permutation*opcions.size());
				if(position>0) {
					position--;
				}
				config = new String[8][2];
				date = getHour(hour);
				selection = opcions.get(position);
				for(Map.Entry<String, Boolean> actual: days.entrySet()) {
					String key = actual.getKey();
					Boolean value = actual.getValue();
					switch (key) {
					case "Mar":
						config[0][0] = "Mar "+date;
						if(value) {
							config[0][1] = selection;
						}else {
							config[0][1] = "LLLL";
						}
						break;
					case "Mie":
						config[1][0] = "Mie "+date;
						if(value) {
							config[1][1] = selection;
						}else {
							config[1][1] = "LLLL";
						}
						break;
					case "Jue":
						config[2][0] = "Jue "+date;
						if(value) {
							config[2][1] = selection;
						}else {
							config[2][1] = "LLLL";
						}
						break;
					case "Vie":
						config[3][0] = "Vie "+date;
						if(value) {
							config[3][1] = selection;
						}else {
							config[3][1] = "LLLL";
						}
						break;
					case "Sab":
						config[4][0] = "Sab "+date;
						if(value) {
							config[4][1] = selection;
						}else {
							config[4][1] = "LLLL";
						}
						break;
					case "Dom":
						config[5][0] = "Dom "+date;
						if(value) {
							config[5][1] = selection;
						}else {
							config[5][1] = "LLLL";
						}
						break;
					case "Lun":
						config[6][0] = "Lun "+date;
						if(value) {
							config[6][1] = selection;
						}else {
							config[6][1] = "LLLL";
						}
						break;
					case "Mar2":
						config[7][0] = "Mar2 "+date;
						if(value) {
							config[7][1] = selection;
						}else {
							config[7][1] = "LLLL";
						}
						break;
					default:
						break;
					}
				}
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.INFORM);
				reply.setConversationId("report-option");
				Object info[] = {chr, config};
				try {
					reply.setContentObject(info);

				} catch (IOException e) {
					e.printStackTrace();
				}
				myAgent.send(reply);


			}else {
				block();
			}
		}
		
		public String getHour(int hour) {
			String select;
			select = "";
			int entero = hour / 2;
			if(entero < 10) {
				select = "0" + select + entero ;
			}else {
				select = select + entero ;
			}
			if(hour%2!=0) {
				select = select + ":30";
			}else {
				select = select + ":00";
			}

			return select;
		}
	} 
	private class asLeaderGoing extends CyclicBehaviour {
		
		HashMap<String, ArrayList<AID>> posibilities = new HashMap<String, ArrayList<AID>>();
		MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("route-as-leaderGoing"),
				MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF));
						
		@SuppressWarnings("unchecked")
		@Override
		public void action() {
			ACLMessage msg = myAgent.receive(mt);
			Object[] content;
			String dayHour;
			ArrayList<Integer> options;
			Integer myId = Integer.parseInt(name.split(" ")[1]);
			if(msg!=null) {
				
				try {
					content = (Object[]) msg.getContentObject();
					dayHour = (String) content[0];
					options = (ArrayList<Integer>) content[1];
					searchAgents(dayHour, options);
					
					System.out.println("Soy el lider " + name);
					for(Map.Entry<String, ArrayList<AID>> actual: posibilities.entrySet()) {
						System.out.print(actual.getKey() + " ");
						for(AID agent: actual.getValue()) {
							System.out.print(agent.getName() + " ");
						}
						System.out.println();
					}
					
					sendProposals(dayHour);
										
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
				
			}else {
				block();
			}
		}
		
		public void searchAgents(String dayHour, ArrayList<Integer> agents) {
			
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription serviceD = new ServiceDescription();
			serviceD.setType("situation");
			template.addServices(serviceD);
			try {
				DFAgentDescription[] result = DFService.search(myAgent, template);
				for(int i=0; i < result.length; i++) {
					Integer id = Integer.parseInt(result[i].getName().getName().split("@")[0].split(" ")[1]);
					if(agents.contains(id)) {
						if(posibilities.get(dayHour) != null) {
							ArrayList<AID> aux = this.posibilities.get(dayHour);
							aux.add(result[i].getName());
							this.posibilities.put(dayHour, aux);
						}else {
							ArrayList<AID> aux = new ArrayList<AID>();
							aux.add(result[i].getName());
							this.posibilities.put(dayHour, aux);
						}
					}

				}
			} catch (FIPAException e) {
				e.printStackTrace();
			}
		}
		
		public void sendProposals(String dayHour) {
			
			ArrayList<AID> agents = this.posibilities.get(dayHour);
			for(AID actual: agents) {
				ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
				msg.setConversationId("propose-car");
				msg.addReceiver(actual);
				msg.setContent("Hola quiero una respuesta tuya");
				myAgent.send(msg);
			}

		}
		
	}
	
	private class ReceiveExpectedProposesGoing extends CyclicBehaviour {
		
		MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("inform-size-leaders-going"),
				MessageTemplate.MatchPerformative(ACLMessage.INFORM));

		@Override
		public void action() {
			
			ACLMessage msg = myAgent.receive(mt);
			if(msg != null) {
				try {
					Object[] param = (Object[]) msg.getContentObject();
					String dayHour = param[0].toString();
					int cantReplies = Integer.parseInt(param[1].toString());
					expectedProposesGoing.put(dayHour, cantReplies);
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
				
				
				
			}else {
				block();
			}
		}
		
	}
	
	private class ReceiveMessageFromLeaders extends CyclicBehaviour {
		
		MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("propose-car"),
				MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));

		@Override
		public void action() {
			
			ACLMessage msg = myAgent.receive(mt);
			if(msg != null) {
				int idAgent = Integer.parseInt(msg.getSender().getName().split("@")[0].split(" ")[1]);
				System.out.print("El lider " + msg.getSender().getName() + " me ha enviado un mensaje de propuesta. Yo soy el agente: " + name);
				System.out.print(" Nuestra distancia es: " + distances.get(idAgent));
				System.out.println();
			}else {
				block();
			}
		}
		
	}
}
