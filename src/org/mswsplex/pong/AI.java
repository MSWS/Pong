package org.mswsplex.pong;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class AI extends Paddle {

	private Ball ball;

	private List<Double> prevY;

	private int minX, maxX;

	private boolean onRight;

	public AI(Color color, int x, Ball ball, boolean onRight, int minX, int maxX) {
		super(color, x);
		this.ball = ball;
		this.minX = minX;
		this.maxX = maxX;
		this.onRight = onRight;

		prevY = new ArrayList<>();
	}

	@Override
	public void move() {

		double estY = ball.getY(), tmpX = ball.getX(), tmpY = ball.getY(), tmpVX = ball.getXVel() * 10,
				tmpVY = ball.getYVel() * 10;

		boolean est = false;

		int amo = 0;
		while (!est && amo < 50) {
			if (onRight) {
				if (tmpX >= this.getX() - ball.getWidth()) {
					estY = tmpY;
					est = true;
				}
			} else {
				if (tmpX <= this.getX() + this.getWidth()) {
					estY = tmpY;
					est = true;
				}
			}

			if (tmpX >= maxX || tmpX <= minX) {
				tmpVX = -tmpVX;
			}

			if (tmpY <= 0) {
				tmpY = 0;
				tmpVY = -tmpVY;
			}

			if (tmpY > Pong.HEIGHT - ball.getHeight()) {
				tmpY = Pong.HEIGHT - ball.getHeight();
				tmpVY = -tmpVY;
			}

			tmpX += tmpVX;
			tmpY += tmpVY;
			amo++;
		}

		estY += ball.getHeight() / 2;
		estY -= this.getHeight() / 2;

		prevY.add(estY);

		int avgSize = 50;

		if (prevY.size() > avgSize)
			for (int i = 0; i < avgSize && i < prevY.size(); i++) {
				prevY.remove(i);
			}

		double avgY = 0;

		for (double d : prevY)
			avgY += d;

		avgY /= prevY.size();

		double dist = Math.abs(getY() - avgY);

		if (dist > 5) {
			if (getY() > avgY) {
				setYVel(-3);
			} else {
				setYVel(3);
			}
		} else {
			setYVel(0);
		}

		super.move();
	}

}
