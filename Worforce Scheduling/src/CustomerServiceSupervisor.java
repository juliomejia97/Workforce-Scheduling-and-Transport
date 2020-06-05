import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import jade.core.Agent;

public class CustomerServiceSupervisor extends Agent{
	

	private static final long serialVersionUID = 1L;
	
	private HashMap<String, Integer> actA = new HashMap<String, Integer>();
	private HashMap<String, Integer> actB = new HashMap<String, Integer>();
	private HashMap<String, Integer> actC = new HashMap<String, Integer>();
	
	@Override
	protected void setup() {
		
		try {
			BufferedReader br;
			br = new BufferedReader(new FileReader(new File("./ActivityADemand.csv")));
			String line = br.readLine();
			
			while(line != null) {
				String [] data = line.split(";");
				String hour = data[0].trim();
				int demand = Integer.parseInt(data[1].trim());
				this.actA.put(hour, demand);
				line = br.readLine();
			}
			
			br.close();
			
			br = new BufferedReader(new FileReader(new File("./ActivityBDemand.csv")));
			line = br.readLine();
			
			while(line != null) {
				String [] data = line.split(";");
				String hour = data[0].trim();
				int demand = Integer.parseInt(data[1].trim());
				this.actB.put(hour, demand);
				line = br.readLine();
			}
			
			br.close();
			
			br = new BufferedReader(new FileReader(new File("./ActivityCDemand.csv")));
			line = br.readLine();
			
			while(line != null) {
				String [] data = line.split(";");
				String hour = data[0].trim();
				int demand = Integer.parseInt(data[1].trim());
				this.actC.put(hour, demand);
				line = br.readLine();
			}
			
			br.close();
			
			System.out.println("Supervisor started...");


		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IOException: " + e.getMessage());
		}		
	}
}
