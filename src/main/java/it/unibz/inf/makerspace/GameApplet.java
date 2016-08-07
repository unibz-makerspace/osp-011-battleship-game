package it.unibz.inf.makerspace;

import com.bortbort.arduino.FiloFirmata.Firmata;
import com.bortbort.arduino.FiloFirmata.Messages.*;

import processing.core.PApplet;
import processing.serial.Serial;

public class GameApplet extends PApplet {
	
	static final int DEFAULT_WIDTH = 400;
	static final int DEFAULT_HEIGHT = 400;
	
	// https://github.com/reapzor/FiloFirmata
	private Firmata firmataDevice;
	private ProtocolVersionMessage protocolVersionMessage;
	private SysexReportFirmwareMessage sysexReportFirmwareMessage;
	
	// https://github.com/processing/processing/wiki
	@Override
	public void settings() {
		size(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	@Override
	public void setup() {
		surface.setResizable(true);
		
		final String[] ports = Serial.list();
		if(ports.length != 0) {
			for(int i = 0; i < ports.length; i++) {
				print("" + (i+1) + ". port is: ");
				println(ports[i]);
			}
			getFirmataDevice(ports[0]);
		}
	}
	
	private void getFirmataDevice(String port) {
		firmataDevice = new Firmata(port);
		firmataDevice.start();
		protocolVersionMessage = firmataDevice.sendMessageSynchronous(
				ProtocolVersionMessage.class,
				new ProtocolVersionQueryMessage()
		);
		sysexReportFirmwareMessage = firmataDevice.sendMessageSynchronous(
				SysexReportFirmwareMessage.class,
		        new SysexReportFirmwareQueryMessage()
		);
		print("Arduino connected on port '" +
				port +
				"' with ");
		print("firmata protocol v" +
				protocolVersionMessage.getMajorVersion() +
				"." +
				protocolVersionMessage.getMinorVersion() +
				" and "
		);
		println("firmata firmware '" +
				sysexReportFirmwareMessage.getFirmwareName() +
				"' v" +
				sysexReportFirmwareMessage.getMajorVersion() +
				"." +
				sysexReportFirmwareMessage.getMinorVersion()
		);
	}

}
