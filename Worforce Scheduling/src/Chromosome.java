import java.io.Serializable;

public class Chromosome implements Serializable{
	private float solution[][];
	private double FO;
	private double fitness;
	private float fatherRate;
	
	public Chromosome() {
		InitChromosome();
	}
	
	public void InitChromosome() {
		//TODO: Generate random solution
		solution = new float [7][7];
		/*
		 * [00:00 - 23:30]
		 * 
		 * */
	}

	public float[][] getSolution() {
		return solution;
	}

	public void setSolution(float[][] solution) {
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
	
	
}
