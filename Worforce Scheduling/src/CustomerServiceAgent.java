import java.util.HashMap;

import jade.core.Agent;

public class CustomerServiceAgent extends Agent{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
	private double coorX;
	private double coorY;
	private boolean actA;
	private boolean actB;
	private boolean actC;
	private HashMap<String, Boolean> days = new HashMap<String, Boolean>();
	
	@Override
	protected void setup() {
		
		Object[] params = getArguments();
		String param = params[0].toString();
		
		String [] data = param.split(";");
		this.name = "Agente " + data[0].trim();
		this.coorX = Double.parseDouble(data[1].trim());
		this.coorY = Double.parseDouble(data[2].trim());
		this.actA = Boolean.parseBoolean(data[3].trim());
		this.actB = Boolean.parseBoolean(data[4].trim());
		this.actC = Boolean.parseBoolean(data[5].trim());
		this.days.put("Martes", Boolean.parseBoolean(data[6].trim()));
		this.days.put("Miercoles", Boolean.parseBoolean(data[7].trim()));
		this.days.put("Jueves", Boolean.parseBoolean(data[8].trim()));
		this.days.put("Viernes", Boolean.parseBoolean(data[9].trim()));
		this.days.put("Sabado", Boolean.parseBoolean(data[10].trim()));
		this.days.put("Domingo", Boolean.parseBoolean(data[11].trim()));
		this.days.put("Lunes", Boolean.parseBoolean(data[12].trim()));
		
		System.out.println(this.name + " started...");
		
	}
	
	

}
