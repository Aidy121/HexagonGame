// Author: Aidan Fisher

import javax.imageio.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.applet.*;
import java.io.*;
import java.util.*;

public class Component extends Applet implements Runnable {
	private static final long serialVersionUID = 1L;
	boolean isRunning = false;
	public static int ticksPerSecond = 10;
	public static Point screenPos = new Point(0, 0);
	public static Point mousePos = new Point(0, 0);
	public Image screen;
	public static Component component;

	public static int screenW = 1200;
	public static int screenH = 800;

	public static double screenX = 0;
	public static double screenY = 0;
	public static double tileSize = 1;

	public static ArrayList<Hexagon> hexagons = new ArrayList<Hexagon>();

	public static BufferedImage hexagonBorder;
	public static BufferedImage hexagonBackGround;
	public static BufferedImage hexagonLandHighlighted;
	public static BufferedImage hexagonBlocker;
	public static BufferedImage[] hexagonRiver = new BufferedImage[8];
	public static BufferedImage[] hexagonRoad = new BufferedImage[8];
	public static BufferedImage[] hexagonRiverHighlighted = new BufferedImage[8];
	public static BufferedImage[] hexagonRoadHighlighted = new BufferedImage[8];

	public static boolean[][] typeTake = new boolean[8][6];

	public static ArrayList<RiverRoadCombo> finishedHexagons = new ArrayList<RiverRoadCombo>();

	public static Hexagon currentHexagon = null;
	public static Hexagon placedHexagon = null;

	public ArrayList<Button> buttons = new ArrayList<Button>();

	public Component() {
		setPreferredSize(new Dimension(screenW, screenH));
		addKeyListener(new Listening());
		addMouseListener(new Listening());
		addMouseWheelListener(new Listening());
		try {
			hexagonBorder = ImageIO.read(new File("res/HexagonBorder.png"));
			hexagonBackGround = ImageIO.read(new File("res/HexagonBG.png"));
			hexagonLandHighlighted = ImageIO.read(new File("res/HexagonLandHighlight.png"));
			hexagonBlocker = ImageIO.read(new File("res/HexagonBlocker.png"));

			hexagonRiver[0] = ImageIO.read(new File("res/HexagonRiver0.png"));
			hexagonRiver[1] = ImageIO.read(new File("res/HexagonRiver1.png"));
			hexagonRiver[2] = ImageIO.read(new File("res/HexagonRiver2.png"));
			hexagonRiver[3] = ImageIO.read(new File("res/HexagonRiver3.png"));
			hexagonRiver[4] = ImageIO.read(new File("res/HexagonRiver4.png"));
			hexagonRiver[5] = ImageIO.read(new File("res/HexagonRiver5.png"));
			hexagonRiver[6] = ImageIO.read(new File("res/HexagonRiver6.png"));
			hexagonRiver[7] = ImageIO.read(new File("res/HexagonRiver7.png"));

			hexagonRoad[0] = ImageIO.read(new File("res/HexagonRoad0.png"));
			hexagonRoad[1] = ImageIO.read(new File("res/HexagonRoad1.png"));
			hexagonRoad[2] = ImageIO.read(new File("res/HexagonRoad2.png"));
			hexagonRoad[3] = ImageIO.read(new File("res/HexagonRoad3.png"));
			hexagonRoad[4] = ImageIO.read(new File("res/HexagonRoad4.png"));
			hexagonRoad[5] = ImageIO.read(new File("res/HexagonRoad5.png"));
			hexagonRoad[6] = ImageIO.read(new File("res/HexagonRoad6.png"));
			hexagonRoad[7] = ImageIO.read(new File("res/HexagonRoad7.png"));

			hexagonRiverHighlighted[0] = ImageIO.read(new File("res/HexagonRiver0h.png"));
			hexagonRiverHighlighted[1] = ImageIO.read(new File("res/HexagonRiver1h.png"));
			hexagonRiverHighlighted[2] = ImageIO.read(new File("res/HexagonRiver2h.png"));
			hexagonRiverHighlighted[3] = ImageIO.read(new File("res/HexagonRiver3h.png"));
			hexagonRiverHighlighted[4] = ImageIO.read(new File("res/HexagonRiver4h.png"));
			hexagonRiverHighlighted[5] = ImageIO.read(new File("res/HexagonRiver5h.png"));
			hexagonRiverHighlighted[6] = ImageIO.read(new File("res/HexagonRiver6h.png"));
			hexagonRiverHighlighted[7] = ImageIO.read(new File("res/HexagonRiver7h.png"));

			hexagonRoadHighlighted[0] = ImageIO.read(new File("res/HexagonRoad0h.png"));
			hexagonRoadHighlighted[1] = ImageIO.read(new File("res/HexagonRoad1h.png"));
			hexagonRoadHighlighted[2] = ImageIO.read(new File("res/HexagonRoad2h.png"));
			hexagonRoadHighlighted[3] = ImageIO.read(new File("res/HexagonRoad3h.png"));
			hexagonRoadHighlighted[4] = ImageIO.read(new File("res/HexagonRoad4h.png"));
			hexagonRoadHighlighted[5] = ImageIO.read(new File("res/HexagonRoad5h.png"));
			hexagonRoadHighlighted[6] = ImageIO.read(new File("res/HexagonRoad6h.png"));
			hexagonRoadHighlighted[7] = ImageIO.read(new File("res/HexagonRoad7h.png"));
		} catch (Exception e) {
		}

	}

