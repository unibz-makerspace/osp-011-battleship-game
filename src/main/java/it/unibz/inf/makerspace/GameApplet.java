package it.unibz.inf.makerspace;

import processing.core.PApplet;
import processing.serial.Serial;

public class GameApplet extends PApplet {
	
	static final int DEFAULT_WIDTH = 400;
	static final int DEFAULT_HEIGHT = 400;
	
	private String[] comPorts;
	
	// https://github.com/processing/processing/wiki
	@Override
	public void settings() {
		size(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	@Override
	public void setup() {
		surface.setResizable(true);
		
		comPorts = Serial.list();
		if(comPorts.length != 0) {
			for(int i = 0; i < comPorts.length; i++) {
				// TODO: Populate a ComPort list in the UI and add the Arduinos.
				Application.addArduino(comPorts[i]);
				println(Application.getArduino(comPorts[i]));
			}
		}
	}
}
