import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class CustomerServiceAgent extends Agent{

	private static final long serialVersionUID = 1L;

	private String name;
	private double coorX;
	private double coorY;
	private boolean actA;
	private boolean actB;
	private boolean actC;
	private HashMap<String, Boolean> days = new HashMap<String, Boolean>();
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

		System.out.println(this.name + " started...");

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		//Offering different services
		ServiceDescription sd = new ServiceDescription();
		sd.setType("report-timeslot");
		sd.setName("JADE-scheduling");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		GeneratePossiblesActivities();
		addBehaviour(new TimeSlotConfiguration());
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
			int hour;
			int chr;
			int slot1;
			int slot2;
			int slot3;
			int slot4;
			int slot5;
			int slot6;
			int slot7;
			String[][] config;
			String message;
			String date;
			if (msg != null) {
				
				message = (String) msg.getContent();
				chr = Integer.parseInt(message.split(" ")[0]);
				hour = Integer.parseInt(message.split(" ")[1]); //Hora de inicio
				slot1 = (int) Double.parseDouble(message.split(" ")[2]) * opcions.size();
				if(slot1 > 0) {
					slot1--;
				}
				slot2 = (int) Double.parseDouble(message.split(" ")[3]) * opcions.size();
				if(slot2 > 0) {
					slot2--;
				}
				slot3 = (int) Double.parseDouble(message.split(" ")[4]) * opcions.size();
				if(slot3 > 0) {
					slot3--;
				}
				slot4 = (int) Double.parseDouble(message.split(" ")[5]) * opcions.size();
				if(slot4 > 0) {
					slot4--;
				}
				slot5 = (int) Double.parseDouble(message.split(" ")[6]) * opcions.size();
				if(slot5 > 0) {
					slot5--;
				}
				slot6 = (int) Double.parseDouble(message.split(" ")[7]) * opcions.size();
				if(slot6 > 0) {
					slot6--;
				}
				slot7 = (int) Double.parseDouble(message.split(" ")[8]) * opcions.size();
				if(slot7 > 0) {
					slot7--;
				}


				config = new String[7][2];
				date = getHour(hour);
				for(Map.Entry<String, Boolean> actual: days.entrySet()) {
					String key = actual.getKey();
					Boolean value = actual.getValue();
					switch (key) {
					case "Mar":
						config[0][0] = "Mar "+date;
						if(value) {
							config[0][1] = opcions.get(slot1);
						}else {
							config[0][1] = "LLLL";
						}
						break;
					case "Mie":
						config[1][0] = "Mie "+date;
						if(value) {
							config[1][1] = opcions.get(slot2);
						}else {
							config[1][1] = "LLLL";
						}
						break;
					case "Jue":
						config[2][0] = "Jue "+date;
						if(value) {
							config[2][1] = opcions.get(slot3);
						}else {
							config[2][1] = "LLLL";
						}
						break;
					case "Vie":
						config[3][0] = "Vie "+date;
						if(value) {
							config[3][1] = opcions.get(slot4);
						}else {
							config[3][1] = "LLLL";
						}
						break;
					case "Sab":
						config[4][0] = "Sab "+date;
						if(value) {
							config[4][1] = opcions.get(slot5);
						}else {
							config[4][1] = "LLLL";
						}
						break;
					case "Dom":
						config[5][0] = "Dom "+date;
						if(value) {
							config[5][1] = opcions.get(slot6);
						}else {
							config[5][1] = "LLLL";
						}
						break;
					case "Lun":
						config[6][0] = "Lun "+date;
						if(value) {
							config[6][1] = opcions.get(slot7);
						}else {
							config[6][1] = "LLLL";
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
}
