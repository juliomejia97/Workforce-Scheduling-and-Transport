import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import jade.core.Agent;

public class Airline extends Agent{

	
	private static final long serialVersionUID = 1L;
	

	public static void main(String[] args) throws IOException {
		
		BufferedReader br = new BufferedReader(new FileReader(new File("./Agent parameters.csv")));
		String line = br.readLine();
		
		while(line != null) {
			line = br.readLine();
			String [] data = line.split(";");
			String name = "Agente " + data[0].trim();
			double coorX = Double.parseDouble(data[1].trim());
			double coorY = Double.parseDouble(data[2].trim());
			boolean actA = Boolean.parseBoolean(data[3].trim());
			boolean actB = Boolean.parseBoolean(data[4].trim());
			boolean actC = Boolean.parseBoolean(data[5].trim());
			HashMap<String, Boolean> days = new HashMap<String, Boolean>();
			days.put("Martes", Boolean.parseBoolean(data[6].trim()));
			days.put("Miercoles", Boolean.parseBoolean(data[7].trim()));
			days.put("Jueves", Boolean.parseBoolean(data[8].trim()));
			days.put("Viernes", Boolean.parseBoolean(data[9].trim()));
			days.put("Sabado", Boolean.parseBoolean(data[10].trim()));
			days.put("Domingo", Boolean.parseBoolean(data[11].trim()));
			days.put("Lunes", Boolean.parseBoolean(data[12].trim()));
			
			System.out.print("Name: " + name);
			System.out.print(" X: " + coorX);
			System.out.print(" Y: " + coorY);
			System.out.print(" A: " + actA);
			System.out.print(" B: " + actB);
			System.out.print(" C: " + actC);
			System.out.println();

		}
		

		br.close();
	}

}
