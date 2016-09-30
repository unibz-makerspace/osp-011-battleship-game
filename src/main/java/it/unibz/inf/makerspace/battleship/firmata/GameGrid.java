package it.unibz.inf.makerspace.battleship.firmata;

import java.io.ByteArrayOutputStream;

import com.bortbort.arduino.FiloFirmata.Firmata;
import com.bortbort.arduino.FiloFirmata.Messages.Message;
import com.bortbort.arduino.FiloFirmata.Messages.TransmittableSysexMessage;
import com.bortbort.arduino.FiloFirmata.Parser.SysexMessageBuilder;

/**
 *  The battleship game grid specification with Arduino Firmata protocol.
 * @author Julian Sanin
 */
public class GameGrid {
	
	public static final int MAX_ROWS = 8;
	public static final int MAX_COLUMNS = 8;
	
	/**
	 * The tile specification.
	 * @author Julian Sanin
	 */
	public static class Tile {
		
		public static final byte INVALID_VALUE = Byte.MAX_VALUE;
		
		static final byte TILE_TYPE_MESSAGE = 0x0F;
		static final byte TILE_CHANGE_MESSAGE = 0x0E;
		
		/**
		 * The allowed tile types of the game grid.
		 * @author Julian Sanin
		 */
		public enum Type {
			/**
			 * A not revealed tile.
			 */
			NONE      (0x00),
			/**
			 * A tile with no ship on it.
			 */
			WATER     (0x01),
			/**
			 * A tile with a ship on it that has been hit.
			 */
			HIT       (0x02),
			/**
			 * A tile with a ship on it that has been destroyed (All tiles of
			 * the ship has been hit).
			 */
			DESTROYED (0x03);
			
			public final byte item;
			
			Type(int item) {
				this.item = (byte) item;
			}
		}
		
		/**
		 * This type of message can be sent to the game grid to reveal the type
		 * of a tile. Initially all tiles are of type water. As soon as the
		 * other player starts to reveal a tile its type will be changed to
		 * either water, shit hit, or ship destroyed.
		 * @see {@link Type}
		 * @author Julian Sanin
		 */
		public static class TypeMessage extends TransmittableSysexMessage {
			
			private final byte item;
			private final byte row;
			private final byte column;
			private final Tile.Type type;

			public TypeMessage(int row, int column, Tile.Type type) {
				super(TILE_TYPE_MESSAGE);
				this.item = type.item;
				this.row = (byte) (row % MAX_ROWS);
				this.column = (byte) (column % MAX_COLUMNS);
				this.type = type;
			}

			@Override
			protected Boolean serialize(ByteArrayOutputStream outputStream) {
				outputStream.write(item);
				outputStream.write(row);
				outputStream.write(column);
				return true;
			}
			
			public int getRow() {
				return row;
			}
			
			public int getColumn() {
				return column;
			}
			
			public Tile.Type getType() {
				return type;
			}
		}
		
		/**
		 * Message builder of received change messages from the game grid via
		 * the Firmata protocol.
		 * @see {@link Firmata#addCustomSysexParser}
		 * @author Julian Sanin
		 *
		 */
		public static class ChangeMessageBuilder extends SysexMessageBuilder {

			public ChangeMessageBuilder() {
				super(TILE_CHANGE_MESSAGE);
			}

			@Override
			public Message buildMessage(byte[] messageBody) {
				if(messageBody.length >= 2) {
					byte row = messageBody[0];
					byte column = messageBody[1];
					return new ChangeMessage(row, column);
				}
				return new ChangeMessage();
			}

		}
		
		/**
		 * This type of message is received when a user has selected a tile of
		 * the game grid.
		 * @author Julian Sanin
		 */
		public static class ChangeMessage implements Message {
			
			private final byte row;
			private final byte column;
			
			public ChangeMessage() {
				row = INVALID_VALUE;
				column = INVALID_VALUE;
			}
			
			public ChangeMessage(byte row, byte column) {
				this.row = row;
				this.column = column;
			}
			
			public int getRow() {
				return row;
			}
			
			public int getColumn() {
				return column;
			}
		}
	}
}
