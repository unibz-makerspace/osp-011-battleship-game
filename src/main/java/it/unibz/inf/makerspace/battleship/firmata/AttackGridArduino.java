package it.unibz.inf.makerspace.battleship.firmata;

import it.unibz.inf.makerspace.battleship.firmata.GameGrid.Tile.ChangeMessage;

import com.bortbort.arduino.FiloFirmata.Firmata;
import com.bortbort.arduino.FiloFirmata.MessageListener;
import com.bortbort.arduino.FiloFirmata.Messages.Message;

public class AttackGridArduino {
	
	private Arduino arduino;
	
	private MessageListener<Message> messageListener;
	
	public AttackGridArduino() {
		arduino = null;
	}
	
	public AttackGridArduino(Arduino arduino) {
		this.arduino = arduino;
		
		final Firmata firmata = this.arduino.getFirmata();
		final String firmwareName = this.arduino.getSketchName();
		
		messageListener = new MessageListener<Message>() {

			@Override
			public void messageReceived(Message message) {
				if(message instanceof ChangeMessage) {
					// FIXME: Use this received message in the game logic.
					ChangeMessage m = (ChangeMessage) message;
					System.out.println(
							"[" + firmwareName + "] row=" + m.getRow() +
							", column=" + m.getColumn());
				}
			}
		};
		firmata.addMessageListener(ChangeMessage.class, messageListener);
	}

}
