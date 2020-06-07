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
import jade.lang.acl.UnreadableException;

public class CustomerServiceAgent extends Agent{
	
	/**
	 * 
	 */
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
		this.coorX = Double.parseDouble(data[1].trim());
		this.coorY = Double.parseDouble(data[2].trim());
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
	    //TODO: Look for other services that the agent offers
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
	
	public void GeneratePossiblesActivities()  {
		addBehaviour(new OneShotBehaviour() {
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
	
	//TODO: Recieve the decimal part of the AG and report my option 
	private class TimeSlotConfiguration extends CyclicBehaviour {
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
				//TODO:
				permutation = proposal%1;
				hour = (int) (proposal - permutation);
				position = (int) Math.round(permutation*opcions.size());
				if(position>0) {
					position--;
				}
				config = new String[7][2];
				date = getHour(hour);
				selection = opcions.get(position);
				for(Map.Entry<String, Boolean> actual:days.entrySet()) {
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
					default:
						break;
					}
				}
				 ACLMessage reply = msg.createReply();
				 reply.setPerformative(ACLMessage.INFORM);
				 Object info[] = {chr, config};
		    	try {
					reply.setContentObject(info);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
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
			  select = select + entero ;
			  if(hour%2!=0) {
				  select = select + ":30";
			  }else {
				  select = select + ":00";
			  }
			  
			  return select;
		  }
		}  // End of inner class OfferRequestsServer
		
	
	

}
