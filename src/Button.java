// Author: Aidan Fisher

import java.awt.*;
import java.util.Random;

public class Button {

	int x, y, width, height;

	public static int NORMAL = 0, MOUSE_OVER = 1, PRESSED = 2;

	int state;

	Color c;

	String s;

	public Button(String s, Color c, int x, int y, int width, int height) {
		this.s = s;
		this.c = c;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.state = NORMAL;
	}

	public void action() {
		if (s.equals("Water")) {
			GameState.addCombo(GameState.lastHexagon, CurrentCombo.RIVER, 0);
		} else if (s.equals("Road")) {
			GameState.addCombo(GameState.lastHexagon, CurrentCombo.ROAD, 0);
		} else if (s.equals("Land")) {
			GameState.playerState = GameState.CHOOSING_ROTATION;
			return;
		}
		GameState.playerState = GameState.PLACING_HEXAGON;
		GameState.nextTurn();
		Component.currentHexagon = new Hexagon(0, 0, Component.finishedHexagons.get(new Random().nextInt(Component.finishedHexagons.size())));
	}

	public void update(Point p) {
		if (p.x > x && p.x < x + width && p.y > y && p.y < y + height) {
			if (state == PRESSED && !Listening.mouse1Down) {
				state = NORMAL;
				action();
			}
			if (!Listening.mouse1Down) {
				state = MOUSE_OVER;
			}
			if (Listening.mouse1Down && state == MOUSE_OVER) {
				state = PRESSED;
			}
		} else if (state != PRESSED || !Listening.mouse1Down) {
			state = NORMAL;
		} else if (state == PRESSED && Listening.mouse1Down) {
			state = PRESSED;
		}
	}

	public void render(Graphics2D g) {
		if (state == NORMAL) {
			g.setColor(c);
			g.fillRect(x, y, width, height);
			//g.setColor(new Color(c.getRed() - 50, c.getGreen() - 50, c.getBlue() - 50));
		} else if (state == MOUSE_OVER) {
			g.setColor(new Color(c.getRed() + 20, c.getGreen() + 20, c.getBlue() + 20));
			g.fillRect(x, y, width, height);
			//g.setColor(new Color(c.getRed() - 20, c.getGreen() - 30, c.getBlue() - 30));
		} else if (state == PRESSED) {
			g.setColor(new Color(c.getRed() - 25, c.getGreen() - 25, c.getBlue() - 25));
			g.fillRect(x, y, width, height);
			//g.setColor(new Color(c.getRed() - 60, c.getGreen() - 60, c.getBlue() - 60));
		}

		g.setColor(new Color(0, 0, 0));
		g.setFont(new Font("Trebuchet MS", Font.PLAIN, height - 6));
		g.drawString(s, x + 6, y + height - 5);
		g.setStroke(new BasicStroke(3));
		g.drawRect(x, y, width, height);
	}
}
