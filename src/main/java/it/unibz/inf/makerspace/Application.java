package it.unibz.inf.makerspace;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import purejavacomm.CommPortIdentifier;

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
		try {
			arduinos.add(new Arduino(comPort));
		} catch(RuntimeException e) {
			// Do nothing if not an Arduino sketch loaded with firmata. 
		}
	}
	
	public static Arduino[] getArduinos() {
		return arduinos.toArray(new Arduino[arduinos.size()]);
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
	
	public static String[] getSerialComPorts() {
		ArrayList<String> serialComPorts = new ArrayList<>();
		@SuppressWarnings("rawtypes")
		Enumeration portIds = CommPortIdentifier.getPortIdentifiers();
		while(portIds.hasMoreElements()) {
			CommPortIdentifier portId = 
					(CommPortIdentifier)portIds.nextElement();
			if(portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				serialComPorts.add(portId.getName());
			}
		}
		return serialComPorts.toArray(new String[serialComPorts.size()]);
	}
}
