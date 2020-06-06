import java.lang.reflect.Array;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;

public class GeneticAlgorithmAgent extends Agent{
	
	private CSSupervisorGUI myGui;
	private int population;
	private float mutationRate;
	private float crossoverRate;
	private float elitRate;
	private float threshold;
	private int maxIteration;
	private ArrayList<ArrayList<Dia>> opcions;
	private HashMap<Dia, Integer> demand = new HashMap<Dia, Integer>();
	
	protected void setup() {
		super.setup();
		opcions = new ArrayList<ArrayList<Dia>>();
		System.out.println("Genetic Algorithm Agent have start....");
		Object[] args = getArguments();
		population = Integer.parseInt(args[0].toString());
		mutationRate = Float.parseFloat(args[1].toString());
		crossoverRate = Float.parseFloat(args[2].toString());
		elitRate = Float.parseFloat(args[3].toString());
		demand = (HashMap<Dia, Integer>) args[4];
		
		//ArrayList of days
		ArrayList<Dia> tue = new ArrayList<Dia>();
		ArrayList<Dia> wed = new ArrayList<Dia>();
		ArrayList<Dia> thu = new ArrayList<Dia>();
		ArrayList<Dia> fri = new ArrayList<Dia>();
		ArrayList<Dia> sat = new ArrayList<Dia>();
		ArrayList<Dia> sun = new ArrayList<Dia>();
		ArrayList<Dia> mon = new ArrayList<Dia>();
		for(Map.Entry<Dia, Integer> entry: demand.entrySet()) {
			Dia key = entry.getKey();
			switch (key.getDay()) {
			case "Mar":
					tue.add(key);
				break;
			case "Mie":
					wed.add(key);
				break;
			case "Jue":
					thu.add(key);
				break;
			case "Vie":
					fri.add(key);
				break;
			case "Sab":
					sat.add(key);
				break;
			case "Dom":
					sun.add(key);
				break;
			case "Lun":
					mon.add(key);
				break;
			default:
				break;
			}
		}
		
		tue = sortArrays(tue);
		wed = sortArrays(wed);
		thu = sortArrays(thu);
		fri = sortArrays(fri);
		sat = sortArrays(sat);
		sun = sortArrays(sun);
		mon = sortArrays(mon);
		
		opcions.add(tue);
		opcions.add(wed);
		opcions.add(thu);
		opcions.add(fri);
		opcions.add(sat);
		opcions.add(sun);
		opcions.add(mon);
		//TODO: Generate the number of chromosomes of population size
	}
	
	public ArrayList<Dia> sortArrays(ArrayList<Dia> lst) {
		lst.sort(new Comparator<Dia>() {

			@Override
			public int compare(Dia franja1, Dia franja2) {
				return franja1.getFranja().compareTo(franja2.getFranja());
			}
		});
		return lst;
	}
	
	/*
	ACLMessage mess = new ACLMessage(ACLMessage.REQUEST);
	mess.setContentObject((Object) object);
	*/

}
