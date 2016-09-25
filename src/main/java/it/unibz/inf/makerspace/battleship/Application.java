package it.unibz.inf.makerspace.battleship;

import it.unibz.inf.makerspace.battleship.firmata.Arduino;

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
		GameApplet.main("it.unibz.inf.makerspace.battleship.GameApplet");
		arduinos = new ArrayList<>();
		setup();
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
	
	private String[] comPorts;
	private Arduino[] comPortsArduinos;
	
	public void setup() {
		// Try to connect to an Arduino on each available serial port.
		// TODO: Probably this should be done in a seperate thread.
		comPorts = Application.getSerialComPorts();
		if(comPorts.length != 0) {
			for(int i = 0; i < comPorts.length; i++) {
				Application.addArduino(comPorts[i]);
			}
		}
		
		// TODO: Populate a Arduino ComPort list in the UI. This should happen
		// as soon all Arduino have been detected.
		// FIXME: Assign the Arduinos for the Laser grid and the RGB LED grid to
		// two different kind of variables. The Arduinos can be distinguished by
		// the firmwareName field of the firmata member of the arduino instance.
		comPortsArduinos = Application.getArduinos();
		for(Arduino arduino : comPortsArduinos) {
			System.out.println(arduino);
		}
	}
}
