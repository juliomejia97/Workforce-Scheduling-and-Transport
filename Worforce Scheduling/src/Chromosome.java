import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import jade.util.leap.HashMap;

public class Chromosome implements Serializable{
	private ArrayList<Double> solution;
	private ArrayList<String[][]> timesolts;
	private double FO;
	private double fitness;
	private float fatherRate;
	
	public Chromosome(int numAgents) {
		//Initialize the chromosome
		solution = new ArrayList<Double>();
		timesolts = new ArrayList<String[][]>(numAgents);
		for(int i=0; i < numAgents; i++) {
			solution.add(generateRandom());
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
