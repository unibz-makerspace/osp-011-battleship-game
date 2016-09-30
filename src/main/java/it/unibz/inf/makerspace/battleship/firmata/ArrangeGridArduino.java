package it.unibz.inf.makerspace.battleship.firmata;

import it.unibz.inf.makerspace.battleship.Application;
import it.unibz.inf.makerspace.battleship.firmata.ArrangeGrid.ColumnChangeMessage;
import it.unibz.inf.makerspace.battleship.firmata.ArrangeGrid.RowChangeMessage;
import it.unibz.inf.makerspace.battleship.game.OnArrangeTileSetListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.bortbort.arduino.FiloFirmata.Firmata;
import com.bortbort.arduino.FiloFirmata.MessageListener;
import com.bortbort.arduino.FiloFirmata.Messages.Message;

public class ArrangeGridArduino {
	
	private static final int CLEAR_COORDINATES_MILLIS = 1000;
	
	private Arduino arduino;
	
	private MessageListener<Message> messageListener;
	
	private OnArrangeTileSetListener onArrangeTileSetListener = null;
	
	private List<Map<Axis, Integer>> shipCoordinates;
	
	private final ScheduledExecutorService scheduler =
		     Executors.newScheduledThreadPool(1);
	
	private final Runnable clearCoordinatesTask = new Runnable() {
		@Override
		public void run() {
			if ((Application.DEBUG_ENABLED) && (shipCoordinates.size() > 0)) {
				System.out.print("Cleared coordinate: ");
				for (Map<Axis, Integer> c : shipCoordinates) {
					if (c.containsKey(Axis.ROW)) {
						System.out.print("row=" + c.get(Axis.ROW) + " ");
					} else if(c.containsKey(Axis.COLUMN)) {
						System.out.print("col=" + c.get(Axis.COLUMN + " "));
					}
				}
				System.out.println();
			}
			shipCoordinates.clear();
		}
	};
	
	private enum Axis {
		ROW, COLUMN
	};
	
	public ArrangeGridArduino() {
		arduino = null;
	}
	
	public ArrangeGridArduino(Arduino arduino) {
		this.arduino = arduino;
		
		final Firmata firmata = this.arduino.getFirmata();
		final String firmwareName = this.arduino.getSketchName();
		
		shipCoordinates = new ArrayList<Map<Axis,Integer>>();
		
		messageListener = new MessageListener<Message>() {
			
			private ScheduledFuture<?> clearCoordinatesHandle = null;

			@Override
			public void messageReceived(Message message) {
				if(message instanceof RowChangeMessage) {
					clearCoordinatesHandle = scheduler.schedule(
							clearCoordinatesTask,
							CLEAR_COORDINATES_MILLIS, TimeUnit.MILLISECONDS);
					RowChangeMessage m = (RowChangeMessage) message;
					if (Application.DEBUG_ENABLED) {
						System.out.println("[" + firmwareName +
								"]    row=" + m.getRow()
						);
					}
					Map<Axis, Integer> coordinate =
							new HashMap<Axis, Integer>();
					coordinate.put(Axis.ROW, m.getRow());
					shipCoordinates.add(coordinate);
				} else if(message instanceof ColumnChangeMessage) {
					clearCoordinatesHandle = scheduler.schedule(
							clearCoordinatesTask,
							CLEAR_COORDINATES_MILLIS, TimeUnit.MILLISECONDS);
					ColumnChangeMessage m = (ColumnChangeMessage) message;
					if (Application.DEBUG_ENABLED) {
						System.out.println("[" + firmwareName +
								"] column=" + m.getColumn()
						);
					}
					Map<Axis, Integer> coordinate =
							new HashMap<Axis, Integer>();
					coordinate.put(Axis.COLUMN, m.getColumn());
					shipCoordinates.add(coordinate);
				}
				if (shipCoordinates.size() == 2) {
					if (clearCoordinatesHandle != null) {
						clearCoordinatesHandle.cancel(true);
					}
					if (onArrangeTileSetListener != null) {
						Map<Axis, Integer> c1 = shipCoordinates.get(0);
						Map<Axis, Integer> c2 = shipCoordinates.get(1);
						if ((c1.containsKey(Axis.ROW) &&
								c2.containsKey(Axis.COLUMN)) ||
							(c1.containsKey(Axis.COLUMN) &&
								c2.containsKey(Axis.ROW))) {
							int row = 0;
							int column = 0;
							for (Map<Axis, Integer> c : shipCoordinates) {
								if (c.containsKey(Axis.ROW)) {
									row = c.get(Axis.ROW);
								}
								if (c.containsKey(Axis.COLUMN)) {
									column = c.get(Axis.COLUMN);
								}
							}
							onArrangeTileSetListener.onArrangeTileSet(
									row, column);
						}
					}
					shipCoordinates.clear();
				}
			}
		};
		firmata.addMessageListener(RowChangeMessage.class, messageListener);
		firmata.addMessageListener(ColumnChangeMessage.class, messageListener);
	}
	
	public void setOnArrangeTileSetListener(
			OnArrangeTileSetListener onArrangeTileSetListener) {
		this.onArrangeTileSetListener = onArrangeTileSetListener;
	}
}
