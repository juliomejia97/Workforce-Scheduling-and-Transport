import java.io.Serializable;
import java.util.ArrayList;

public class Chromosome implements Serializable{
	private Dia solution[][];
	private double FO;
	private double fitness;
	private float fatherRate;
	private ArrayList<ArrayList<Dia>> opcions;
	
	public Chromosome(ArrayList<ArrayList<Dia>> pOpcions) {
		opcions = pOpcions;
		InitChromosome();
	}
	
	public void InitChromosome() {
		//TODO: Generate random solution
		int contSchedules = 0;
		solution = new Dia [7][7];
		ArrayList<Boolean> estado;
		ArrayList<Dia> actual;
		//For each day generate a random configuration
		for(int i= 0; i < 7; i++) {
			actual = opcions.get(i);
			estado = new ArrayList<Boolean>(actual.size());
			while(contSchedules<7) {
				//Generate randoms bettewen 0 - array.size()
				
			}
		}
	}

	public Dia[][] getSolution() {
		return solution;
	}

	public void setSolution(Dia[][] solution) {
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
