package net.letscode.game.misc;

import lombok.ToString;

/**
 *
 * @author timothyb
 */
@ToString
public class Point2D {

	public double x;
	public double y;

	public Point2D() {
		x = 0;
		y = 0;
	}

	public Point2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Localizes this point such that the returned point will be positioned
	 * relative to
	 * <code>origin</code>. That is, the new point will use
	 * <code>origin</code> as its (0,0).
	 *
	 * @param origin the origin
	 * @return a localized instance of this point relative to the origin
	 */
	public Point2D localize(Point2D origin) {
		return new Point2D(
				x + -1 * (origin.x),
				y + -1 * (origin.y));
	}

	/**
	 * Finds the quadrant this point resides in, assuming an origin of
	 * <code>(0, 0)</code>. Note that this is inclusive towards the northeast,
	 * meaning that zero values for either the x or y coordinates will favor
	 * north (for the y axis) and west (for the x axis).
	 *
	 * @return the quadrant this point resides in
	 */
	public QuadTree.Quadrant getQuadrant() {
		if (y >= 0) {
			if (x >= 0) {
				return QuadTree.Quadrant.NORTHEAST;
			} else {
				return QuadTree.Quadrant.NORTHWEST;
			}
		} else {
			if (x >= 0) {
				return QuadTree.Quadrant.SOUTHEAST;
			} else {
				return QuadTree.Quadrant.SOUTHWEST;
			}
		}
	}
}