	public static void moveScreen(Point curr, Point last) {
		if (Listening.movingScreen) {
			screenX += last.x - curr.x;
			screenY += last.y - curr.y;
		}
	}

	public void start() {
		// Type take considerations:
		typeTake[0][0] = true;
		typeTake[1][0] = true;
		typeTake[1][1] = true;
		typeTake[2][0] = true;
		typeTake[2][2] = true;
		typeTake[3][0] = true;
		typeTake[3][3] = true;
		typeTake[4][0] = true;
		typeTake[4][1] = true;
		typeTake[4][2] = true;
		typeTake[5][0] = true;
		typeTake[5][2] = true;
		typeTake[5][3] = true;
		typeTake[6][0] = true;
		typeTake[6][1] = true;
		typeTake[6][3] = true;
		typeTake[7][0] = true;
		typeTake[7][2] = true;
		typeTake[7][4] = true;

		// Add all possible hexagons:
		for (int river = 0; river < 8; river++) {
			for (int road = 0; road < 8; road++) {
				for (int rotation = 0; rotation < 6; rotation++) {
					boolean toBeCreated = true;
					if (river != 8 && road != 8) {
						for (int i = 0; i < 6; i++) {
							// Exceptions (major) straight + 3 wide
							if (road == 3 && rotation >= 3) {
								toBeCreated = false;
								break;
							}
							if (road == 7 && rotation >= 2) {
								toBeCreated = false;
								break;
							}
							// Minor exceptions
							if (river == 7 && road == 0 && (rotation == 3 || rotation == 5)) {
								toBeCreated = false;
								break;
							}
							if (river == 7 && road == 2 && (rotation == 3 || rotation == 5)) {
								toBeCreated = false;
								break;
							}
							if (river == 3 && road == 0 && (rotation == 4 || rotation == 5)) {
								toBeCreated = false;
								break;
							}
							if (river == 3 && road == 1 && rotation == 4) {
								toBeCreated = false;
								break;
							}
							if (river == 3 && road == 2 && rotation == 2) {
								toBeCreated = false;
								break;
							}
							if (typeTake[river][i] && typeTake[road][(i - rotation + 6) % 6]) {
								toBeCreated = false;
								break;
							}
						}
					}

					if (toBeCreated) {
						RiverRoadCombo r = new RiverRoadCombo(river, road, rotation);
						finishedHexagons.add(r);
						if (r.riverType >= 1) {
							finishedHexagons.add(r);
							finishedHexagons.add(r); // (3)
							finishedHexagons.add(r);
							finishedHexagons.add(r);
						}
						if (r.riverType >= 4) {
							finishedHexagons.add(r); // (4)
							finishedHexagons.add(r);
						}
						if (r.roadType >= 1) {
							finishedHexagons.add(r);
						}
						if (r.roadType >= 4) {
							//finishedHexagons.add(r);
						}
					}
				}
			}
		}

		//start stuff
		for (int i = 0; i < Component.finishedHexagons.size(); i++) {
			//hexagons.add(new Hexagon(i % 20, i / 20, finishedHexagons.get(i)));
		}

		Component.currentHexagon = new Hexagon(0, 0, Component.finishedHexagons.get(new Random().nextInt(Component.finishedHexagons.size())));

		// Butons:
		buttons.add(new Button("Water", new Color(80, 120, 230), 375, 40, 155, 60));
		buttons.add(new Button("Road", new Color(130, 120, 130), 540, 40, 130, 60));
		buttons.add(new Button("Land", new Color(40, 200, 70), 680, 40, 130, 60));
		buttons.add(new Button("None", new Color(200, 200, 200), 820, 40, 133, 60));

		//start loop
		isRunning = true;
		new Thread(this).start();
	}

