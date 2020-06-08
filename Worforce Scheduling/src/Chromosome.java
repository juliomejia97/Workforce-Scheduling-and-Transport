import java.io.Serializable;
import java.util.ArrayList;
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
		//Initialize the chromosome
		this.id = id;
		this.solution = new ArrayList<Double>();
		this.timesolts = new ArrayList<String[][]>();
		for(int i=0; i < numAgents; i++) {
			this.timesolts.add(new String[7][2]);
			this.solution.add(generateRandom());
		}
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
		int upperbound = 49;
		int lowestbound = 0;
		int range = upperbound - lowestbound;
		double number = rand.nextDouble() * range;
		double shifted = number + lowestbound;
		return roundTwoDecimals(shifted,2);
	}
	public static double roundTwoDecimals(double value,int places) {
		if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
}
