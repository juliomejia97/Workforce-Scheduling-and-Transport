import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import jade.core.Agent;

public class TransportSupervisor extends Agent {

	private static final long serialVersionUID = 1L;
	private HashMap<String, String[]> coordenates = new HashMap<String, String[]>();
	
	@Override
	protected void setup(){
		
		try {
			BufferedReader br;
			br = new BufferedReader(new FileReader(new File("./Coordenates.csv")));
			String line = br.readLine();
			
			while(line != null) {
				String []data = line.split(";");
				String ag = data[0];
				String [] coor = new String[2];
				coor[0] = data[1]; //X
				coor[1] = data[2]; //Y
				this.coordenates.put(ag, coor);
				
				line = br.readLine();
			}

			br.close();
			

		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException: " + e.getMessage());
			
		} catch (IOException e) {
			System.out.println("IOException: " + e.getMessage());
		}
		
		
	}
	

}
