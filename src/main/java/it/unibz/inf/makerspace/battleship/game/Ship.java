package it.unibz.inf.makerspace.battleship.game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Ship {
	
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
	
	private Ship(Type type, List<Point> points)
			throws UnsupportedOperationException {
		this.name = type.toString();
		this.points = points.toArray(new Point[points.size()]);
		hitPoints = new ArrayList<Point>(points.size());
		if (type == Type.Battleship) {
			battleships++;
		} else if (type == Type.Destroyer) {
			destroyers++;
		}
		if (battleships == MAX_BATTLESHIPS) {
			throw new UnsupportedOperationException(
					"Invalid operation can not create more than " +
					MAX_BATTLESHIPS + " of type " + Type.Battleship.toString()
			);
		}
		if (destroyers == MAX_DESTROYERS) {
			throw new UnsupportedOperationException(
					"Invalid operation can not create more than " +
					MAX_DESTROYERS + " of type " + Type.Destroyer.toString()
			);
		}
	}
	
	public String getName() {
		return name;
	}
	
	public final Point[] getPoints() {
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
	
	private boolean belongsPointToShip(Point p) {
		boolean result = false;
		for (Point point : points) {
			if (point.equals(p)) {
				result = true;
			}
		}
		return result;
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
		
		public Builder(Type type) {
			nestedType = type;
			nestedLength = type.length;
			nestedPoints = new ArrayList<Point>(nestedLength);
		}
		
		public Builder addPoint(Point p) throws UnsupportedOperationException {
			if (nestedPoints.size() == 0) {
				nestedPoints.add(p);
			} else if ((nestedPoints.size() < nestedLength) &&
					isPointInNeighborhood(p)) {
				nestedPoints.add(p);
			} else {
				throw new UnsupportedOperationException(
						"Invalid operation for point p=" + p +
						" that is not neighbor of the remaining ship."
				);
			}
			return this;
		}
		
		private boolean isPointInNeighborhood(Point p) {
			for (Point point : nestedPoints) {
				final Point[] neighbors = new Point[] {
						new Point(point.x-1, point.y-1),
						new Point(point.x+0, point.y-1),
						new Point(point.x+1, point.y-1),
						new Point(point.x+1, point.y+0),
						new Point(point.x+1, point.y+1),
						new Point(point.x+0, point.y+1),
						new Point(point.x-1, point.y+1),
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
		
		public int getRemainingPointsToSet() {
			return nestedLength - nestedPoints.size();
		}
		
		public Ship create() throws UnsupportedOperationException {
			return new Ship(nestedType, nestedPoints);
		}
	}
}