	public void stop() {
		isRunning = false;
	}

	public static void main(String args[]) {
		Component component = new Component();
		JFrame frame = new JFrame();
		frame.add(component);
		frame.pack();
		frame.setTitle("Hexagon Game");
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		Component.component = component;
		component.start();

	}

	public void tick() {
		GameState.tick();
	}

	public void render() {
		((VolatileImage) screen).validate(getGraphicsConfiguration());
		Graphics g = screen.getGraphics();
		screenPos = getLocationOnScreen();
		mousePos = getMousePosition();
		Listening.currLocation = new Point(MouseInfo.getPointerInfo().getLocation().x - Component.screenPos.x, MouseInfo.getPointerInfo().getLocation().y - Component.screenPos.y);
		moveScreen(Listening.currLocation, Listening.lastLocation);
		Listening.lastLocation = Listening.currLocation;
		//draw:
		g.setColor(new Color(0, 120, 255));
		g.fillRect(0, 0, screenW, screenH);
		Graphics2D g2 = (Graphics2D) g;

		for (int i = 0; i < hexagons.size(); i++) {
			hexagons.get(i).render(g2);
		}

		if (currentHexagon != null && mousePos != null) {
			currentHexagon.renderAlt(mousePos, g2);
		}

		if (GameState.playerState == GameState.CHOOSING_RRL) {
			if (mousePos != null) {
				for (int i = 0; i < buttons.size(); i++) {
					buttons.get(i).update(mousePos);
				}
			}
			for (int i = 0; i < buttons.size(); i++) {
				buttons.get(i).render(g2);
			}
		}

		if (GameState.playerState == GameState.CHOOSING_ROTATION) {
			g.setColor(new Color(255, 255, 255));
			g.setFont(new Font("Verdana", Font.PLAIN, 18));
			g.drawString("Choose location for land. (On placed tile)", 350, screenH - 80);
		}
		if (GameState.playerState == GameState.PLACING_HEXAGON) {
			if (placedHexagon != null) {
				g.setColor(new Color(255, 255, 255));
				g.setFont(new Font("Verdana", Font.PLAIN, 18));
				g.drawString("Press Space to confirm or change location. ", 350, screenH - 80);
			} else {
				g.setColor(new Color(255, 255, 255));
				g.setFont(new Font("Verdana", Font.PLAIN, 18));
				g.drawString("Rotate by scrolling. Left click to place. Middle click to place blocker. ", 350, screenH - 80);
			}
		}

		GameState.render(g2);

		g = getGraphics();
		g.drawImage(screen, 0, 0, screenW, screenH, 0, 0, screenW, screenH, null);
		g.dispose();
	}

	public void run() {
		screen = createVolatileImage(screenW, screenH);
		long lastTime = System.nanoTime();
		double unprocessed = 0;
		double nsPerTick = 1000000000.0 / /*Just in case*/(double) ticksPerSecond;
		while (isRunning) {
			long now = System.nanoTime();
			unprocessed += (now - lastTime) / nsPerTick;
			lastTime = now;
			while (unprocessed >= 1) {
				tick();
				unprocessed -= 1;
			}
			{
				render();
				if (unprocessed < 1) {
					try {
						// Divided by 10, so up to 10x the frames then # of ticks per second.
						Thread.sleep((int) ((1 - unprocessed) * nsPerTick / 10.0) / 1000000, (int) ((1 - unprocessed) * nsPerTick / 10.0) % 1000000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
