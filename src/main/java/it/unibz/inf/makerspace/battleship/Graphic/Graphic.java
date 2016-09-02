package it.unibz.inf.makerspace.battleship.Graphic;

import java.awt.*;
import java.awt.ActionListener;
import javax.swing.*;
import it.unibz.inf.makerspace.battleship.Field.*;

public class Graphic extends JPanel
{
	JLabel[][] boxes = new JLabel[8][8];
	JPanel background, options, back, notification;
	Field field;
	JButton startFinish;
	JFrame frame = new JFrame("Awesome Battleship Game");
	String player;

	public void initialize(String p)
	{
		player = p;
		field = new Field();
		startFinish = new JButton("Start Game");
		JLabel labelNotification = new JLabel("Set the ships and click \"Start Game\"");
		notification = new JPanel();
		notification.add(labelNotification);
		back = new JPanel();
		options = new JPanel();
		background = new JPanel();
		background.setPreferredSize(new Dimension(450,450));
		background.setLayout(new GridLayout(8,8));
		
		for(int i=0;i<8;i++)
			for(int j=0;j<8;j++)
			{
				boxes[i][j].setBackground(Color.BLUE);
				background.add(boxes[i][j]);
			}
		
		options.setLayout(new BoxLayout(1,2));
		options.add(notification);
		options.add(startFinish);
		back.setLayout(new BoxLayout(2,1));
		back.add(options);
		back.add(background);
		frame.add(back);
	}
	
	public void setShip(int startX, int startY, int finishX, int finishY)
	{
		if(startX == finishX)
		{
			for(int i=startY;i<finishY+1;i++)
				boxes[startX][i].setBackground(Color.GREEN);
			labelNotification.setText("");
		}
		else
			if(startY == finishY)
			{
				for(int i=startX;i<finishX+1;i++)
					boxes[i][finishY].setBackground(Color.GREEN);
				labelNotification.setText("");
			}
			else
				labelNotification.setText("Only horizontal or vertical ships are allowed!");
		field.setShip(player,startX,startY,finishX,finishY);
	}
	
	public void attack(int X, int Y)
	{
		int result = field.attack(player, X, Y);
		if(result == 0)
		{
			labelNotification.setText("Missed!");
			return;
		}
		if(result == 1)
		{
			labelNotification.setText("You are getting on the correct way!");
			boxes[X][Y].setBackground(Color.RED);
			return;
		}
		if(result == 2)
		{
			labelNotification.setText("You have destroyed an enemy's ship!");
			boxes[X][Y].setBackground(Color.RED);
			return;
		}
		if(result == -1)
		{
			labelNotification.setText("Something went wrong, try again");
			return;
		}
	}
}

private class ButtonListener implements ActionListener
{
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource().getText().equals("Start Game"))
		{
			for(int i=0;i<8;i++)
				for(int j=0;j<8;j++)
					boxes[i][j].setBackground(Color.BLUE);
			e.getSource().setText("Surrend");
		}	
		else
			if(e.getSource().getText().equals("Surrender"))
			{
				labelNotification.setText("You have decided to surrender, you loose");
				//send message to the opponent that the player has just surrended
			}
	}
}