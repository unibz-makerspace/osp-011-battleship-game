package it.unibz.inf.makerspace.battleship.game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Ship {
	
	public enum Orientation {
		HORIZONTAL,
		VERTICAL
	};
	
	public enum Type {
		Battleship(4), Destroyer(2);
		
		public final int length;
		
		private Type(int length) {
			this.length = length;
		}
	};
	
	public static final int MAX_BATTLESHIPS = 2;
	public static final int MAX_DESTROYERS  = 4;
	
	private static int battleships = 0;
	private static int destroyers = 0;
	
	private final String name;
	private final Point[] points;
	private final List<Point> hitPoints;
	private final Orientation orientation;
	
	private Ship(Type type, List<Point> points, Orientation orientation)
			throws UnsupportedOperationException {
		this.name = type.toString();
		this.points = points.toArray(new Point[points.size()]);
		hitPoints = new ArrayList<Point>(points.size());
		this.orientation = orientation;
		if (type == Type.Battleship) {
			battleships++;
		} else if (type == Type.Destroyer) {
			destroyers++;
		}
		System.out.println(Type.Battleship.toString() + "=" + battleships);
		System.out.println(Type.Destroyer.toString() + "=" + destroyers);
		if (battleships > MAX_BATTLESHIPS) {
			throw new UnsupportedOperationException(
					"Invalid operation can not create more than " +
					MAX_BATTLESHIPS + " ships of type " +
					Type.Battleship.toString()
			);
		}
		if (destroyers > MAX_DESTROYERS) {
			throw new UnsupportedOperationException(
					"Invalid operation can not create more than " +
					MAX_DESTROYERS + " ships of type " +
					Type.Destroyer.toString()
			);
		}
	}
	
	public static void resetShipCount() {
		battleships = 0;
		destroyers = 0;
	}
	
	public String getName() {
		return name;
	}
	
	public final Point[] getShipPoints() {
		return points;
	}
	
	public void hitShip(Point p) throws UnsupportedOperationException {
		if (belongsPointToShip(p)) {
			hitPoints.add(p);
		} else {
			throw new UnsupportedOperationException(
					"Invalid operation for point p=" + p +
					" that is not part of the ship."
			);
		}
	}
	
	public boolean belongsPointToShip(Point p) {
		boolean result = false;
		for (Point point : points) {
			if (point.equals(p)) {
				result = true;
			}
		}
		return result;
	}
	
	public Orientation getOrientation() {
		return orientation;
	}
	
	public int getHitsLeft() {
		return points.length - hitPoints.size();
	}
	
	@Override
	public String toString() {
		String result = getName();
		for (Point p : points) {
			result += " " + p;
		}
		return result;
	}
	
	public static class Builder {
		
		private final Type nestedType;
		private final int nestedLength;
		private final List<Point> nestedPoints;
		private Orientation nestedOrientation;
		
		public Builder(Type type) {
			nestedType = type;
			nestedLength = type.length;
			nestedPoints = new ArrayList<Point>(nestedLength);
		}
		
		public Builder addPoint(Point p) throws UnsupportedOperationException {
			if (nestedPoints.size() == 0) {
				nestedPoints.add(p);
			} else if ((nestedPoints.size() == 1) && isPointInNeighborhood(p)) {
				final Point p1 = nestedPoints.get(0);
				if (p1.x == p.x) {
					// [p1][p]
					nestedOrientation = Orientation.HORIZONTAL;
				} else {
					// [p1]
					// [p]
					nestedOrientation = Orientation.VERTICAL;
				}
				nestedPoints.add(p);
			} else if ((nestedPoints.size() < nestedLength) &&
					isPointInNeighborhood(p, nestedOrientation)) {
				nestedPoints.add(p);
			} else {
				throw new UnsupportedOperationException(
						"Invalid operation for point p=" + p +
						" that is not neighbor of the remaining ship."
				);
			}
			return this;
		}
		
		public Point[] getPoints() {
			return nestedPoints.toArray(new Point[nestedPoints.size()]);
		}
		
		private boolean isPointInNeighborhood(Point p) {
			for (Point point : nestedPoints) {
				final Point[] neighbors = new Point[] {
						new Point(point.x+0, point.y-1),
						new Point(point.x+1, point.y+0),
						new Point(point.x+0, point.y+1),
						new Point(point.x-1, point.y+0),
				};
				for (Point neighbor : neighbors) {
					if (neighbor.equals(p)) {
						return true;
					}
				}
			}
			return false;
		}
		
		private boolean isPointInNeighborhood(
				Point p, Orientation orientation) {
			for (Point point : nestedPoints) {
				final Point[] neighbors =
						(orientation == Orientation.VERTICAL) ?
								new Point[] {
									new Point(point.x+1, point.y+0),					
									new Point(point.x-1, point.y+0),
								} :
								new Point[] {
									new Point(point.x+0, point.y-1),
									new Point(point.x+0, point.y+1),
								};
				for (Point neighbor : neighbors) {
					if (neighbor.equals(p)) {
						return true;
					}
				}
			}
			return false;
		}
		
		public int getRemainingPointsToSet() {
			return nestedLength - nestedPoints.size();
		}
		
		public Ship create() throws UnsupportedOperationException {
			return new Ship(nestedType, nestedPoints, nestedOrientation);
		}
	}
}
