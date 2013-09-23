package net.letscode.game.misc;

/**
 *
 * @author timothyb89
 */
public interface QuadTreeItem {

	public double getX();
	public double getY();
	
	public QuadTree getQuadTreeParent();
	public void setQuadTreeParent(QuadTree parent);
	
}
