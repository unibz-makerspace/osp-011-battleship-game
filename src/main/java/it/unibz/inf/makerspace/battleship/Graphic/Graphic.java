package it.unibz.inf.makerspace.battleship.Graphic;

import java.awt.*;
import javax.swing.*;

public class Graphic extends JPanel
{
	JLabel[][] boxes = new JLabel[8][8];
	JPanel background;
	
	public void initialize()
	{
		background = new JPanel();
		background.setPreferredSize(new Dimension(450,450));
		background.setLayout(new GridLayout(8,8));
		
		for(int i=0;i<8;i++)
			for(int j=0;j<8;j++)
			{
				boxes[i][j].setBackground(Color.BLUE);
				background.add(boxes[i][j]);
			}
	}
	
	public void setShip(int startX, int startY, int finishX, int finishY)
	{
		if(startX == finishX)
			for(int i=startY;i<finishY+1;i++)
				boxes[startX][i].setBackground(Color.GREEN);
		else
			if(startY == finishY)
				for(int i=startX;i<finishX+1;i++)
					boxes[i][finishY].setBackground(Color.GREEN);
	}
	
	public int attack(int X, int Y)
	{
		if(boxes[X][Y].getBackground().BLUE != null)
			return -1;
		else
			return 1;
	}
}