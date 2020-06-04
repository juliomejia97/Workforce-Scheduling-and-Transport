import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class Main {
	
	private static ContainerController container;

	public static void main(String[] args) throws StaleProxyException {
		
		jade.core.Runtime runtime = jade.core.Runtime.instance();
		Profile profile = new ProfileImpl();
		profile.setParameter(Profile.MAIN_HOST, "127.0.0.1");
		profile.setParameter(Profile.MAIN_PORT, profile.LOCAL_PORT);
		profile.setParameter(Profile.PLATFORM_ID, "DKJAH");
		
		container = runtime.createMainContainer(profile);
		String name = "Airline";
		Object [] params = {};
		container.createNewAgent(name, Airline.class.getName(), params).start();

	}

}
