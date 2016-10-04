package it.unibz.inf.makerspace.battleship.game;

import java.io.Serializable;

public class GameInfo implements Serializable  {

	private static final long serialVersionUID = 7996879751302976809L;
	
	public final int hitPoints;
	public final int bombsUsed;
	public final long timeNeeded;
	
	public GameInfo(int hitPoints, int bombsUsed, long timeNeeded) {
		this.hitPoints = hitPoints;
		this.bombsUsed = bombsUsed;
		this.timeNeeded = timeNeeded;
	}

}
