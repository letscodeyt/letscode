package net.letscode.game.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.Getter;
import lombok.ToString;

/**
 * Implements a 'static' Quadtree, a variation of a data structure used to aid
 * in locating entities (or groups of entities) within 2D space. For rough
 * details on this implementation, see
 * <a href="http://en.wikipedia.org/wiki/Quadtree">the Wikipedia</a> article.
 * 
 * <p>This implementation makes a few modifications to the standard algorithm,
 * mainly in that the initial boundry size is static, and all subdivisions are
 * exactly in half. That is, all subtrees are of a predetermined size.</p>
 * 
 * <p>The final subtree, at <code>MAX_DEPTH</code> is special in that it is the
 * only subtree that should contain {@link QuadTreeItem} instances</p>
 * 
 * <p>Note that when iterating over this tree, items are only guaranteed to be
 * ordered within <code>[axis] / 2^(maxDepth)</code> units. This structure is
 * intended primarily for filtering groups of entities within a region, and
 * should not be used for anything that requires accurate positioning.</p>
 * 
 * <p>For the 3-dimensional equivalent, an
 * <a href="http://en.wikipedia.org/wiki/Octree">Octree</a> should be used
 * instead.</p>
 * @author timothyb89
 */
public class QuadTree<T extends QuadTreeItem> implements Iterable<T> {
	
	@Getter private int maxDepth;
	@Getter private int depth;
	@Getter private QuadTree root;
	@Getter private QuadTree parent;
	@Getter private Boundry boundry;
	
	private QuadTree<T> northWest;
	private QuadTree<T> northEast;
	private QuadTree<T> southWest;
	private QuadTree<T> southEast;
	
	/**
	 * The list of children. This is only ever used if this tree is a leaf node
	 */
	private List<T> children;
	
	public QuadTree(Boundry boundry, int maxDepth) {
		this.boundry = boundry;
		
		this.maxDepth = maxDepth;
		this.depth = 1;
		this.root = this;
		this.parent = null;
		this.children = new LinkedList<>();
	}
	
	/**
	 * Creates a new QuadTree with the given boundry and maximum depth. Note
	 * that the maximum depth directly affects the accuracy of the tree, and a
	 * maximum depth that is too small will result in excessively coarse entity
	 * organization.
	 * @param x the boundry x
	 * @param y the boundry y
	 * @param width the boundry width
	 * @param height the boundry height
	 * @param maxDepth the maximum depth for the quadtree
	 */
	public QuadTree(
			double x, double y, double width, double height, int maxDepth) {
		this(new Boundry(x, y, width, height), maxDepth);
	}
	
	/**
	 * Creates a new QuadTree for the given quadrant within the parent. The
	 * boundry is calculated based on the parent and the specified quadrant.
	 * @param parent the parent tree
	 * @param quadrant the quadrant 
	 */
	public QuadTree(QuadTree parent, Quadrant quadrant) {
		this.parent = parent;
		this.root = parent.getRoot();
		
		this.maxDepth = parent.getMaxDepth();
		this.depth = parent.getDepth() + 1;
		
		// only initialize the list if we're at the max depth
		// this is mainly to force an exception to be thrown if the list is ever
		// modified when it shouldn't be
		if (depth == maxDepth) {
			this.children = new LinkedList<>();
		}
		
		double px = parent.boundry.x;
		double py = parent.boundry.y;
		
		double width = parent.boundry.width / 2;
		double height = parent.boundry.height / 2;
		
		double pqw = parent.boundry.width / 4;  // parent quarter width
		double pqh = parent.boundry.height / 4; // parent quarter height
		
		// subdivide: width and height are always half the parent's, and (X,Y)
		// values are centered within the resulting rectangle
		switch (quadrant) {
			case NORTHEAST:
				this.boundry = new Boundry(
						px + pqw, py + pqh,
						width, height);
				break;
			case NORTHWEST:
				this.boundry = new Boundry(
						px - pqw, py + pqh,
						width, height);
				break;
			case SOUTHEAST:
				this.boundry = new Boundry(
						px + pqw, py - pqh,
						width, height);
				break;
			case SOUTHWEST:
				this.boundry = new Boundry(
						px - pqw, py - pqh,
						width, height);
				break;
		}
	}
	
	/**
	 * Gets the subtree for the given quadrant. Note that this will return null
	 * for empty subtrees, assuming that they have been pruned.
	 * @param q the quadrant
	 * @return the subtree for the given quadrant, or null
	 */
	public QuadTree<T> getSubtree(Quadrant q) {
		switch (q) {
			case NORTHEAST:
				return northEast;
			case NORTHWEST:
				return northWest;
			case SOUTHEAST:
				return southEast;
			case SOUTHWEST:
				return southWest;
			default:
				return null;
		}
	}
	
