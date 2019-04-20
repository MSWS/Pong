package org.mswsplex.pong;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Ball {
	private float width, height;
	private double x, y, xVel, yVel;

	private final float fWidth, fHeight;

	private Color color;

	private List<Integer> historyX, historyY;

	public Ball(Color color, float width, float height) {
		this.fWidth = width;
		this.fHeight = height;
		this.color = color;

		reset();
	}

	public void setX(int x) {
		this.x = x;
	}

	public void reset() {
		this.x = Pong.WIDTH / 2 - (width * (1 / 4));
		this.y = Pong.HEIGHT / 2 - (height * (1 / 4));

		this.width = fWidth;
		this.height = fHeight;

		historyX = new ArrayList<>();
		historyY = new ArrayList<>();

		ThreadLocalRandom rnd = ThreadLocalRandom.current();

		xVel = rnd.nextBoolean() ? 2 + rnd.nextDouble() : -2 - rnd.nextDouble();
		yVel = rnd.nextDouble(-5, 5);
	}

	public void setY(int y) {
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getXVel() {
		return this.xVel;
	}

	public double getYVel() {
		return this.yVel;
	}

	public int getWidth() {
		return (int) this.width;
	}

	public int getHeight() {
		return (int) this.height;
	}

	public void move() {
		x += xVel;
		y += yVel;

		if (y <= 0) {
			yVel = -yVel;
			y = 0;
		} else if (y >= Pong.HEIGHT - height) {
			yVel = -yVel;
			y = Pong.HEIGHT - height;
		}

		historyX.add((int) x);
		historyY.add((int) y);

		int hSize = 100;

		if (historyX.size() > hSize)
			for (int i = 0; i < historyX.size() - hSize; i++) {
				historyX.remove(i);
				historyY.remove(i);
			}

		for (int i = historyX.size() - 1; i >= 0; i--) {
			if (historyX.get(i) == 0)
				continue;
//
//			historyX.set(i, (int) ((historyX.get(i) + xVel) * 1.01));
//			historyY.set(i, (int) ((historyY.get(i) + yVel) * .99));
		}
	}

	public boolean checkCollision(Set<Paddle> paddles) {
		for (Paddle paddle : paddles) {
			if (x + width >= paddle.getX() && x <= paddle.getX() + paddle.getWidth()) {
				// within X coords of paddle

				if (y + height >= paddle.getY() && y <= paddle.getY() + paddle.getHeight()) {
					// within Y coords of paddle
					// hitting left side of paddle
					xVel = -xVel * ThreadLocalRandom.current().nextDouble(1, 1.05);
					yVel = ((y + height / 2) - (paddle.getY() + paddle.getHeight() / 2)) / 8
							+ ThreadLocalRandom.current().nextDouble(-.5, .5);
					width = (float) Math.max(10, width * .99);
					height = (float) Math.max(10, height * .99);
					return true;
				}
			}
		}
		return false;
	}

	public void draw(Graphics g) {
		int pX = -1, pY = -1;
		for (int i = 1; i < historyX.size(); i++) {
			if (i == 0) {
				continue;
			}

			pX = historyX.get(i - 1);
			pY = historyY.get(i - 1);
			g.setColor(new Color((int) (((double) i / historyX.size()) * 255),
					(int) (((double) i / historyX.size()) * 255), (int) (((double) i / historyX.size()) * 255)));
			// g.fillRect(historyX.get(i) + this.getWidth() / 2, historyY.get(i) +
			// this.getHeight() / 2, 1, 1);

			g.drawLine(pX + this.getWidth() / 2, pY + this.getHeight() / 2, historyX.get(i) + this.getWidth() / 2,
					historyY.get(i) + this.getHeight() / 2);
		}
		g.setColor(color);
		g.fillRect((int) x, (int) y, (int) width, (int) height);

	}
}
