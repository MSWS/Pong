package org.mswsplex.pong;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Pong extends Applet implements Runnable, KeyListener, MouseListener {

	ThreadLocalRandom rnd;
	Thread thread;

	Paddle hPaddle, cPaddle;
	Ball ball;

	// Default resolution is 1000:500
	// Personal resolution is 1360:650

	public static final int WIDTH = 1000, HEIGHT = 500;
	private Set<Paddle> paddles;

	Graphics gfx;
	Image img;

	public static int p1Score = 0, p2Score = 0;

	public static Status status;

	private int winner, hits;

	public static final Font FONT = new Font("Consolas", Font.BOLD, 24);

	private long startTime, lastFpsTime, lastRpsTime;

	private float fps, lastFps, rps, lastRps; // Rallies Per Second

	private boolean mousePressed = false;

	private double prevMouseX, prevMouseY;

	@Override
	public void init() {
		resize(WIDTH, HEIGHT);
		hits = 0;

		rnd = ThreadLocalRandom.current();
		paddles = new HashSet<Paddle>();
		startTime = System.currentTimeMillis();
		lastFpsTime = System.currentTimeMillis();

		ball = new Ball(15, 15);

		// Random Level AI

		// hPaddle = new Paddle(Color.WHITE, 950);
		// hPaddle = new AI(Color.WHITE, 950, ball, true, 50, 950,
		// Math.round(rnd.nextDouble() * 1000.0) / 1000.0);
		// cPaddle = new AI(Color.WHITE, 50, ball, false, 50, 950,
		// Math.round(rnd.nextDouble() * 1000.0) / 1000.0);

		// Basic AI
//		hPaddle = new AI(Color.WHITE, (int) (WIDTH * (19.0 / 20.0)), ball, true, (int) (WIDTH * (1.0 / 20.0)),
//				(int) (WIDTH * (19.0 / 20.0)), 0);
//		cPaddle = new AI(Color.WHITE, (int) (WIDTH * (1.0 / 20.0)), ball, true, (int) (WIDTH * (19.0 / 20.0)),
//				(int) (WIDTH * (1.0 / 20.0)), 0);

		// Expert Level AI

		hPaddle = new AI(Color.WHITE, (int) (WIDTH * (19.0 / 20.0)), ball, true, (int) (WIDTH * (1.0 / 20.0)),
				(int) (WIDTH * (19.0 / 20.0)), 1);
		cPaddle = new AI(Color.WHITE, (int) (WIDTH * (1.0 / 20.0)), ball, false, (int) (WIDTH * (1.0 / 20.0)),
				(int) (WIDTH * (19.0 / 20.0)), 1);

		// Human
//		hPaddle = new Paddle(Color.WHITE, (int) (WIDTH * (19.0 / 20.0)));
//		cPaddle = new Paddle(Color.WHITE, (int) (WIDTH * (1.0 / 20.0)));

		paddles.add(cPaddle);
		paddles.add(hPaddle);

		img = createImage(WIDTH, HEIGHT);
		gfx = img.getGraphics();

		gfx.setFont(FONT);

		addKeyListener(this);
		addMouseListener(this);

		thread = new Thread(this);
		thread.start();

		status = Status.START;
	}

	@Override
	public void paint(Graphics g) {
		drawBackground(gfx);

		if (status == Status.START) {
			long time = System.currentTimeMillis() - startTime;

			gfx.setColor(Color.cyan);
			gfx.setFont(FONT.deriveFont((float) ((float) 5 + Math.sin((float) time / 500.0f)) * 10));
			gfx.drawString("Press Enter To Start", WIDTH / 2 - (gfx.getFont().getSize() * 5), HEIGHT / 2);
			gfx.setFont(FONT);
			g.drawImage(img, 0, 0, this);
			return;
		}

		drawLines(gfx);

		if (status == Status.SCORE) {
			gfx.setColor(Color.GREEN);
			gfx.drawString("Player " + winner + " Scored", (int) (WIDTH / 2.25), HEIGHT / 2);
			gfx.setFont(FONT.deriveFont(18f));
			gfx.drawString("Press Enter To Continue", (int) (WIDTH / 2.5), (int) (HEIGHT / 1.8));
			gfx.setFont(FONT);
		} else {
			if (status == Status.PAUSE) {
				gfx.setColor(Color.blue);
				gfx.drawString("Game Paused", (int) (WIDTH / 2.25), HEIGHT / 2);
				gfx.setFont(FONT.deriveFont(18f));
				gfx.drawString("Press Enter To Continue", (int) (WIDTH / 2.5), (int) (HEIGHT / 1.8));
				gfx.setFont(FONT);
			}

			drawBall(gfx);
			drawPaddles(gfx);
		}

		int sm = manageTextAndScores(gfx);
		if (sm != 0) {
			// status = Status.SCORE;
			hits = 0;
			rps = 0;
			lastRps = 0;
			lastRpsTime = System.currentTimeMillis();
			resetPositions();
			winner = sm;
		}

		g.drawImage(img, 0, 0, this);
	}

	public int manageTextAndScores(Graphics g) {
		g.setColor(Color.WHITE);
		g.drawString("FPS: " + lastFps, 10, 20);

		g.setColor(Color.BLUE);
		g.drawString(p1Score + "", (int) (WIDTH * (1.0 / 4.0)), 40);
		if (cPaddle instanceof AI) {
			g.drawString("(" + ((AI) cPaddle).getSkill() + ")", (int) (WIDTH * (1.0 / 4.0)), 60);
		}

		g.setColor(Color.RED);
		g.drawString(p2Score + "", (int) (WIDTH * (3.0 / 4.0)), 40);
		if (hPaddle instanceof AI) {
			g.drawString("(" + ((AI) hPaddle).getSkill() + ")", (int) (WIDTH * (3.0 / 4.0)), 60);
		}

		g.setColor(Color.GRAY);
		g.drawString(hits + " (" + lastRps + ")", (int) (WIDTH / 2.1), HEIGHT / 10);

		g.setColor(Color.WHITE);

		if (ball.getX() <= 0) {
			p2Score++;
			return 2;
		} else if (ball.getX() >= WIDTH - ball.getWidth()) {
			p1Score++;
			return 1;
		}
		return 0;
	}

	public void resetPositions() {
		ball.reset();
		cPaddle.resetPosition();
		hPaddle.resetPosition();
	}

	public void drawBackground(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, WIDTH, HEIGHT);
	}

	public void drawLines(Graphics g) {
		int lineWidth = 2, lineHeight = 30, lineGap = 15;

		g.setColor(Color.WHITE);

		for (int y = 0; y < HEIGHT; y += lineHeight + lineGap) {
			g.fillRect(WIDTH / 2 - (lineWidth / 2), y, lineWidth, lineHeight);
		}
	}

	public void drawPaddles(Graphics g) {
		cPaddle.draw(g);
		hPaddle.draw(g);
	}

	public void drawBall(Graphics g) {
		ball.draw(g);
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}

	@Override
	public void run() {
		while (true) {
			if (status == Status.RUNNING) {
				cPaddle.move();
				hPaddle.move();
				ball.move();

				if (mousePressed && getMousePosition() != null) {
					double velX = getMousePosition().getX() - prevMouseX, velY = getMousePosition().getY() - prevMouseY;
					ball.setX((int) getMousePosition().getX() - ball.getWidth() / 2);
					ball.setY((int) getMousePosition().getY() - ball.getHeight() / 2);
					ball.setXVel(ball.getXVel() + velX);
					ball.setYVel(ball.getYVel() + velY);
					prevMouseX = getMousePosition().getX();
					prevMouseY = getMousePosition().getY();
				}
				if (ball.checkCollision(paddles)) {
					rps++;
					hits++;
				}
			}
			repaint();
			fps++;

			long frameSampleTime = 5000, rallySampleTime = 5000;

			if (System.currentTimeMillis() - lastFpsTime > frameSampleTime) {
				lastFps = (fps / (frameSampleTime / 1000));
				fps = 0;
				lastFpsTime = System.currentTimeMillis();
			}

			if (System.currentTimeMillis() - lastRpsTime > rallySampleTime) {
				lastRps = (rps / (rallySampleTime / 1000));
				rps = 0;
				lastRpsTime = System.currentTimeMillis();
			}

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent key) {

	}

	@Override
	public void keyPressed(KeyEvent key) {
		switch (key.getKeyCode()) {
		case KeyEvent.VK_UP:
			hPaddle.setYVel(-5);
			break;
		case KeyEvent.VK_DOWN:
			hPaddle.setYVel(5);
			break;
		case KeyEvent.VK_W:
			if (!(cPaddle instanceof AI))
				cPaddle.setYVel(-5);
			break;
		case KeyEvent.VK_S:
			if (!(cPaddle instanceof AI))
				cPaddle.setYVel(5);
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent key) {
		if (key.getKeyCode() == KeyEvent.VK_ENTER) {
			switch (status) {
			case START:
			case SCORE:
				ball.reset();
			case PAUSE:
				status = Status.RUNNING;
				break;
			case RUNNING:
				status = Status.PAUSE;
				break;
			}
		}

		if (!(cPaddle instanceof AI)) {
			if ((key.getKeyCode() == KeyEvent.VK_W || key.getKeyCode() == KeyEvent.VK_S)) {
				cPaddle.setYVel(0);
			}
		}

		if (!(key.getKeyCode() == KeyEvent.VK_UP || key.getKeyCode() == KeyEvent.VK_DOWN))
			return;

		hPaddle.setYVel(0);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (status != Status.RUNNING)
			return;
		ball.setXVel(0);
		ball.setYVel(0);
		prevMouseX = e.getX();
		prevMouseY = e.getY();
		mousePressed = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mousePressed = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
		mousePressed = false;
	}
}
