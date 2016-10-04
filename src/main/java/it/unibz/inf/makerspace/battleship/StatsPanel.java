package it.unibz.inf.makerspace.battleship;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import it.unibz.inf.makerspace.battleship.game.GameStats;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

public class StatsPanel extends JPanel implements GameStats.FileListener {

	private static final long serialVersionUID = -433578733814612175L;
	
	private GameStats.FileWorker gameStatsWorker;
	private HashMap<String, GameStats.Info> gameStatsInfos;
	
	private JPanel listPanel;
	private JScrollPane scrollPane;
	private GridBagConstraints gridBagConstraints;
	
	public StatsPanel() {
		try {
			UIManager.setLookAndFeel(UIManager
					.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException
				| UnsupportedLookAndFeelException e) {
		}
		
		setLayout(new BorderLayout());
		listPanel = new JPanel();
		listPanel.setLayout(new GridBagLayout());
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 1;
		//gridBagConstraints.gridx = 0;
		gameStatsInfos = GameStats.sortByGameStats(GameStats.getGameStatsInfos());
		try {
			gameStatsWorker = new GameStats.FileWorker();
			gameStatsWorker.setGameStatsFileListener(this);
			gameStatsWorker.execute();
		} catch (IOException e) { }
		/*
		Set<Entry<String, GameStats.Info>> set = gameStatsInfos.entrySet();
		for (Iterator<Entry<String, GameStats.Info>> it = set.iterator(); it.hasNext();) {
			Entry<String, GameStats.Info> entry = it.next();
			System.out.println(entry.getValue().playerInfo.entryDate);
		}*/
		scrollPane = new JScrollPane(listPanel);
		scrollPane.setVerticalScrollBarPolicy(
				   JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 
		add(scrollPane);
		updateStatsList();
	}
	
	private void updateStatsList() {
		listPanel.removeAll();
		Set<Entry<String, GameStats.Info>> set = gameStatsInfos.entrySet();
		for (Iterator<Entry<String, GameStats.Info>> it = set.iterator(); it.hasNext();) {
			Entry<String, GameStats.Info> entry = it.next();
			listPanel.add(new GameInfoPanel(entry.getValue()), gridBagConstraints);
			listPanel.validate();
		}
		SwingUtilities.invokeLater(new Runnable() {
			   public void run() { 
				   scrollPane.getVerticalScrollBar().setValue(0);
			   }
			});
	}

	@Override
	public void onFileCreated(String fileName) {
	}

	@Override
	public void onFileModified(String fileName) {
		File userFile = new File(fileName);
		String name = userFile.getName();
		if (name.indexOf(".") > 0) {
		    name = name.substring(0, name.lastIndexOf("."));
		}
		System.out.println(name);
		if (gameStatsInfos.containsKey(name)) {
			gameStatsInfos.remove(name);
		}
		GameStats.Info info = GameStats.getGameStatsInfo(userFile);
		if(info != null) {
			gameStatsInfos.put(name, info);
		}
		gameStatsInfos = GameStats.sortByGameStats(gameStatsInfos);
		updateStatsList();
	}

	@Override
	public void onFileDeleted(String fileName) {
	}
	
	static class GameInfoPanel extends JPanel {
		
		private static final long serialVersionUID = 1753943789754785796L;
		
		private static final DateFormat formatter = new SimpleDateFormat("mm:ss:SSS");

		public GameInfoPanel(GameStats.Info info) {
			// TODO: update info
			setLayout(new GridLayout(1, 6));
			setBorder(new EmptyBorder(10, 10, 10, 10));
			add(new JLabel("Player: "));
			add(new JLabel(info.playerInfo.userName));
			add(new JLabel("Hit Points:" ));
			add(new JLabel("_"));
			add(new JLabel("Bombs used: "));
			add(new JLabel("_"));
			add(new JLabel("Time needed: "));
			add(new JLabel(formatter.format(new Date(100L))));
		}
	}
}
