import java.time.LocalTime;

public class Dia {
	
	private String day;
	private LocalTime franja;
	
	public Dia(String pDia, LocalTime pFranja) {
		this.day = pDia;
		this.franja = pFranja;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public LocalTime getFranja() {
		return franja;
	}

	public void setFranja(LocalTime franja) {
		this.franja = franja;
	}
	
	

}
