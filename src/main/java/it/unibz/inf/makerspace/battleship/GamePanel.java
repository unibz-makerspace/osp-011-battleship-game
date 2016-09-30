package it.unibz.inf.makerspace.battleship;

import it.unibz.inf.makerspace.battleship.firmata.ArrangeGridArduino;
import it.unibz.inf.makerspace.battleship.firmata.AttackGridArduino;
import it.unibz.inf.makerspace.battleship.game.OnArrangeTileSetListener;
import it.unibz.inf.makerspace.battleship.game.OnAttackTileSetListener;
import it.unibz.inf.makerspace.battleship.game.Ship;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class GamePanel extends JPanel
		implements OnArrangeTileSetListener, OnAttackTileSetListener, 
		MouseListener {
	
	public enum TileState {
		WATER(Color.CYAN),
		PLACEMENT(Color.GRAY), // Special case, not visible to attack grid.
		SHIP(Color.BLACK),     // Special case, not visible to attack grid.
		HIT(Color.YELLOW),
		DESTROYED(Color.RED);
		
		public final Color color;
		
		private TileState(Color color) {
			this.color = color;
		}
	};
	
	public enum ShipSound {
		PLACED("/Bleep.wav"),
		MISPLACED("/Error.wav"),
		MISSED("/WaveCrash.wav"),
		HIT("/ExplosionAlarm.wav"),
		DESTROYED("/ExplosionDestroyed.wav");
		
		public final String resourceName;
		
		private ShipSound(String resourceName) {
			this.resourceName = resourceName;
		}
	};
	
	public static final int MAX_ROWS    = 8;
	public static final int MAX_COLUMNS = 8;

	private static final long serialVersionUID = -3924073445963716304L;
	
	private JTextField[][] tiles;
	private JTextField status;
	
	private List<Ship> ships;
	private Ship.Builder shipBuilder = null;
	
	private String[] shipBuilderStatus;
	
	public GamePanel(
			ArrangeGridArduino arrangeGridArduino,
			AttackGridArduino attackGridArduino) {
		shipBuilderStatus = new String[Ship.MAX_BATTLESHIPS +
		                               Ship.MAX_DESTROYERS + 1];
		for (int i = 0; i < shipBuilderStatus.length; i++) {
			if (i < Ship.MAX_BATTLESHIPS) {
				shipBuilderStatus[i] =
						"B" + Ship.Type.Battleship.length +
						(char)('a' + i);
			} else if (i < (Ship.MAX_BATTLESHIPS + Ship.MAX_DESTROYERS)) {
				shipBuilderStatus[i] =
						"D" +  + Ship.Type.Destroyer.length +
						(char)('a' + i - Ship.MAX_BATTLESHIPS);
			} else {
				shipBuilderStatus[i] = "H";
			}
		}
		ships = new ArrayList<Ship>();
		tiles = new JTextField[MAX_ROWS][MAX_COLUMNS];
		setLayout(new GridLayout(MAX_ROWS + 1, MAX_COLUMNS + 1));
		for (int row = 0; row < MAX_ROWS + 1; row++) {
			for (int column = 0; column < MAX_COLUMNS + 1; column++) {
				if ((row < MAX_ROWS) && (column < MAX_COLUMNS)) {
					JTextField tile = new JTextField(
							(Application.DEBUG_ENABLED) ?
									"(" + row + "," + column + ")" :
									""
					);
					tile.setEditable(false);
					tile.setHorizontalAlignment(JTextField.CENTER);
					add(tile);
					tiles[row][column] = tile;
					tile.setBackground(TileState.WATER.color);
				} else if (column == MAX_COLUMNS) {
					if (row != MAX_ROWS) {
						JTextField rowLabel = new JTextField(getRowLabel(row));
						rowLabel.setEditable(false);
						rowLabel.setHorizontalAlignment(JTextField.CENTER);
						add(rowLabel);
					}
				} else {
					JTextField columnLabel =
							new JTextField(getColumnLabel(column));
					columnLabel.setEditable(false);
					columnLabel.setHorizontalAlignment(JTextField.CENTER);
					add(columnLabel);
				}
				if ((row == MAX_ROWS) && (column == MAX_COLUMNS)) {
					status = new JTextField();
					status.setEditable(false);
					status.setHorizontalAlignment(JTextField.CENTER);
					status.addMouseListener(this);
					add(status);
				}  
			}
		}
		arrangeGridArduino.setOnArrangeTileSetListener(this);
		status.setText(shipBuilderStatus[ships.size()]);
		Ship.resetShipCount();
	}
	
	private String getRowLabel(int row) {
		return String.valueOf((char)('A' + row));
	}
	
	private String getColumnLabel(int column) {
		return String.valueOf(column + 1);
	}
	
	private void playShipSound(ShipSound shipSound) {
		URL url = getClass().getResource(shipSound.resourceName);
		AudioClip ac = Applet.newAudioClip(url);
		ac.play();
	}
	
	@Override
	public Dimension getPreferredSize() {
		// Make panel square.
		Dimension d = super.getPreferredSize();
		Container c = getParent();
		if (c != null) {
			d = c.getSize();
		} else {
			return new Dimension(10, 10);
		}
		int w = (int) d.getWidth();
		int h = (int) d.getHeight();
		int s = (w < h ? w : h);
		return new Dimension(s, s);
	}
	
	private boolean areAllShipsPlaced = false;
	private boolean isShipPlacementEnabled = true;
	
	@Override
	public void onArrangeTileSet(int row, int column) {
		if (!isShipPlacementEnabled) {
			return;
		}
		if (Application.DEBUG_ENABLED) {
			System.out.println("shipBuilder=(" + row + "," + column + ")");
		}
		if (ships.size() < Ship.MAX_BATTLESHIPS) {
			if (shipBuilder == null) {
				shipBuilder = new Ship.Builder(Ship.Type.Battleship);
			}
			try {
				shipBuilder.addPoint(new Point(row, column));
			} catch(UnsupportedOperationException e) {
				// Fail, redo it.
				isShipPlacementEnabled = false;
				playShipSound(ShipSound.MISPLACED);
				shipBuilder = new Ship.Builder(Ship.Type.Battleship);
				System.out.println(e.getMessage());
			}
		} else if (ships.size() < Ship.MAX_BATTLESHIPS + Ship.MAX_DESTROYERS) {
			if (shipBuilder == null) {
				shipBuilder = new Ship.Builder(Ship.Type.Destroyer);
			}
			try {
				shipBuilder.addPoint(new Point(row, column));
			} catch(UnsupportedOperationException e) {
				// Fail, redo it.
				isShipPlacementEnabled = false;
				playShipSound(ShipSound.MISPLACED);
				shipBuilder = new Ship.Builder(Ship.Type.Destroyer);
				System.out.println(e.getMessage());
			}
		}
		System.out.println("shipbuilder.remainingPoints=" + shipBuilder.getRemainingPointsToSet());
		if (shipBuilder.getRemainingPointsToSet() == 0) {
			try {
				Ship ship = shipBuilder.create();
				ships.add(ship);
				shipBuilder = null;
				// Play sound.
				playShipSound(ShipSound.PLACED);
			} catch(UnsupportedOperationException e) {
				// Fail silently
				System.out.println(e.getMessage());
			}
		}
		status.setText(shipBuilderStatus[ships.size()]);
		// TODO: Set to last state. Not needed?
		if (status.getText().equals("H")) {
			if (Application.DEBUG_ENABLED) {
				System.out.println("Ship placement finished");
			}
			areAllShipsPlaced = true;
			isShipPlacementEnabled = false;
			// TODO: enable attack grid listener
		}
		drawShipTiles();
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (!isShipPlacementEnabled && !areAllShipsPlaced) {
			isShipPlacementEnabled = true;
			if (Application.DEBUG_ENABLED) {
				System.out.println("Ship placement re-enabled");
			}
		}
	}
	@Override
	public void mousePressed(MouseEvent e) { }
	@Override
	public void mouseReleased(MouseEvent e) { }
	@Override
	public void mouseEntered(MouseEvent e) { }
	@Override
	public void mouseExited(MouseEvent e) { }
	
	private void drawShipTiles() {
		// Draw intermediate placement points.
		if (shipBuilder != null) {
			drawShipPoints(shipBuilder.getPoints(), TileState.PLACEMENT);
		}
		for (Ship ship : ships) {
			// Draw ship points.
			drawShipPoints(ship.getShipPoints(), TileState.SHIP);
			// TODO: Draw hit points.
			// TODO: Draw destroyed points.
		}
	}
	
	private void drawShipPoints(Point[] shipPoints, TileState tileState) {
		for (Point shipPoint : shipPoints) {
			int row = shipPoint.x;
			int column = shipPoint.y;
			tiles[row][column].setBackground(tileState.color);
		}
	}

	@Override
	public void onAttackTileSet(int row, int column) {
		// TODO Auto-generated method stub
	}
}
