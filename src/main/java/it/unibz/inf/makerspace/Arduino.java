package it.unibz.inf.makerspace;

import com.bortbort.arduino.FiloFirmata.Firmata;
import com.bortbort.arduino.FiloFirmata.MessageListener;
import com.bortbort.arduino.FiloFirmata.Messages.*;

public class Arduino {
	
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
		firmwareName = sysexReportFirmwareMessage.getFirmwareName() + ".ino";
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
				if(message instanceof SysexStringDataMessage) {
					// Print out Firmata.sendString()
					SysexStringDataMessage m = (SysexStringDataMessage)message;
					System.out.println(
							"[" + firmwareName + "] " + m.getStringData()
					);
				}
			}
		};
		firmata.addMessageListener(
				SysexStringDataMessage.class, messageListener
		);
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
