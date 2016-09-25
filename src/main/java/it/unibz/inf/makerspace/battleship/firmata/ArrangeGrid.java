package it.unibz.inf.makerspace.battleship.firmata;

import com.bortbort.arduino.FiloFirmata.Firmata;
import com.bortbort.arduino.FiloFirmata.Messages.Message;
import com.bortbort.arduino.FiloFirmata.Parser.SysexMessageBuilder;

/**
 *  The battleship arrange grid specification with Arduino Firmata protocol.
 * @author Julian Sanin
 */
public class ArrangeGrid {
	
	public static final int MAX_ROWS    = 8;
	public static final int MAX_COLUMNS = 8;
	
	public static final byte INVALID_VALUE = Byte.MAX_VALUE;
	
	static final byte ROW_CHANGE_MESSAGE    = 0x0D;
	static final byte COLUMN_CHANGE_MESSAGE = 0x0C;
	
	/**
	 * Message builder of received row change messages from the game grid via
	 * the Firmata protocol.
	 * @see {@link Firmata#addCustomSysexParser}
	 * @author Julian Sanin
	 *
	 */
	public static class RowChangeMessageBuilder extends SysexMessageBuilder {

		public RowChangeMessageBuilder() {
			super(ROW_CHANGE_MESSAGE);
		}

		@Override
		public Message buildMessage(byte[] messageBody) {
			if(messageBody.length >= 1) {
				byte row = messageBody[0];
				return new RowChangeMessage(row);
			}
			return new RowChangeMessage();
		}
	}
	
	/**
	 * Message builder of received column change messages from the game grid via
	 * the Firmata protocol.
	 * @see {@link Firmata#addCustomSysexParser}
	 * @author Julian Sanin
	 *
	 */
	public static class ColumnChangeMessageBuilder extends SysexMessageBuilder {

		public ColumnChangeMessageBuilder() {
			super(COLUMN_CHANGE_MESSAGE);
		}

		@Override
		public Message buildMessage(byte[] messageBody) {
			if(messageBody.length >= 1) {
				byte column = messageBody[0];
				return new ColumnChangeMessage(column);
			}
			return new ColumnChangeMessage();
		}
	}
	
	/**
	 * This type of message is received when a user has selected a row of
	 * the arrange grid.
	 * @author Julian Sanin
	 */
	public static class RowChangeMessage implements Message {
		
		private final byte row;
		
		public RowChangeMessage() {
			row = INVALID_VALUE;
		}
		
		public RowChangeMessage(byte row) {
			this.row = row;
		}
		
		public int getRow() {
			return row;
		}
	}
	
	/**
	 * This type of message is received when a user has selected a column of
	 * the arrange grid.
	 * @author Julian Sanin
	 */
	public static class ColumnChangeMessage implements Message {
		
		private final byte column;
		
		public ColumnChangeMessage() {
			column = INVALID_VALUE;
		}
		
		public ColumnChangeMessage(byte column) {
			this.column = column;
		}
		
		public int getColumn() {
			return column;
		}
	}

}
