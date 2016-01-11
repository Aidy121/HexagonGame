// Author: Aidan Fisher

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class GameState {
	public final static int PLACING_HEXAGON = 0;
	public final static int CHOOSING_RRL = 1;
	public final static int CHOOSING_ROTATION = 2;

	public static Hexagon lastHexagon = null;

	public static int TURN_TIME = 25 * Component.ticksPerSecond;
	public static int[] turnTimer = { TURN_TIME, 0, 0 };
	public static int numPlayers = 3;
	public static int playerTurn = 0;
	public static int playerState = PLACING_HEXAGON;
	public static Color[] playerColors = { new Color(220, 0, 0), new Color(0, 140, 0), new Color(0, 0, 180) };
	public static int[] numPieces = { 4, 4, 4 };
	public static CurrentCombo[][] playerCombos = new CurrentCombo[3][4];
	public static boolean[] piecesLeft = { true, true, true };
	public static int numTurns = 0;

	public static void addCombo(Hexagon h, int type, int side) { // Not done if blocker
		for (int i = 0; i < playerCombos[playerTurn].length; i++) {
			if (playerCombos[playerTurn][i] == null) {
				h.playerOwned = playerTurn;
				h.type = type;
				h.side = side;
				playerCombos[playerTurn][i] = new CurrentCombo(h, type, side);
				numPieces[playerTurn]--;
				break;
			}
		}
		newPieceCalculations();
	}

	public static void tick() {
		turnTimer[playerTurn]--;
		if (turnTimer[playerTurn] == 0) {
			System.out.println("Infraction");
			turnTimer[playerTurn] -= 5 * Component.ticksPerSecond;
		} else if (turnTimer[playerTurn] == -10 * Component.ticksPerSecond) {
			System.out.println("Infraction x2");
			turnTimer[playerTurn] -= 5 * Component.ticksPerSecond;
		} else if (turnTimer[playerTurn] == -20 * Component.ticksPerSecond) {
			System.out.println("Infraction x3");
			turnTimer[playerTurn] -= 5 * Component.ticksPerSecond;
		}
	}

	public static void newPieceCalculations() {
		// Update existing combos:
		for (int i = 0; i < playerCombos.length; i++) {
			for (int j = 0; j < playerCombos[i].length; j++) {
				if (playerCombos[i][j] != null) {
					playerCombos[i][j] = new CurrentCombo(playerCombos[i][j].hexagon, playerCombos[i][j].type, playerCombos[i][j].corner);
				} else {
					break;
				}
			}
		}
	}

	public static int total(int player) {
		int total = 0;
		for (int i = 0; i < playerCombos[player].length; i++) {
			if (playerCombos[player][i] != null) {
				if (playerCombos[player][i].type == CurrentCombo.LAND) {
					total += playerCombos[player][i].size;
				} else {
					total += playerCombos[player][i].size;
				}
			} else {
				break;
			}
		}
		return total;
	}

	public static void nextTurn() {
		playerTurn++;
		if (playerTurn >= numPlayers) {
			playerTurn = 0;
			numTurns++;
		}
		turnTimer[playerTurn] += TURN_TIME;
	}

	public static void render(Graphics2D g) {
		g.setStroke(new BasicStroke(2));

		g.setColor(GameState.playerColors[GameState.playerTurn]);
		g.fillOval(10, 10, 60, 60);
		g.setColor(new Color(0, 0, 0));
		g.drawOval(10, 10, 60, 60);

		g.setColor(GameState.playerColors[0]);
		g.fillRect(10, 100, 40, 40);
		g.setColor(new Color(0, 0, 0));
		g.drawRect(10, 100, 40, 40);
		g.setColor(GameState.playerColors[1]);
		g.fillRect(10, 150, 40, 40);
		g.setColor(new Color(0, 0, 0));
		g.drawRect(10, 150, 40, 40);
		g.setColor(GameState.playerColors[2]);
		g.fillRect(10, 200, 40, 40);
		g.setColor(new Color(0, 0, 0));
		g.drawRect(10, 200, 40, 40);

		g.setColor(new Color(160, 160, 160));
		g.fillRect(50, 100, 80, 40);
		g.setColor(new Color(0, 0, 0));
		g.drawRect(50, 100, 80, 40);
		g.setColor(new Color(160, 160, 160));
		g.fillRect(50, 150, 80, 40);
		g.setColor(new Color(0, 0, 0));
		g.drawRect(50, 150, 80, 40);
		g.setColor(new Color(160, 160, 160));
		g.fillRect(50, 200, 80, 40);
		g.setColor(new Color(0, 0, 0));
		g.drawRect(50, 200, 80, 40);

		g.setFont(new Font("Verdana", Font.PLAIN, 40));
		g.drawString(String.valueOf(total(0)), 65, 137);
		g.drawString(String.valueOf(total(1)), 65, 187);
		g.drawString(String.valueOf(total(2)), 65, 237);

		g.drawString(String.valueOf(turnTimer[playerTurn] / Component.ticksPerSecond) + "s", 200, 160);

		g.drawString("Turn: " + String.valueOf(numTurns + 1), 90, 70);
	}
}
