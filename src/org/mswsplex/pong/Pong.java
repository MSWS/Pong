package org.mswsplex.pong;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Pong extends Applet implements Runnable, KeyListener {

	ThreadLocalRandom rnd;
	Thread thread;

	Paddle hPaddle;
	AI cPaddle;
	Ball ball;

	public static final int WIDTH = 1000, HEIGHT = 500;
	private Set<Paddle> paddles;

	Graphics gfx;
	Image img;

	public static int p1Score = 0, p2Score = 0;

	public static Status status;

	private int winner, hits;

	public static final Font FONT = new Font("Consolas", Font.BOLD, 24);

	private long startTime;

	@Override
	public void init() {
		resize(WIDTH, HEIGHT);
		hits = 0;

		rnd = ThreadLocalRandom.current();
		paddles = new HashSet<Paddle>();
		startTime = System.currentTimeMillis();

		ball = new Ball(Color.GREEN, 20, 20);

		// hPaddle = new Paddle(Color.WHITE, 950);
		// hPaddle = new AI(Color.WHITE, 950, ball, true, 50, 950,
		// Math.round(rnd.nextDouble() * 1000.0) / 1000.0);
		// cPaddle = new AI(Color.WHITE, 50, ball, false, 50, 950,
		// Math.round(rnd.nextDouble() * 1000.0) / 1000.0);

		hPaddle = new AI(Color.WHITE, 950, ball, true, 50, 950, 1);
		cPaddle = new AI(Color.WHITE, 50, ball, false, 50, 950, 1);

		paddles.add(cPaddle);
		paddles.add(hPaddle);

		img = createImage(WIDTH, HEIGHT);
		gfx = img.getGraphics();

		gfx.setFont(FONT);

		this.addKeyListener(this);

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

		int sm = manageTextAndScores(gfx);
		if (sm != 0) {
			// status = Status.SCORE;
			hits = 0;
			resetPositions();
			winner = sm;
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

			drawPaddles(gfx);
			drawBall(gfx);
		}

		g.drawImage(img, 0, 0, this);
	}

	public int manageTextAndScores(Graphics g) {
		g.setColor(Color.WHITE);

		g.drawString("P1 Score: " + p1Score, 50, 40);
		g.drawString("P2 Score: " + p2Score, 750, 40);

		g.drawString("Rallies: " + hits, (int) (WIDTH / 2.35), HEIGHT / 10);

		if (hPaddle instanceof AI) {
			g.drawString("Difficulty: " + ((AI) hPaddle).getSkill(), 750, 60);
		}
		if (cPaddle instanceof AI) {
			g.drawString("Difficulty: " + ((AI) cPaddle).getSkill(), 50, 60);
		}

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
		int lineWidth = 5, lineHeight = 30, lineGap = 15;

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
				if (ball.checkCollision(paddles))
					hits++;
			}
			repaint();
			try {
				Thread.sleep(1);
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
		}
	}

	@Override
	public void keyReleased(KeyEvent key) {
		if (key.getKeyCode() == KeyEvent.VK_ENTER) {
			switch (status) {
			case START:
			case SCORE:
			case PAUSE:
				status = Status.RUNNING;
				break;
			case RUNNING:
				status = Status.PAUSE;
				break;
			}
		}

		if (!(key.getKeyCode() == KeyEvent.VK_UP || key.getKeyCode() == KeyEvent.VK_DOWN))
			return;

		hPaddle.setYVel(0);
	}
}
