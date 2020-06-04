import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import jade.core.AID;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class Airline extends Agent{

	private static final long serialVersionUID = 1L;
	private ContainerController container;

	@Override
	protected void setup() {

		try {
			
			jade.core.Runtime runtime = jade.core.Runtime.instance();
			Profile profile = new ProfileImpl();
			profile.setParameter(Profile.MAIN_HOST, "127.0.0.1");
			profile.setParameter(Profile.MAIN_PORT, profile.LOCAL_PORT);
			profile.setParameter(Profile.PLATFORM_ID, "DKJAH");
			
			container = runtime.createMainContainer(profile);
			
			System.out.println("Airline started...");
			
			BufferedReader br;
			br = new BufferedReader(new FileReader(new File("./Agent parameters.csv")));
			String line = br.readLine();
			line = br.readLine();
			int cont = 1;

			while(line != null) {
				
				String name = "Agente ";
				Object [] params = {line};
				container.createNewAgent(name + cont, CustomerServiceAgent.class.getName(), params).start();
				cont++;
				line = br.readLine();
				
			}
			br.close();
			
			
		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IOException: " + e.getMessage());
		} catch (StaleProxyException e) {
			System.out.println("StaleProxyException: " + e.getMessage());
		}
	}
}
