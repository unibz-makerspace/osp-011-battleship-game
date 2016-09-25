package it.unibz.inf.makerspace.battleship;

import it.unibz.inf.makerspace.battleship.firmata.Arduino;
import it.unibz.inf.makerspace.battleship.firmata.GameGrid;
import it.unibz.inf.makerspace.battleship.firmata.GameGrid.Tile;
import it.unibz.inf.makerspace.battleship.firmata.GameGrid.Tile.TypeMessage;
import processing.core.PApplet;

public class GameApplet extends PApplet {
	
	static final int DEFAULT_WIDTH = 400;
	static final int DEFAULT_HEIGHT = 400;
	
	private String[] comPorts;
	private Arduino[] arduinos;
	
	private int tSecond;
	
	// https://github.com/processing/processing/wiki
	@Override
	public void settings() {
		size(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	@Override
	public void setup() {
		surface.setResizable(true);
		/*
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
		arduinos = Application.getArduinos();
		for(Arduino arduino : arduinos) {
			println(arduino);
		}
		*/
		tSecond = second();
	}
	
	@Override
	public void draw() {
		// TODO: Update Game UI.
		/*
		// FIXME: Transmit real game events to the Arduino from within the game
		// logic.
		final int tCurrentSecond = second();
		if(tSecond != tCurrentSecond) {
			Tile.Type types[] = {
					Tile.Type.WATER, Tile.Type.HIT, Tile.Type.DESTROYED
			};
			for(Arduino arduino : arduinos) {
				int randomRow = (int) random(GameGrid.MAX_ROWS);
				int randomColumn = (int) random(GameGrid.MAX_COLUMNS);
				Tile.Type type = types[(int) random(types.length)];
				arduino.firmata.sendMessage(
						new TypeMessage(
								randomRow,
								randomColumn,
								type
						)
				);
			}
			tSecond = tCurrentSecond;
		}
		*/
	}
}
