package org.mswsplex.pong;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AI extends Paddle {

	private Ball ball;

	private List<Double> prevY;

	private int minX, maxX;

	private boolean onRight;

	private double skill; // 0-1 inclusive

	public AI(Color color, int x, Ball ball, boolean onRight, int minX, int maxX, double skill) {
		super(color, x);
		this.ball = ball;
		this.minX = minX;
		this.maxX = maxX;
		this.onRight = onRight;
		this.skill = skill;

		prevY = new ArrayList<>();
	}

	@Override
	public void move() {

		double estY = ball.getY(), tmpX = ball.getX(), tmpY = ball.getY(),
				tmpVX = ball.getXVel() * (10 + ((1 - skill) * 10)), tmpVY = ball.getYVel() * (10 + ((1 - skill) * 10));

		boolean est = false;

		ThreadLocalRandom rnd = ThreadLocalRandom.current();

		int amo = 0;
		while (!est && amo < 1000) {
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

			tmpX += tmpVX + rnd.nextDouble((-(1 - skill)) * 5, (1.01 - skill) * 5);
			tmpY += tmpVY + rnd.nextDouble((-(1 - skill)) * 5, (1.01 - skill) * 5);
			amo++;
		}

		estY += ball.getHeight() / 2;
		estY -= this.getHeight() / 2;

		prevY.add(estY);

		int avgSize = (int) (50 - ((1 - skill) * 45));

		if (prevY.size() > avgSize)
			for (int i = 0; i < avgSize && i < prevY.size(); i++) {
				prevY.remove(i);
			}

		double avgY = 0;

		for (double d : prevY)
			avgY += d;

		avgY /= prevY.size();

		double dist = Math.abs(getY() - avgY);

		if (dist > 5 + (1 - skill) * 50.0) {
			if (getY() > avgY) {
				setYVel((float) (-3 - (skill * 2.5)));
			} else {
				setYVel((float) (3 + (skill * 2.5)));
			}
		} else {
			setYVel(0);
		}

		super.move();
	}

	public double getSkill() {
		return this.skill;
	}

}
