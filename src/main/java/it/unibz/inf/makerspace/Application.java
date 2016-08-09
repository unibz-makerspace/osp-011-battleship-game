package it.unibz.inf.makerspace;

import java.util.ArrayList;
import java.util.List;

public class Application {
	
	private static List<Arduino> arduinos;
	
	public static void main(String[] args) {
		new Application();
	}
	
	public Application() {
		GameApplet.main("it.unibz.inf.makerspace.GameApplet");
		arduinos = new ArrayList<>();
	}
	
	public static void addArduino(String comPort) {
		arduinos.add(new Arduino(comPort));
	}
	
	public static Arduino getArduino(String comPort) {
		Arduino result = null;
		for(int i = 0; i < arduinos.size(); i++) {
			Arduino arduino = arduinos.get(i);
			if(arduino.comPort.equalsIgnoreCase(comPort)) {
				result = arduino;
				break;
			}
		}
		return result;
	}
	
	public static void removeArduino(String comPort) {
		for(int i = 0; i < arduinos.size(); i++) {
			Arduino arduino = arduinos.get(i);
			if(arduino.comPort.equalsIgnoreCase(comPort)) {
				arduino.stop();
				arduinos.remove(i);
			}
		}
	}
}
