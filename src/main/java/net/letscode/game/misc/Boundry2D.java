package net.letscode.game.misc;

import lombok.ToString;

/**
 *
 * @author timothyb
 */
@ToString
public class Boundry2D {

	public double x;
	public double y;
	public double width;
	public double height;

	public Boundry2D() {
		this.x = 0;
		this.y = 0;
		this.width = 0;
		this.height = 0;
	}

	public Boundry2D(double width, double height) {
		this.x = 0;
		this.y = 0;
		this.width = width;
		this.height = height;
	}

	public Boundry2D(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public Point getPosition() {
		return new Point(x, y);
	}

	public boolean contains(double x, double y) {
		double halfWidth = width / 2;
		double halfHeight = height / 2;

		return (x >= this.x - halfWidth && x <= this.x + halfWidth)
				&& (y >= this.y - halfHeight && y <= this.y + halfHeight);
	}

	public boolean contains(Point p) {
		return contains(p.x, p.y);
	}
}
