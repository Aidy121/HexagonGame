// Author: Aidan Fisher

import java.awt.*;
import java.util.Random;

public class Hexagon implements Cloneable {
	public static double radius = 95;
	public static double size = 200;
	public static double rotDist = 0.01;
	public int x;
	public int y;
	public int renderX;
	public int renderY;
	public int riverType;
	public int roadType;
	public int riverSide;
	public int roadSide;
	public boolean riverHighlighted;
	public boolean roadHighlighted;
	public boolean[] landHighlighted = new boolean[6];
	public boolean blocker;

	public int playerOwned = -1; // - 1 = none
	public int type = -1;
	public int side = -1;

	public Hexagon(int x, int y) {
		this.x = x;
		this.y = y;
		this.riverSide = new Random().nextInt(6);
		this.roadSide = new Random().nextInt(6);
		this.riverType = new Random().nextInt(8);
		this.roadType = new Random().nextInt(8);
		this.renderX = renderX(x);
		this.renderY = renderY(x, y);
		this.blocker = false;
	}

	public Hexagon(int x, int y, int riverType, int roadType, int roadRotation) {
		this.x = x;
		this.y = y;
		this.riverSide = 0;
		this.roadSide = roadRotation;
		this.riverType = riverType;
		this.roadType = roadType;
		this.renderX = renderX(x);
		this.renderY = renderY(x, y);
		this.blocker = false;
	}

	public Hexagon(int x, int y, RiverRoadCombo riverRoadCombo) {
		this.x = x;
		this.y = y;
		this.riverSide = 0;
		this.roadSide = riverRoadCombo.roadRotation;
		this.riverType = riverRoadCombo.riverType;
		this.roadType = riverRoadCombo.roadType;
		this.renderX = renderX(x);
		this.renderY = renderY(x, y);
		this.blocker = false;
	}

	public static int renderX(int x) {
		return (int) (((x * radius * 1.5) - Component.screenX) + size / 2);
	}

	public static int renderY(int x, int y) {
		return (int) ((((y * radius * Math.sqrt(3)) + (x * radius * Math.sqrt(3) / 2)) - Component.screenY) + size / 2);
	}

	public void setBlocker() {
		blocker = true;
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}

	public void render(Graphics2D g) {
		this.renderX = renderX(x);
		this.renderY = renderY(x, y);
		if (blocker) {
			g.drawImage(Component.hexagonBlocker, (int) (renderX - size / 2), (int) (renderY - size / 2), (int) size, (int) size, null);
		} else {
			g.drawImage(Component.hexagonBackGround, (int) (renderX - size / 2), (int) (renderY - size / 2), (int) size, (int) size, null);

			Point location = new Point(renderX, renderY);

			for (int i = 0; i < 6; i++) {
				if (landHighlighted[i]) {
					g.rotate(Math.PI / 3 * i, location.x, location.y);
					g.drawImage(Component.hexagonLandHighlighted, (int) (renderX - size / 2), (int) (renderY - size / 2), (int) size, (int) size, null);
					g.rotate(-Math.PI / 3 * i, location.x, location.y);
				}
			}
			if (riverType != -1) {
				g.rotate(Math.PI / 3 * riverSide, location.x, location.y);
				g.drawImage(riverHighlighted ? Component.hexagonRiverHighlighted[riverType] : Component.hexagonRiver[riverType], (int) (renderX - size / 2), (int) (renderY - size / 2), (int) size,
						(int) size, null);
				g.rotate(-Math.PI / 3 * riverSide, location.x, location.y);
			}
			if (roadType != -1) {
				g.rotate(Math.PI / 3 * roadSide, location.x, location.y);
				g.drawImage(roadHighlighted ? Component.hexagonRoadHighlighted[roadType] : Component.hexagonRoad[roadType], (int) (renderX - size / 2), (int) (renderY - size / 2), (int) size,
						(int) size, null);
				g.rotate(-Math.PI / 3 * roadSide, location.x, location.y);
			}

			if (playerOwned != -1) {
				if (type == CurrentCombo.LAND) {
					g.rotate(Math.PI / 3 * (side + 0), location.x, location.y);
					g.setColor(GameState.playerColors[playerOwned]);
					g.fillOval((int) (renderX + 0.05 * Hexagon.size), (int) (renderY - 0.4 * Hexagon.size), (int) (0.2 * Hexagon.size), (int) (0.2 * Hexagon.size));
					g.setColor(new Color(0, 0, 0));
					g.drawOval((int) (renderX + 0.05 * Hexagon.size), (int) (renderY - 0.4 * Hexagon.size), (int) (0.2 * Hexagon.size), (int) (0.2 * Hexagon.size));
					g.rotate(-Math.PI / 3 * (side + 0), location.x, location.y);
				} else {
					g.setColor(GameState.playerColors[playerOwned]);
					g.fillOval((int) (renderX - 0.1 * Hexagon.size), (int) (renderY - 0.1 * Hexagon.size), (int) (0.2 * Hexagon.size), (int) (0.2 * Hexagon.size));
					g.setColor(new Color(0, 0, 0));
					g.drawOval((int) (renderX - 0.1 * Hexagon.size), (int) (renderY - 0.1 * Hexagon.size), (int) (0.2 * Hexagon.size), (int) (0.2 * Hexagon.size));
					g.setFont(new Font("Trebuchet MS", Font.PLAIN, (int) (0.2 * Hexagon.size)));
					if (type == CurrentCombo.RIVER) {
						g.drawString("W", (int) (renderX - 0.08 * Hexagon.size), (int) (renderY + 0.09 * Hexagon.size));
					} else {
						g.drawString("R", (int) (renderX - 0.05 * Hexagon.size), (int) (renderY + 0.08 * Hexagon.size));
					}
				}
			}

		}
		g.drawImage(Component.hexagonBorder, (int) (renderX - size / 2), (int) (renderY - size / 2), (int) size, (int) size, null);

	}

	public void renderAlt(Point pos, Graphics2D g) {
		this.renderX = pos.x;
		this.renderY = pos.y;
		g.drawImage(Component.hexagonBackGround, (int) (renderX - size / 2), (int) (renderY - size / 2), (int) size, (int) size, null);

		Point location = new Point(renderX, renderY);
		if (riverType != -1) {
			g.rotate(Math.PI / 3 * riverSide, location.x, location.y);
			g.drawImage(riverHighlighted ? Component.hexagonRiverHighlighted[riverType] : Component.hexagonRiver[riverType], (int) (renderX - size / 2), (int) (renderY - size / 2), (int) size,
					(int) size, null);
			g.rotate(-Math.PI / 3 * riverSide, location.x, location.y);
		}
		if (roadType != -1) {
			g.rotate(Math.PI / 3 * roadSide, location.x, location.y);
			g.drawImage(roadHighlighted ? Component.hexagonRoadHighlighted[roadType] : Component.hexagonRoad[roadType], (int) (renderX - size / 2), (int) (renderY - size / 2), (int) size, (int) size,
					null);
			g.rotate(-Math.PI / 3 * roadSide, location.x, location.y);
		}
		g.drawImage(Component.hexagonBorder, (int) (renderX - size / 2), (int) (renderY - size / 2), (int) size, (int) size, null);

	}
}
