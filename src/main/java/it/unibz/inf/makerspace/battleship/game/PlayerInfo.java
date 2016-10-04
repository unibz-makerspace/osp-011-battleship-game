package it.unibz.inf.makerspace.battleship.game;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PlayerInfo implements Serializable {
	
	private static final long serialVersionUID = -8659589024499801685L;
	
	public final String userName;
	public final String emailAddress;
	public final String entryDate;
	
	public PlayerInfo(String userName, String emailAddress) {
		this.userName = userName;
		this.emailAddress = emailAddress;
		entryDate = new SimpleDateFormat("yyyyMMddhhmmSSS").format(new Date());
	}
}
