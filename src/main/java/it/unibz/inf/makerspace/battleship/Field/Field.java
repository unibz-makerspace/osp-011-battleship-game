package it.unibz.inf.makerspace.battleship.Field;

import java.util.Arrays;

class Field
{
	//To each ship is assigned a number, so that it is possible to identify it
	private int[][] fieldA, fieldB;
	private int[] shipsA, shipsB;
	
	public void initialize()
	{
		shipsA = new int[] {2,2,4,4,4,4};
		shipsB = new int[] {2,2,4,4,4,4};
		
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
}