	/**
	 * Sets the given subtree to the provided value.
	 * @param q the quadrant to set
	 * @param tree the value to set for the given quadrant
	 */
	public void setSubtree(Quadrant q, QuadTree tree) {
		switch (q) {
			case NORTHEAST:
				this.northEast = tree;
				break;
			case NORTHWEST:
				this.northWest = tree;
				break;
			case SOUTHEAST:
				this.southEast = tree;
				break;
			case SOUTHWEST:
				this.southWest = tree;
				break;
		}
	}
	
	/**
	 * Inserts the given item into the tree. Any necessary subtrees will be
	 * created, up to <code>maxDepth</code>.
	 * @param item the item to insert
	 */
	public void insert(T item) {
		if (item.getQuadTreeParent() != null) {
			throw new IllegalArgumentException(
					"Can't insert item into tree twice; remove it first.");
		}
		
		Point ip = new Point(item.getX(), item.getY());
		
		if (!boundry.contains(ip)) {
			throw new IllegalArgumentException(
					"Item is out of bounds: " + ip + " cannot be contained "
					+ "within " + boundry);
		}
		
		Point l = ip.localize(boundry.getPosition());
		
		if (depth < maxDepth) {
			// recurse to the appropriate quadrant
			Quadrant q = l.getQuadrant();
			QuadTree<T> sub = getSubtree(q);
			
			// if the subtree doesn't exist yet, create it
			if (sub == null) {
				sub = new QuadTree<>(this, q);
				setSubtree(q, sub);
			}
			
			// recurse
			sub.insert(item);
		} else {
			// actually perform the insertion
			children.add(item);
			item.setQuadTreeParent(this);
		}
	}
	
	/**
	 * Removes the given item from this tree. The tree is then pruned,
	 * nullifying trees emptied as a result of the removal.
	 * 
	 * <p>In particular, this will work on any item contained even within
	 * sibling trees, as this method (for indirect parents) just removes the
	 * child from the parent using {@code item.getQuadTreeParent().remove()}.
	 * This ends up being cheaper to do than searching subtrees for the real
	 * parent, and is also somewhat more lenient.</p>
	 * 
	 * <p>Note that this method technically works with any QuadTreeItem, but is
	 * restricted to items within the same overall tree (i.e. same root
	 * tree).</p>
	 * @param item the item to remove
	 */
	public void remove(T item) {
		QuadTree<T> p = item.getQuadTreeParent();
		if (p == null) {
			throw new IllegalArgumentException("Item has a null parent tree");
		}
		
		if (p == this) {
			// if this item belongs to us, remove it and prune
			// if for some reason the item isn't actually in our list ...
			// oh well
			children.remove(item);
			item.setQuadTreeParent(null);
			prune();
		} else {
			// indirect parent

			QuadTree<T> r = p.getRoot();

			// make sure the roots match
			// there's no technical reason to check for this, but if it ever
			// happens, it's pratically guaranteed to be a programmer error.
			if (r != root) {
				throw new IllegalArgumentException(
						"Item is contained within a completely separate tree "
						+ "and cannot be removed.");
			}

			// ask the real parent to remove the item for us
		}
	}
	
	/**
	 * Prunes this QuadTree, removing any empty subtrees. Fundamentally this
	 * allows the child tree to be garbage collected by removing any remaining
	 * references to it.
	 */
	private void prune() {
		if (northWest != null && northWest.isEmpty()) {
			northWest = null;
		}
		
		if (northEast != null && northEast.isEmpty()) {
			northEast = null;
		}
		
		if (southWest != null && southWest.isEmpty()) {
			southWest = null;
		}
		
		if (southEast != null && southEast.isEmpty()) {
			southEast = null;
		}
		
		// recurse upwards
		if (parent != null) {
			parent.prune();
		}
	}
	
	/**
	 * Checks that this quadtree contains no children. This will call the
	 * respective <code>isEmpty()</code> methods of any non-null subtrees.
	 * @return true if this tree and its subtrees contain no children, false if
	 *     at least one does
	 */
	public boolean isEmpty() {
		// nonempty child list means this is a leaf node and does not have any
		// sub-boundries, so we don't have to check them
		if (!children.isEmpty()) {
			return false;
		}
		
		return ((northWest != null) ? northWest.isEmpty() : true)
				&& ((northEast != null) ? northEast.isEmpty() : true)
				&& ((southWest != null) ? southWest.isEmpty() : true)
				&& ((southEast != null) ? southEast.isEmpty() : true);
	}

	/**
	 * Returns an unmodifiable list of children contained (directly) within this
	 * QuadTree. Note that this specifically only direct children; the tree can
	 * be iterated over if a list of all children is needed.
	 * <p>This should always return null for QuadTrees that are already at the
	 * maximum depth.</p>
	 * @return an unmodifiable list of direct children for this tree
	 */
	public List<T> getChildren() {
		return Collections.unmodifiableList(children);
	}

	@Override
	public Iterator<T> iterator() {
		if (children != null) {
			return children.iterator();
		} else {
			return new QuadTreeIterator();
		}
	}
	
	public class QuadTreeIterator implements Iterator<T> {

		private Quadrant quad;
		private Iterator<T> childIterator;
		
		private T next;
		
