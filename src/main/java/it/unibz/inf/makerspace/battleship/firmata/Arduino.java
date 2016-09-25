package it.unibz.inf.makerspace.battleship.firmata;

import static it.unibz.inf.makerspace.battleship.firmata.ArrangeGrid.*;
import it.unibz.inf.makerspace.battleship.firmata.GameGrid.Tile.*;

import com.bortbort.arduino.FiloFirmata.Firmata;
import com.bortbort.arduino.FiloFirmata.MessageListener;
import com.bortbort.arduino.FiloFirmata.Messages.*;

/**
 * The Arduino connection via Firmata protocol.
 * @author Julian
 */
public class Arduino {
	
	static {
		Firmata.addCustomSysexParser(new ChangeMessageBuilder());
		Firmata.addCustomSysexParser(new RowChangeMessageBuilder());
		Firmata.addCustomSysexParser(new ColumnChangeMessageBuilder());
	}
	
	// https://github.com/reapzor/FiloFirmata
	public final Firmata firmata;
	public final String comPort;
	public final String firmwareName;
	public final int firmwareMajorVersion;
	public final int firmwareMinorVersion;
	public final int firmataMajorVersion;
	public final int firmataMinorVersion;
	
	private MessageListener<Message> messageListener;
	
	public Arduino(String comPort) throws RuntimeException {
		firmata = new Firmata(comPort);
		firmata.start();
		this.comPort = comPort;
		SysexReportFirmwareMessage sysexReportFirmwareMessage;
		sysexReportFirmwareMessage = firmata.sendMessageSynchronous(
				SysexReportFirmwareMessage.class,
		        new SysexReportFirmwareQueryMessage()
		);
		firmwareName = sysexReportFirmwareMessage.getFirmwareName();
		firmwareMajorVersion = sysexReportFirmwareMessage.getMajorVersion();
		firmwareMinorVersion = sysexReportFirmwareMessage.getMinorVersion();
		ProtocolVersionMessage protocolVersionMessage;
		protocolVersionMessage = firmata.sendMessageSynchronous(
				ProtocolVersionMessage.class,
				new ProtocolVersionQueryMessage()
		);
		firmataMajorVersion = protocolVersionMessage.getMajorVersion();
		firmataMinorVersion = protocolVersionMessage.getMinorVersion();
		
		messageListener = new MessageListener<Message>() {

			@Override
			public void messageReceived(Message message) {
				// Print debug messages from the Arduino to the console.
				if(message instanceof SysexStringDataMessage) {
					// Print out Firmata.sendString()
					SysexStringDataMessage m = (SysexStringDataMessage)message;
					System.out.println(
							"[" + firmwareName + "] " + m.getStringData()
					);
				} else if(message instanceof ChangeMessage) {
					// FIXME: Use this received message in the game logic.
					ChangeMessage m = (ChangeMessage) message;
					System.out.println(
							"[" + firmwareName + "] row=" + m.getRow() +
							", column=" + m.getColumn());
				} else if(message instanceof RowChangeMessage) {
					// FIXME: Use this received message in the game logic.
					RowChangeMessage m = (RowChangeMessage) message;
					System.out.println(
							"[" + firmwareName + "]    row=" + m.getRow());
				} else if(message instanceof ColumnChangeMessage) {
					// FIXME: Use this received message in the game logic.
					ColumnChangeMessage m = (ColumnChangeMessage) message;
					System.out.println(
							"[" + firmwareName + "] column=" + m.getColumn());
				}
			}
		};
		firmata.addMessageListener(
				SysexStringDataMessage.class, messageListener
		);
		firmata.addMessageListener(ChangeMessage.class, messageListener);
	}
	
	public void stop() {
		firmata.stop();
	}
	
	@Override
	public String toString() {
		return "[" + comPort + "] Firmata v" + firmataMajorVersion + "." +
				firmataMinorVersion + " " + "sketch '" + firmwareName +
				"' " + "v" + firmwareMajorVersion + "." +
				firmwareMinorVersion;
	}
}
