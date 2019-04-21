package org.mswsplex.pong;

public class HEntry {
	private double x, y, vx, vy;

	private long age;

	public HEntry(double x, double y, double vx, double vy) {
		this.x = x;
		this.y = y;
		this.vx = -vx;
		this.vy = -vy;

		age = System.currentTimeMillis();
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public void move() {
		x += vx;
		y += vy;
		vx *= (.9999 / (1 - (1.0 / (getTimeExisted() + .01))));
		vy *= (.9999 / (1 - (1.0 / (getTimeExisted() + .01))));
	}

	public double getVX() {
		return this.vx;
	}

	public double getVY() {
		return this.vy;
	}

	public long getTimeExisted() {
		return System.currentTimeMillis() - age;
	}

	public long getAge() {
		return age;
	}
}
