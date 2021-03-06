package it.unibz.inf.makerspace.battleship.Field;

import java.util.Arrays;

public class Field
{
	//To each ship is assigned a number, so that it is possible to identify it
	private int[][] fieldA, fieldB;
	private int[] shipsA, shipsB;
	
	public void initialize()
	{
		fieldA = new int[8][8];
		fieldB = new int[8][8]; 
		
		Arrays.fill(fieldA, 0);
		Arrays.fill(fieldB, 0);
	}

	public void setShip(String field, int xStart, int yStart, int xFinish, int yFinish)
	{
		
		if(field.equals("A"))
		{
			if(xStart == xFinish)
				for(int i=yStart; i<=yFinish; i++)
					fieldA[xStart][i] = xStart*yStart*xFinish*yFinish;
			else
				for(int i=xStart; i<=xFinish; i++)
					fieldA[yStart][i] = xStart*yStart*xFinish*yFinish;
		}
		else
		{
			if(xStart == xFinish)
				for(int i=yStart; i<=yFinish; i++)
					fieldA[xStart][i] = xStart*yStart*xFinish*yFinish;
			else
				for(int i=xStart; i<=xFinish; i++)
					fieldA[yStart][i] = xStart*yStart*xFinish*yFinish;
		}
	}
	
	public int attack(String player, int x, int y)
	{
		boolean killedx = true, killedy = true; 
		int ship;
		if(player.equals("B"))
		{
			if(fieldA[x][y]==0)
				return 0;
			
			ship = fieldA[x][y];
			fieldA[x][y]=0;

			for(int i=0;i<fieldA.length;i++)
				if(fieldA[i][y] == ship)
					killedx = false;
			
			for(int i=0;i<fieldA[x].length;i++)
				if(fieldA[x][i] == ship)
					killedy = false;
		}
		else
			if(player.equals("A"))
			{
				if(fieldB[x][y]==0)
					return 0;
			
				ship = fieldB[x][y];
				fieldB[x][y]=0;
				
				for(int i=0;i<fieldB.length;i++)
					if(fieldB[i][y] == ship)
						killedx = false;
			
				for(int i=0;i<fieldB[x].length;i++)
					if(fieldB[x][i] == ship)
						killedy = false;
			}
			else
				return -1;
		
		if(killedx && killedy)
			return 2;
		else
			return 1;
	}
}