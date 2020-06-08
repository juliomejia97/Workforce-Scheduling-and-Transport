import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Chromosome implements Serializable{

	private static final long serialVersionUID = 1L;
	private ArrayList<Double> solution;
	private ArrayList<String[][]> timesolts;
	private int id;
	private double FO;
	private double fitness;
	private float fatherRate;

	public Chromosome(int id, int numAgents) {

		this.id = id;
		this.FO = 0;
		this.fitness = 0;
		this.fatherRate = 0;
		this.solution = new ArrayList<Double>();
		this.timesolts = new ArrayList<String[][]>();
		for(int i=0; i < numAgents; i++) {
			this.timesolts.add(new String[7][2]);
			this.solution.add(generateRandom());
		}
	}

	public void calculateSchedulingFO(HashMap<String, Integer> pA, HashMap<String, Integer> pB, HashMap<String, Integer> pC) {

		@SuppressWarnings("unchecked")
		HashMap<String, Integer> a = (HashMap<String, Integer>) pA.clone();
		@SuppressWarnings("unchecked")
		HashMap<String, Integer> b = (HashMap<String, Integer>) pB.clone();
		@SuppressWarnings("unchecked")
		HashMap<String, Integer> c = (HashMap<String, Integer>) pC.clone();
		@SuppressWarnings("unchecked")
		HashMap<String, Integer> max = (HashMap<String, Integer>) pA.clone();
		double maxValue = 0;

		for(String[][] actual: timesolts) {
			for(int i = 0; i < 7; i++) {
				String day = actual[i][0].split(" ")[0];
				String initialHour = actual[i][0].split(" ")[1];
				String activity = actual[i][1];
				HashMap<String, String> labor = getTimeSlotsAgent(initialHour, activity);
				for(Map.Entry<String, String> entry: labor.entrySet()) {
					String dayHour = day + " " + entry.getKey();
					String act = entry.getValue();
					switch(act) {
					case "A":
						if(a.get(dayHour) > 0)
							a.put(dayHour, a.get(dayHour) - 1);
						break;
					case "B":
						if(b.get(dayHour) > 0)
							b.put(dayHour, b.get(dayHour) - 1);
						break;
					case "C":
						if(c.get(dayHour) > 0)
							c.put(dayHour, c.get(dayHour) - 1);
						break;
					case "L":
						break;
					default:
						break;
					}
				}
			}
		}
		
		for(Map.Entry<String, Integer> actA: a.entrySet()) {
			String day = actA.getKey().split(" ")[0];
			LocalTime time = LocalTime.of(Integer.parseInt(actA.getKey().split(" ")[1].split(":")[0]), Integer.parseInt(actA.getKey().split(" ")[1].split(":")[1]));
			if(day.equalsIgnoreCase("Mar")) {
				if(time.compareTo(LocalTime.of(2, 0)) > 0) {
					this.FO += (actA.getValue() * 2);
				}else {
					this.FO += actA.getValue();
				}
			} else if(day.equalsIgnoreCase("Mie")) {
				if(time.compareTo(LocalTime.of(6, 0)) < 0) {
					this.FO += (actA.getValue() * 2);
				}else {
					this.FO += actA.getValue();
				}
			} else {
				this.FO += actA.getValue();
			}
		}
		
		for(Map.Entry<String, Integer> actB: b.entrySet()) {
			String day = actB.getKey().split(" ")[0];
			LocalTime time = LocalTime.of(Integer.parseInt(actB.getKey().split(" ")[1].split(":")[0]), Integer.parseInt(actB.getKey().split(" ")[1].split(":")[1]));
			if(day.equalsIgnoreCase("Mar")) {
				if(time.compareTo(LocalTime.of(2, 0)) > 0) {
					this.FO += (actB.getValue() * 2);
				}else {
					this.FO += actB.getValue();
				}
			} else if(day.equalsIgnoreCase("Mie")) {
				if(time.compareTo(LocalTime.of(6, 0)) < 0) {
					this.FO += (actB.getValue() * 2);
				}else {
					this.FO += actB.getValue();
				}
			} else {
				this.FO += actB.getValue();
			}
		}
		
		for(Map.Entry<String, Integer> actC: c.entrySet()) {
			String day = actC.getKey().split(" ")[0];
			LocalTime time = LocalTime.of(Integer.parseInt(actC.getKey().split(" ")[1].split(":")[0]), Integer.parseInt(actC.getKey().split(" ")[1].split(":")[1]));
			if(day.equalsIgnoreCase("Mar")) {
				if(time.compareTo(LocalTime.of(2, 0)) > 0) {
					this.FO += (actC.getValue() * 2);
				}else {
					this.FO += actC.getValue();
				}
			} else if(day.equalsIgnoreCase("Mie")) {
				if(time.compareTo(LocalTime.of(6, 0)) < 0) {
					this.FO += (actC.getValue() * 2);
				}else {
					this.FO += actC.getValue();
				}
			} else {
				this.FO += actC.getValue();
			}
		}
		
//		for(Map.Entry<String, Integer> maxAct: max.entrySet()) {
//			int mayor = 0;
//			if(a.get(maxAct.getKey()) > mayor) {
//				mayor = a.get(maxAct.getKey());
//			}
//			if(b.get(maxAct.getKey()) > mayor) {
//				mayor = b.get(maxAct.getKey());
//			}
//			if(c.get(maxAct.getKey()) > mayor) {
//				mayor = b.get(maxAct.getKey());
//			}
//			maxAct.setValue(mayor);
//		}
//		
//		for(Map.Entry<String, Integer> maxAct: max.entrySet()) {
//			maxValue += maxAct.getValue();
//		}
//		
//		this.FO += (maxValue * 25);
		
	}

	public HashMap<String, String> getTimeSlotsAgent(String initialHour, String permutation) {

		HashMap<String, String> labor = new HashMap<String, String>();
		LocalTime init = LocalTime.of(Integer.parseInt(initialHour.split(":")[0]), Integer.parseInt(initialHour.split(":")[1]));
		String firstAct = Character.toString(permutation.charAt(0));
		//Primeras dos horas se hacen la actividad 1
		labor.put(initialHour, firstAct);
		LocalTime next = init.plusMinutes(30);
		labor.put(next.toString(), firstAct);
		next = next.plusMinutes(30);
		labor.put(next.toString(), firstAct);
		next = next.plusMinutes(30);
		labor.put(next.toString(), firstAct);
		next = next.plusMinutes(30);
		labor.put(next.toString(), firstAct);
		//Proximas dos horas y media se hace la actividad 2
		String secAct = Character.toString(permutation.charAt(1));
		next = next.plusMinutes(30);
		labor.put(next.toString(), secAct);
		next = next.plusMinutes(30);
		labor.put(next.toString(), secAct);
		next = next.plusMinutes(30);
		labor.put(next.toString(), secAct);
		next = next.plusMinutes(30);
		labor.put(next.toString(), secAct);
		next = next.plusMinutes(30);
		labor.put(next.toString(), secAct);
		//Proximas dos horas se hace la actividad 3
		String thirdAct = Character.toString(permutation.charAt(2));
		next = next.plusMinutes(30);
		labor.put(next.toString(), thirdAct);
		next = next.plusMinutes(30);
		labor.put(next.toString(), thirdAct);
		next = next.plusMinutes(30);
		labor.put(next.toString(), thirdAct);
		next = next.plusMinutes(30);
		labor.put(next.toString(), thirdAct);
		//Ultimas dos horas y media se hace la actividad 4
		String fourthAct = Character.toString(permutation.charAt(3));
		next = next.plusMinutes(30);
		labor.put(next.toString(), fourthAct);
		next = next.plusMinutes(30);
		labor.put(next.toString(), fourthAct);
		next = next.plusMinutes(30);
		labor.put(next.toString(), fourthAct);
		next = next.plusMinutes(30);
		labor.put(next.toString(), fourthAct);
		next = next.plusMinutes(30);
		labor.put(next.toString(), fourthAct);
		
		return labor;

	}

	public ArrayList<Double> getSolution(){
		return solution;
	}

	public void setSolution(ArrayList<Double> solution) {
		this.solution = solution;
	}

	public double getFO() {
		return FO;
	}

	public void setFO(double fO) {
		FO = fO;
	}

	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	public float getFatherRate() {
		return fatherRate;
	}

	public ArrayList<String[][]> getTimesolts() {
		return timesolts;
	}

	public void setSolutionToTimeslots(int pos, String[][] sol) {
		this.timesolts.set(pos, sol);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setFatherRate(float fatherRate) {
		this.fatherRate = fatherRate;
	}

	public static double generateRandom(){
		Random rand = new Random();
		double upperbound = 47;
		int lowestbound = 0;
		double range = upperbound - lowestbound;
		double number = rand.nextDouble() * range;
		double shifted = number + lowestbound;
		return roundTwoDecimals(shifted, 2);
	}
	public static double roundTwoDecimals(double value,int places) {
		if (places < 0) throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}
}
