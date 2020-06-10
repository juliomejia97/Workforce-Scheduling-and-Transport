import java.util.Random;

public class Solution {
	
	private int initHour;
	private double slot1;
	private double slot2;
	private double slot3;
	private double slot4;
	private double slot5;
	private double slot6;
	private double slot7;
	
	public Solution(){
		
		this.initHour = (int) Math.round(generateRandom(47));
		this.slot1 = generateRandom(1);
		this.slot2 = generateRandom(1);
		this.slot3 = generateRandom(1);
		this.slot4 = generateRandom(1);
		this.slot5 = generateRandom(1);
		this.slot6 = generateRandom(1);
		this.slot7 = generateRandom(1);
		
	}
	
	public static double generateRandom(double upperbound){
		Random rand = new Random();
		int lowestbound = 0;
		double range = upperbound - lowestbound;
		double number = rand.nextDouble() * range;
		double shifted = number + lowestbound;
		return roundTwoDecimals(shifted, 2);
	}
	
	public static double roundTwoDecimals(double value,int places) {
		
		if (places < 0) throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}

	public int getInitHour() {
		return initHour;
	}

	public void setInitHour(int initHour) {
		
		if(initHour > 47) {
			this.initHour = initHour % 47;
		} else if(initHour < 0) {
			this.initHour = 47 + initHour;
		} else {
			this.initHour = initHour;
		}
		
	}

	public double getSlot1() {
		return slot1;
	}

	public void setSlot1(double slot1) {
		if(slot1 > 1) {
			this.slot1 = slot1 % 1;
		} else if(slot1 < 0) {
			this.slot1 = 1 + slot1;
		} else {
			this.slot1 = slot1;
		}
	}

	public double getSlot2() {
		return slot2;
	}

	public void setSlot2(double slot2) {
		if(slot2 > 1) {
			this.slot2 = slot2 % 1;
		} else if(slot2 < 0) {
			this.slot2 = 1 + slot2;
		} else {
			this.slot2 = slot2;
		}
	}

	public double getSlot3() {
		return slot3;
	}

	public void setSlot3(double slot3) {
		if(slot3 > 1) {
			this.slot3 = slot3 % 1;
		} else if(slot3 < 0) {
			this.slot3 = 1 + slot3;
		} else {
			this.slot3 = slot3;
		}
	}

	public double getSlot4() {
		return slot4;
	}

	public void setSlot4(double slot4) {
		if(slot4 > 1) {
			this.slot4 = slot4 % 1;
		} else if(slot4 < 0) {
			this.slot4 = 1 + slot4;
		} else {
			this.slot4 = slot4;
		}
	}

	public double getSlot5() {
		return slot5;
	}

	public void setSlot5(double slot5) {
		if(slot5 > 1) {
			this.slot5 = slot5 % 1;
		} else if(slot5 < 0) {
			this.slot5 = 1 + slot5;
		} else {
			this.slot5 = slot5;
		}
	}

	public double getSlot6() {
		return slot6;
	}

	public void setSlot6(double slot6) {
		if(slot6 > 1) {
			this.slot6 = slot6 % 1;
		} else if(slot6 < 0) {
			this.slot6 = 1 + slot6;
		} else {
			this.slot6 = slot6;
		}
	}

	public double getSlot7() {
		return slot7;
	}

	public void setSlot7(double slot7) {
		if(slot7 > 1) {
			this.slot7 = slot7 % 1;
		} else if(slot7 < 0) {
			this.slot7 = 1 + slot7;
		} else {
			this.slot7 = slot7;
		}
	}
	
	public String toString() {
		return this.initHour + " " + this.slot1 + " " + this.slot2 + " " + this.slot3 + " " + this.slot4 + " " + this.slot5 + " " + this.slot6 + " " + this.slot7;
	}
	
	

}
