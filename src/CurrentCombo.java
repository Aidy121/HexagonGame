// Author: Aidan Fisher

import java.awt.Point;
import java.util.ArrayList;

public class CurrentCombo {
	public static Point[] sides = { new Point(0, 1), new Point(-1, 1), new Point(-1, 0), new Point(0, -1), new Point(1, -1), new Point(1, 0) };
	public static int RIVER = 0;
	public static int ROAD = 1;
	public static int LAND = 2;
	int type;
	Hexagon hexagon; // Orginal hexagon
	int corner; // Original side
	int size;
	ArrayList<Hexagon> hexagonsInCombo = new ArrayList<Hexagon>();
	ArrayList<boolean[]> cornersInHexagon = new ArrayList<boolean[]>(); // Only applicable for land

	public CurrentCombo(Hexagon h, int type, int corner) {
		this.type = type;
		this.hexagon = h; //
		this.corner = corner;
		// Find all hexagons in combo:
		hexagonsInCombo.add(h);
		if (type == LAND) {
			cornersInHexagon.add(findCorners(h, corner));
		} else {
			cornersInHexagon.add(new boolean[6]);
		}
		findNewHexagons(h, cornersInHexagon.get(cornersInHexagon.size() - 1));
		if (type == RIVER) {
			for (int i = 0; i < hexagonsInCombo.size(); i++) {
				hexagonsInCombo.get(i).riverHighlighted = true;
			}
		} else if (type == ROAD) {

			for (int i = 0; i < hexagonsInCombo.size(); i++) {
				hexagonsInCombo.get(i).roadHighlighted = true;
			}
		} else if (type == LAND) {
			for (int i = 0; i < hexagonsInCombo.size(); i++) {
				for (int j = 0; j < hexagonsInCombo.get(i).landHighlighted.length; j++) {
					if (cornersInHexagon.get(i)[j]) {
						hexagonsInCombo.get(i).landHighlighted[j] = cornersInHexagon.get(i)[j];
					}
				}
			}
		}
		size = hexagonsInCombo.size();
	}

	public boolean isRiver(Hexagon h, int side) {
		return Component.typeTake[h.riverType][(6 + side - h.riverSide) % 6];
	}

	public boolean[] findCorners(Hexagon h, int c) {
		boolean[] corners = new boolean[6];
		int n = (c + 1) % 6;
		for (int i = 0; i < 6; i++) {
			if (isRiver(h, n)) {
				corners[(n + 5) % 6] = true;
				break;
			} else {
				corners[(n + 5) % 6] = true;
			}
			n = (n + 1) % 6;
		}
		n = c;
		for (int i = 0; i < 6; i++) {
			if (isRiver(h, n)) {
				corners[n] = true;
				break;
			} else {
				corners[n] = true;
			}
			n = (n + 5) % 6;
		}
		return corners;
	}

	public boolean[] trueSides(boolean[] cornersInHexagon, boolean[] corners) {
		for (int r = 0; r < 6; r++) {
			if (corners[r] && !cornersInHexagon[r]) {
				cornersInHexagon[r] = true;
			}
		}
		return cornersInHexagon;
	}

	public void findNewHexagons(Hexagon h, boolean[] oldCorners) {
		boolean[] sidesConnect = riversAndRoadsLineUp(h);
		for (int i = 0; i < 6; i++) {
			if (sidesConnect[i]) {
				Hexagon hex = findHexagon(h.x + sides[i].x, h.y + sides[i].y);
				if (!hexagonsInCombo.contains(hex)) {
					hexagonsInCombo.add(hex);
					if (type == LAND) {
						cornersInHexagon.add(new boolean[6]);
						boolean good = false;
						int index = cornersInHexagon.size() - 1;
						if (oldCorners[(i + 2) % 6]) {
							good = true;
							cornersInHexagon.set(index, trueSides(cornersInHexagon.get(index), findCorners(hex, (i + 0) % 6)));
						}
						if (oldCorners[(i + 3) % 6]) {
							good = true;
							cornersInHexagon.set(index, trueSides(cornersInHexagon.get(index), findCorners(hex, (i + 5) % 6)));
						}
						if (!good) {
							hexagonsInCombo.remove(hex);
							cornersInHexagon.remove(index);
							continue;
						}
					} else {
						cornersInHexagon.add(new boolean[6]);
					}
					findNewHexagons(hex, cornersInHexagon.get(cornersInHexagon.size() - 1));
				} else if (type == LAND) {
					int index = hexagonsInCombo.indexOf(hex);
					// Still has to try..
					boolean good = false;

					if (oldCorners[(i + 2) % 6]) {
						// Check if this "triangle" is already set...
						if (!cornersInHexagon.get(index)[(i + 0) % 6]) {
							// Continue check:
							good = true;
							cornersInHexagon.set(index, trueSides(cornersInHexagon.get(index), findCorners(hex, (i + 0) % 6)));
						}
					}
					if (oldCorners[(i + 3) % 6]) {
						// Check if this "triangle" already set...
						if (!cornersInHexagon.get(index)[(i + 5) % 6]) {
							// Continue check:
							good = true;
							cornersInHexagon.set(index, trueSides(cornersInHexagon.get(index), findCorners(hex, (i + 5) % 6)));
						}
					}

					if (good) {
						findNewHexagons(hex, cornersInHexagon.get(index));
					}
				}
			}
		}
	}

	public Hexagon findHexagon(int x, int y) {
		for (Hexagon h : Component.hexagons) {
			if (h.x == x && h.y == y) {
				return h;
			}
		}
		return null;
	}

	public boolean doesCompare(Hexagon h1, Hexagon h2, int num) {
		//System.out.println("hmm");
		if (h1.blocker || h2.blocker) {
			return false;
		} else if (Component.typeTake[h1.riverType][(6 + num - h1.riverSide) % 6] && Component.typeTake[h2.riverType][(9 + num - h2.riverSide) % 6] && type == RIVER) {
			return true;
		} else if (Component.typeTake[h1.roadType][(6 + num - h1.roadSide) % 6] && Component.typeTake[h2.roadType][(9 + num - h2.roadSide) % 6] && type == ROAD) {
			return true;
		} else if (type == LAND) {
			return true;
		}
		return false;
	}

	public boolean[] riversAndRoadsLineUp(Hexagon h) {
		boolean[] sidesConnect = new boolean[6];
		for (Hexagon hexagon : Component.hexagons) {
			if (hexagon.x == h.x - 1 && hexagon.y == h.y) {
				sidesConnect[2] = doesCompare(hexagon, h, 2);
			} else if (hexagon.x == h.x + 1 && hexagon.y == h.y) {
				sidesConnect[5] = doesCompare(hexagon, h, 5);
			} else if (hexagon.x == h.x && hexagon.y == h.y - 1) {
				sidesConnect[3] = doesCompare(hexagon, h, 3);
			} else if (hexagon.x == h.x && hexagon.y == h.y + 1) {
				sidesConnect[0] = doesCompare(hexagon, h, 0);
			} else if (hexagon.x == h.x - 1 && hexagon.y == h.y + 1) {
				sidesConnect[1] = doesCompare(hexagon, h, 1);
			} else if (hexagon.x == h.x + 1 && hexagon.y == h.y - 1) {
				sidesConnect[4] = doesCompare(hexagon, h, 4);
			}
		}
		return sidesConnect;
	}
}
