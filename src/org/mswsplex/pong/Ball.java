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

	private List<HEntry> history;

	public Ball(float width, float height) {
		this.fWidth = width;
		this.fHeight = height;

		reset();
	}

	public void reset() {
		this.x = Pong.WIDTH / 2 - (width * (1 / 4));
		this.y = Pong.HEIGHT / 2 - (height * (1 / 4));

		this.width = fWidth;
		this.height = fHeight;

		history = new ArrayList<>();

		ThreadLocalRandom rnd = ThreadLocalRandom.current();

		xVel = rnd.nextBoolean() ? 2.5 + rnd.nextDouble() : -2.5 - rnd.nextDouble();
		yVel = rnd.nextDouble(-5, 5);
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setXVel(double x) {
		xVel = x;
	}

	public void setYVel(double y) {
		yVel = y;
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

		int hSize = 500;

		history.add(new HEntry(x, y, xVel, yVel));

		if (history.size() > hSize) {
			for (int i = 0; i < history.size() - hSize; i++) {
				history.remove(i);
			}
		}

	}

	public boolean checkCollision(Set<Paddle> paddles) {
		for (Paddle paddle : paddles) {
			if (x + width >= paddle.getX() && x <= paddle.getX() + paddle.getWidth()) {
				if (y + height >= paddle.getY() && y <= paddle.getY() + paddle.getHeight()) {
					xVel = -xVel * ThreadLocalRandom.current().nextDouble(1.01, 1.06);
					yVel = ((y + height / 2) - (paddle.getY() + paddle.getHeight() / 2)) / 2.5
							+ ThreadLocalRandom.current().nextDouble(-.5, .5);
					width = (float) Math.max(5, width * .99);
					height = (float) Math.max(5, height * .99);
					return true;
				}
			}
		}
		return false;
	}

	public void draw(Graphics g) {
		int px = -1, py = -1;
		if (Pong.status == Status.RUNNING)
			for (HEntry he : history)
				he.move();
		for (int i = 1; i < history.size(); i++) {
			if (i == 0) {
				continue;
			}
			px = (int) history.get(i - 1).getX();
			py = (int) history.get(i - 1).getY();

			g.setColor(new Color(Color.HSBtoRGB(((float) i / (float) history.size() / 150.0f) * 360.0f, 1.0f,
					(float) i / history.size())));
			g.drawLine(px + this.getWidth() / 2, py + this.getHeight() / 2,
					(int) (history.get(i).getX() + this.getWidth() / 2),
					(int) (history.get(i).getY() + this.getHeight() / 2));
		}

		int red = 0, blue = 0;

		if (x + getWidth() / 2 > Pong.WIDTH / 2) {
			red = (int) (((x + getWidth() / 2) - Pong.WIDTH / 2.0) / (Pong.WIDTH / 2.0) * 255.0);
		} else {
			blue = (int) (((Pong.WIDTH / 2.0 - x) / (Pong.WIDTH / 2.0)) * 255.0);
		}

		red = Math.min(Math.max(red, 0), 255);
		blue = Math.min(Math.max(blue, 0), 255);

		g.setColor(new Color(red, Math.max(150 - (red + blue), 0), blue));
		g.fillRect((int) x, (int) y, (int) width, (int) height);

	}
}
