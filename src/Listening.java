// Author: Aidan Fisher

import java.awt.event.*;
import java.awt.*;
import java.util.*;

public class Listening implements MouseListener, MouseWheelListener, KeyListener {
	public static boolean movingScreen = false;
	public static boolean mouse1Down = false;
	public static Point pressLocation = new Point(0, 0);
	public static Point gridLocation = new Point(0, 0);
	public static Point currLocation = new Point(0, 0);
	public static Point lastLocation = new Point(0, 0);
	public static int zoomRate = 3;

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			Component.screenX += (currLocation.x) / (1.0 + zoomRate);
			Component.screenY += (currLocation.y) / (1.0 + zoomRate);
			Component.screenX *= (1.0 + 1.0 / zoomRate);
			Component.screenY *= (1.0 + 1.0 / zoomRate);
			Hexagon.size = Hexagon.size * (1.0 + 1.0 / zoomRate);
			Hexagon.radius = Hexagon.size * 0.475;
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			Component.screenX -= (currLocation.x) * (1.0 / zoomRate);
			Component.screenY -= (currLocation.y) * (1.0 / zoomRate);
			Component.screenX /= (1.0 + 1.0 / zoomRate);
			Component.screenY /= (1.0 + 1.0 / zoomRate);
			Hexagon.size = Hexagon.size / (1.0 + 1.0 / zoomRate);
			Hexagon.radius = Hexagon.size * 0.475;
		}

		if (GameState.playerState == GameState.CHOOSING_RRL) {
			if (e.getKeyCode() == KeyEvent.VK_0) {
				GameState.playerState = GameState.PLACING_HEXAGON;
				GameState.nextTurn();
				Component.currentHexagon = new Hexagon(0, 0, Component.finishedHexagons.get(new Random().nextInt(Component.finishedHexagons.size())));
			} else if (e.getKeyCode() == KeyEvent.VK_1 && GameState.lastHexagon != null) {
				GameState.addCombo(GameState.lastHexagon, CurrentCombo.RIVER, 0);
				GameState.playerState = GameState.PLACING_HEXAGON;
				GameState.nextTurn();
				Component.currentHexagon = new Hexagon(0, 0, Component.finishedHexagons.get(new Random().nextInt(Component.finishedHexagons.size())));
			} else if (e.getKeyCode() == KeyEvent.VK_2 && GameState.lastHexagon != null) {
				GameState.addCombo(GameState.lastHexagon, CurrentCombo.ROAD, 0);
				GameState.playerState = GameState.PLACING_HEXAGON;
				GameState.nextTurn();
				Component.currentHexagon = new Hexagon(0, 0, Component.finishedHexagons.get(new Random().nextInt(Component.finishedHexagons.size())));
			} else if (e.getKeyCode() == KeyEvent.VK_3 && GameState.lastHexagon != null) {
				GameState.playerState = GameState.CHOOSING_ROTATION;
			}
		}
	}

	public void keyReleased(KeyEvent e) {
		if (GameState.playerState == GameState.PLACING_HEXAGON && Component.placedHexagon != null) {
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				if (Component.placedHexagon.blocker) {
					GameState.playerState = GameState.PLACING_HEXAGON;
					GameState.nextTurn();
					Component.currentHexagon = new Hexagon(0, 0, Component.finishedHexagons.get(new Random().nextInt(Component.finishedHexagons.size())));
				} else {
					if (GameState.numPieces[GameState.playerTurn] != 0) {
						GameState.playerState = GameState.CHOOSING_RRL;
						GameState.lastHexagon = Component.placedHexagon;
						Component.currentHexagon = null;
					} else {
						GameState.playerState = GameState.PLACING_HEXAGON;
						GameState.nextTurn();
						Component.currentHexagon = new Hexagon(0, 0, Component.finishedHexagons.get(new Random().nextInt(Component.finishedHexagons.size())));
					}
					GameState.newPieceCalculations();
				}
				Component.placedHexagon = null;
			}
		}
	}

	public void keyTyped(KeyEvent e) { //doesn't work, use pressed.

	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getWheelRotation() < 0) {
			if (Component.currentHexagon != null) {
				Component.currentHexagon.riverSide = (Component.currentHexagon.riverSide + 5) % 6;
				Component.currentHexagon.roadSide = (Component.currentHexagon.roadSide + 5) % 6;
			}
		} else if (e.getWheelRotation() > 0) {
			if (Component.currentHexagon != null) {
				Component.currentHexagon.riverSide = (Component.currentHexagon.riverSide + 1) % 6;
				Component.currentHexagon.roadSide = (Component.currentHexagon.roadSide + 1) % 6;
			}
		}
	}

	public void mouseClicked(MouseEvent e) {

	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}

	public double distance(Point a, Point b) {
		return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
	}

	public Point gridify(Point loc) {

		int x = (int) Math.floor((loc.x + Component.screenX) / (Hexagon.radius * 1.5));
		int y = (int) Math.floor((loc.y + Component.screenY) / (Hexagon.radius * Math.sqrt(3)) - (0.5 * x));

		double distance1 = distance(new Point(Hexagon.renderX(x), Hexagon.renderY(x, y)), loc);
		double distance2 = distance(new Point(Hexagon.renderX(x - 1), Hexagon.renderY(x - 1, y)), loc);
		double distance3 = distance(new Point(Hexagon.renderX(x - 1), Hexagon.renderY(x - 1, y + 1)), loc);

		if (distance1 < distance2 && distance1 < distance3) {
			return new Point(x, y);
		} else if (distance2 < distance1 && distance2 < distance3) {
			return new Point(x - 1, y);
		} else {
			return new Point(x - 1, y + 1);
		}
	}

	public boolean gridExists(Point loc) {
		for (int i = 0; i < Component.hexagons.size(); i++) {
			if (Component.hexagons.get(i).x == loc.x && Component.hexagons.get(i).y == loc.y) {
				return true;
			}
		}
		return false;
	}

	public void mousePressed(MouseEvent e) {
		pressLocation = e.getPoint();

		if (e.getButton() == 3) {
			movingScreen = true;
		} else if (e.getButton() == 1) {
			mouse1Down = true;
		}
	}

	public boolean doesNotCompare(Hexagon h1, Hexagon h2, int num) {
		if (h1.blocker || h2.blocker) {
			return false;
		} else if (Component.typeTake[h1.riverType][(6 + num - h1.riverSide) % 6] != Component.typeTake[h2.riverType][(9 + num - h2.riverSide) % 6]) {
			return true;
		} else if (Component.typeTake[h1.roadType][(6 + num - h1.roadSide) % 6] != Component.typeTake[h2.roadType][(9 + num - h2.roadSide) % 6]) {
			return true;
		}
		return false;
	}

	public boolean riversAndRoadsLineUp(Hexagon h) {

		for (Hexagon hexagon : Component.hexagons) {
			if (hexagon.x == h.x - 1 && hexagon.y == h.y) {
				if (doesNotCompare(hexagon, h, 2))
					return false;
			} else if (hexagon.x == h.x + 1 && hexagon.y == h.y) {
				if (doesNotCompare(hexagon, h, 5))
					return false;
			} else if (hexagon.x == h.x && hexagon.y == h.y - 1) {
				if (doesNotCompare(hexagon, h, 3))
					return false;
			} else if (hexagon.x == h.x && hexagon.y == h.y + 1) {
				if (doesNotCompare(hexagon, h, 0))
					return false;
			} else if (hexagon.x == h.x - 1 && hexagon.y == h.y + 1) {
				if (doesNotCompare(hexagon, h, 1))
					return false;
			} else if (hexagon.x == h.x + 1 && hexagon.y == h.y - 1) {
				if (doesNotCompare(hexagon, h, 4))
					return false;
			}
		}
		return true;
	}

	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == 3) {
			movingScreen = false;
		} else if (e.getButton() == 2) {
			if (GameState.playerState == GameState.PLACING_HEXAGON) {
				Point p = gridify(pressLocation);
				if (!gridExists(p)) {
					Component.currentHexagon.x = p.x;
					Component.currentHexagon.y = p.y;
					if (Component.placedHexagon != null) {
						Component.hexagons.remove(Component.placedHexagon);
					}
					Component.placedHexagon = (Hexagon) Component.currentHexagon.clone();
					Component.placedHexagon.setBlocker();
					Component.hexagons.add(Component.placedHexagon);
				}
			}
		} else if (e.getButton() == 1) {
			mouse1Down = false;

			if (GameState.playerState == GameState.PLACING_HEXAGON) {
				Point p = gridify(pressLocation);
				if (!gridExists(p)) {
					Component.currentHexagon.x = p.x;
					Component.currentHexagon.y = p.y;

					if (riversAndRoadsLineUp(Component.currentHexagon)) {
						if (Component.placedHexagon != null) {
							Component.hexagons.remove(Component.placedHexagon);
						}

						Component.placedHexagon = (Hexagon) Component.currentHexagon.clone();
						Component.hexagons.add(Component.placedHexagon);
					}
				}
			} else if (GameState.playerState == GameState.CHOOSING_ROTATION && GameState.lastHexagon != null) {
				double rotation = Math.atan2(pressLocation.y - GameState.lastHexagon.renderY, pressLocation.x - GameState.lastHexagon.renderX);
				int side = (int) ((rotation / (Math.PI / 3) + 7.5)) % 6;
				GameState.addCombo(GameState.lastHexagon, CurrentCombo.LAND, side);
				GameState.playerState = GameState.PLACING_HEXAGON;
				GameState.nextTurn();
				Component.currentHexagon = new Hexagon(0, 0, Component.finishedHexagons.get(new Random().nextInt(Component.finishedHexagons.size())));
			}

		}
	}
}