		public QuadTreeIterator() {
			quad = Quadrant.SOUTHEAST;
			childIterator = null;
			
			// fast-forward the iterator to find the first 'next' value
			nextChild();
		}
		
		private boolean nextQuadrant() {
			if (quad != null) {
				quad = quad.getNext();
				return quad != null;
			}
			
			return false;
		}
		
		private void nextChild() {
			// look for quadrants until we can get a good iterator
			// this is skipped if our current iterator is already valid
			while (childIterator == null || !childIterator.hasNext()) {
				// try to get the next quadrant
				if (!nextQuadrant()) {
					break; // we're out of quadrants to search
				}
				
				QuadTree<T> sub = getSubtree(quad);
				if (sub == null) {
					continue;
				}
				
				childIterator = sub.iterator();
			}
			
			if (quad == null) {
				// we ran out of quadrants
				next = null;
			} else {
				next = childIterator.next();
			}
		}
		
		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public T next() {
			if (next == null) {
				throw new NoSuchElementException("No remaining items in tree.");
			}
			
			T ret = next;
			
			nextChild();
			
			return ret;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("removal not supported");
		}
		
	}
	
	@ToString
	public static class Boundry {
		
		public double x;
		public double y;
		public double width;
		public double height;
		
		public Boundry() {
			this.x = 0;
			this.y = 0;
			this.width = 0;
			this.height = 0;
		}
		
		public Boundry(double width, double height) {
			this.x = 0;
			this.y = 0;
			this.width = width;
			this.height = height;
		}
		
		public Boundry(double x, double y, double width, double height) {
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
	
	@ToString
	public static class Point {
		
		public double x;
		public double y;
		
		public Point() {
			x = 0;
			y = 0;
		}
		
		public Point(double x, double y) {
			this.x = x;
			this.y = y;
		}
		
		/**
		 * Localizes this point such that the returned point will be positioned
		 * relative to <code>origin</code>. That is, the new point will use
		 * <code>origin</code> as its (0,0).
		 * @param origin the origin
		 * @return a localized instance of this point relative to the origin
		 */
		public Point localize(Point origin) {
			return new Point(
					x + -1 * (origin.x),
					y + -1 * (origin.y));
		}
		
		/**
		 * Finds the quadrant this point resides in, assuming an origin of
		 * <code>(0, 0)</code>. Note that this is inclusive towards the
		 * northeast, meaning that zero values for either the x or y coordinates
		 * will favor north (for the y axis) and west (for the x axis).
		 * @return the quadrant this point resides in
		 */
		public Quadrant getQuadrant() {
			if (y >= 0) {
				if (x >= 0) {
					return Quadrant.NORTHEAST;
				} else {
					return Quadrant.NORTHWEST;
				}
			} else {
				if (x >= 0) {
					return Quadrant.SOUTHEAST;
				} else {
					return Quadrant.SOUTHWEST;
				}
			}
		}

	}
	
	public enum Quadrant {
		
		NORTHWEST,
		NORTHEAST(NORTHWEST),
		SOUTHWEST(NORTHEAST),
		SOUTHEAST(SOUTHWEST); // iteration starts here
		
		private Quadrant next;
		
		private Quadrant(Quadrant next) {
			this.next = next;
		}
		
		private Quadrant() {
			this.next = null;
		}

		public Quadrant getNext() {
			return next;
		}
		
		public static Quadrant getIterableStart() {
			return SOUTHEAST;
		}
		
	}
	
	@ToString(exclude = {"parent"})
	public static class TestItem implements QuadTreeItem {

		private double x;
		private double y;
		
		private QuadTree parent;
		
		public TestItem(double x, double y) {
			this.x = x;
			this.y = y;
		}
		
		@Override
		public double getX() {
			return x;
		}

		@Override
		public double getY() {
			return y;
		}

		@Override
		public QuadTree getQuadTreeParent() {
			return parent;
		}

		@Override
		public void setQuadTreeParent(QuadTree parent) {
			this.parent = parent;
		}
		
	}
	
	public static void main(String[] args) {
		QuadTree<TestItem> tree = new QuadTree<>(0, 0, 16, 16, 4);
		
		// northeast
		tree.insert(new TestItem(0, 0));
		tree.insert(new TestItem(0.01, 0.01));
		
		tree.insert(new TestItem(4, 4));
		tree.insert(new TestItem(8, 8));
		
		// northwest
		tree.insert(new TestItem(-4, 4));
		
		// southwest
		tree.insert(new TestItem(-4, -4));
		
		System.out.println("All items:");
		for (TestItem i : tree) {
			System.out.println("\t" + i);
		}
		
		System.out.println();
		
		Quadrant q = Quadrant.getIterableStart();
		do {
			QuadTree<TestItem> t = tree.getSubtree(q);
			if (t == null) {
				continue;
			}
			
			System.out.println("Quadrant: " + q);
			for (TestItem i : t) {
				System.out.println("\t" + i);
			}
			
			System.out.println();
		} while ((q = q.getNext()) != null);
	}
	
}
