package it.unibz.inf.makerspace.battleship.firmata;

import it.unibz.inf.makerspace.battleship.Application;
import it.unibz.inf.makerspace.battleship.firmata.GameGrid.Tile.ChangeMessage;
import it.unibz.inf.makerspace.battleship.game.OnAttackTileSetListener;

import com.bortbort.arduino.FiloFirmata.Firmata;
import com.bortbort.arduino.FiloFirmata.MessageListener;
import com.bortbort.arduino.FiloFirmata.Messages.Message;
import com.bortbort.arduino.FiloFirmata.Messages.SystemResetMessage;

public class AttackGridArduino {
	
	private Arduino arduino;
	
	private MessageListener<Message> messageListener;
	private OnAttackTileSetListener onAttackTileSetListener;
	
	public AttackGridArduino() {
		arduino = null;
		onAttackTileSetListener = null;
	}
	
	public AttackGridArduino(Arduino arduino) {
		this();
		this.arduino = arduino;
		
		final Firmata firmata = this.arduino.getFirmata();
		final String firmwareName = this.arduino.getSketchName();
		
		messageListener = new MessageListener<Message>() {

			@Override
			public void messageReceived(Message message) {
				if(message instanceof ChangeMessage) {
					// FIXME: Use this received message in the game logic.
					ChangeMessage m = (ChangeMessage) message;
					if (Application.DEBUG_ENABLED) {
					System.out.println(
							"[" + firmwareName + "] row=" + m.getRow() +
							", column=" + m.getColumn());
					}
					if (onAttackTileSetListener != null) {
						onAttackTileSetListener.onAttackTileSet(
								m.getRow(), m.getColumn());
					}
				}
			}
		};
		firmata.addMessageListener(ChangeMessage.class, messageListener);
	}
	
	public void setTile(int row, int column, GameGrid.Tile.Type tileType) {
		if (arduino != null) {
			Firmata firmata = arduino.firmata;
			firmata.sendMessage(new GameGrid.Tile.TypeMessage(
					row, column, tileType));
		}
	}
	
	public void resetGrid() {
		if (arduino != null) {
			Firmata firmata = arduino.firmata;
			firmata.sendMessage(new SystemResetMessage());
		}
	}
	
	public void setOnAttackTileSetListener(
			OnAttackTileSetListener onAttackTileSetListener) {
		this.onAttackTileSetListener = onAttackTileSetListener;
	}
}
