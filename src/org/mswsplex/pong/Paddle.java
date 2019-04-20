package org.mswsplex.pong;

import java.awt.Color;
import java.awt.Graphics;

public class Paddle {
	private int height, width, x, y;
	private float xVel, yVel, maxYVel, maxXVel;

	private Color color;

	public Paddle(Color color, int x) {
		this.height = 70;
		this.width = 10;
		this.xVel = 0;
		this.yVel = 0;
		this.maxXVel = 0;
		this.maxYVel = 3;

		this.y = Pong.HEIGHT / 2 - (height * 1 / 4);
		this.x = x;

		this.color = color;
	}

	public void resetPosition() {
		this.xVel = 0;
		this.yVel = 0;
		this.y = Pong.HEIGHT / 2 - (height * 1 / 4);
	}

	public void draw(Graphics g) {
		g.setColor(color);

		g.fillRect(x, y, width, height);
	}

	public void move() {
		this.y += yVel;
		if (this.y < 0) {
			this.y = 0;
		} else if (this.y > Pong.HEIGHT - this.height) {
			this.y = Pong.HEIGHT - this.height;
		}
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = Math.min(Math.max(0, y), Pong.HEIGHT - this.height);
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public float getXVel() {
		return xVel;
	}

	public float getYVel() {
		return yVel;
	}

	public Color getColor() {
		return this.color;
	}

	public void setXVel(float xVel) {
		this.xVel = Math.min(Math.max(xVel, -maxXVel), maxXVel);
	}

	public void setYVel(float yVel) {
		this.yVel = Math.min(Math.max(yVel, -maxYVel), maxYVel);
	}

	public void addXVel(float xVel) {
		setXVel(this.xVel + xVel);
	}

	public void addYVel(float yVel) {
		setYVel(this.yVel + yVel);
	}
}
