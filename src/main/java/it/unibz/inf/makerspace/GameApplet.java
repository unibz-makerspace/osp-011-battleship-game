package it.unibz.inf.makerspace;

import processing.core.PApplet;

public class GameApplet extends PApplet {
	
	static final int DEFAULT_WIDTH = 400;
	static final int DEFAULT_HEIGHT = 400;
	
	private String[] comPorts;
	private Arduino[] arduinos;
	
	// https://github.com/processing/processing/wiki
	@Override
	public void settings() {
		size(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	@Override
	public void setup() {
		surface.setResizable(true);
		
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
		arduinos = Application.getArduinos();
		for(Arduino arduino : arduinos) {
			println(arduino);
		}
	}
}
