package it.unibz.inf.makerspace.battleship;

import it.unibz.inf.makerspace.battleship.firmata.Arduino;
import it.unibz.inf.makerspace.battleship.firmata.ArrangeGridArduino;
import it.unibz.inf.makerspace.battleship.firmata.AttackGridArduino;
import it.unibz.inf.makerspace.battleship.game.PlayerInfo;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

import purejavacomm.CommPortIdentifier;

public class Application extends JFrame {

	public static final boolean DEBUG_ENABLED = true;

	private static String[] getSerialComPorts() {
		ArrayList<String> serialComPorts = new ArrayList<>();
		@SuppressWarnings("rawtypes")
		Enumeration portIds = CommPortIdentifier.getPortIdentifiers();
		while (portIds.hasMoreElements()) {
			CommPortIdentifier portId = (CommPortIdentifier) portIds
					.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				serialComPorts.add(portId.getName());
			}
		}
		return serialComPorts.toArray(new String[serialComPorts.size()]);
	}

	private static List<Arduino> arduinos = new ArrayList<Arduino>();

	private static void addArduino(String comPort) {
		try {
			arduinos.add(new Arduino(comPort));
		} catch (RuntimeException e) {
			// Do nothing if not an Arduino sketch loaded with firmata.
		}
	}
	
	private static JPanel gamePanel;

	public static void main(String[] args) {
		// Try to connect to an Arduino on each available serial port.
		// TODO: Probably this should be done in a separate thread.
		String[] comPorts = Application.getSerialComPorts();
		if (comPorts.length != 0) {
			for (int i = 0; i < comPorts.length; i++) {
				Application.addArduino(comPorts[i]);
			}
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException | InstantiationException
						| IllegalAccessException
						| UnsupportedLookAndFeelException e) {
				}
				JFrame frame = new Application();
				// TODO: put UI here
				// TODO: set listeners
				frame.setTitle("Battleship");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setJMenuBar(createMenuBar());
				frame.setLocationRelativeTo(null);
				frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
				gamePanel = new JPanel(new GridBagLayout());
				frame.add(gamePanel);
			}
		});
	}

	// creates Game menu and submenus
	public static JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Game");
		menuBar.add(menu);

		JMenuItem itemNewGame = new JMenuItem("New Game");
		itemNewGame.addActionListener(new GameListener());
		menu.add(itemNewGame);

		JMenuItem itemStats = new JMenuItem("Statistics");
		itemStats.addActionListener(new StatsListener());
		menu.add(itemStats);

		return menuBar;
	}
	
	static class GameListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			PlayerInfo playerInfo = getPlayerDialog();
			gamePanel.removeAll();
			gamePanel.add(new GamePanel(
					arrangeGridArduino, attackGridArduino, playerInfo
			));
			gamePanel.revalidate();
		}
		
	}
	
	static PlayerInfo getPlayerDialog() {
		JTextField user = new JTextField();
		JTextField email = new JTextField();
		final JComponent[] inputs = new JComponent[] {
				new JLabel("Username:"),
				user,
				new JLabel("Email:"),
				email
		};
		JOptionPane.showMessageDialog(null, inputs, "Player Info",
				JOptionPane.PLAIN_MESSAGE);
		return new PlayerInfo(user.getText(), email.getText());
	}
	
	private static JFrame statistics = new JFrame("Statistics");
	private static JPanel statsPanel = new StatsPanel();
	
	static class StatsListener implements ActionListener {
		
		private void setup() {	
			statistics.setLocationRelativeTo(null);
			statistics.setExtendedState(JFrame.MAXIMIZED_BOTH);					
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (true /*TODO: check if first time*/) {
				setup();
			}
			statistics.getContentPane().add(statsPanel);			
			statistics.pack();
			statistics.setVisible(true);
		}
		
	}

	private static final long serialVersionUID = 7899128798333691634L;

	private ArduinoDialog arduinoDialog;

	private static ArrangeGridArduino arrangeGridArduino =
			new ArrangeGridArduino();
	private static AttackGridArduino attackGridArduino =
			new AttackGridArduino();

	public Application() {
		arduinoDialog = new ArduinoDialog(this, true);
		arduinoDialog.setVisible(true);
	}

	class ArduinoDialog extends JDialog {

		private static final long serialVersionUID = 3770351976909118858L;

		private final JLabel labelArduinoArrangeGrid = new JLabel(
				"Arrange Grid");
		private final JLabel labelArduinoAttackGrid = new JLabel("Attack Grid");

		private final JComboBox<String> listArduinoArrangeGrid =
				new JComboBox<String>();
		private final JComboBox<String> listArduinoAttackGrid =
				new JComboBox<String>();

		private final JButton buttonStart = new JButton("Start");
		private final JButton buttonCancel = new JButton("Cancel");

		private final JLabel labelStatus = new JLabel(" ");

		public ArduinoDialog(final JFrame parent, boolean modal) {
			super(parent, modal);

			for (Arduino arduino : arduinos) {
				listArduinoArrangeGrid.addItem(arduino.toString());
				listArduinoAttackGrid.addItem(arduino.toString());
			}

			setTitle("Arduino setup");

			JPanel panelLabels = new JPanel(new GridLayout(2, 1));
			panelLabels.add(labelArduinoArrangeGrid);
			panelLabels.add(labelArduinoAttackGrid);

			JPanel panelLists = new JPanel(new GridLayout(2, 1));
			panelLists.add(listArduinoArrangeGrid);
			panelLists.add(listArduinoAttackGrid);

			JPanel panelSelection = new JPanel();
			panelSelection.add(panelLabels);
			panelSelection.add(panelLists);

			JPanel panelOptions = new JPanel();
			panelOptions.add(buttonStart);
			panelOptions.add(buttonCancel);

			JPanel panelDialog = new JPanel(new BorderLayout());
			panelDialog.add(panelOptions, BorderLayout.CENTER);
			panelDialog.add(labelStatus, BorderLayout.NORTH);
			labelStatus.setForeground(Color.RED);
			labelStatus.setHorizontalAlignment(SwingConstants.CENTER);

			setLayout(new BorderLayout());
			add(panelSelection, BorderLayout.CENTER);
			add(panelDialog, BorderLayout.SOUTH);
			pack();
			setLocationRelativeTo(null);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);

			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});

			buttonStart.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO: Check arduino selection.
					if (isArduinoSelectionValid()) {
						arrangeGridArduino = getArrangeGridArduino();
						attackGridArduino = getAttackGridArduino();
						parent.setVisible(true);
						setVisible(false);
					} else {
						labelStatus.setText("Found invalid Arduinos");
					}
				}
			});

			buttonCancel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
					parent.dispose();
					System.exit(0);
				}
			});
		}

		private boolean isArduinoSelectionValid() {
			if (DEBUG_ENABLED) {
				return true;
			}
			return ((arduinos.size() >= 2) && !listArduinoArrangeGrid
					.getSelectedItem().equals(
							listArduinoAttackGrid.getSelectedItem()));
		}

		private ArrangeGridArduino getArrangeGridArduino() {
			final String selectedItem = String.valueOf(listArduinoArrangeGrid
					.getSelectedItem());
			for (Arduino arduino : arduinos) {
				if (selectedItem.contains(arduino.getSketchName())) {
					return new ArrangeGridArduino(arduino);
				}
			}
			return new ArrangeGridArduino();
		}

		private AttackGridArduino getAttackGridArduino() {
			final String selectedItem = String.valueOf(listArduinoAttackGrid
					.getSelectedItem());
			for (Arduino arduino : arduinos) {
				if (selectedItem.contains(arduino.getSketchName())) {
					return new AttackGridArduino(arduino);
				}
			}
			return new AttackGridArduino();
		}
	}
}
