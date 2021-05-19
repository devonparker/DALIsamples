import java.util.ArrayList;
import java.util.List;

/**
 * A point quadtree: stores an element at a 2D position, 
 * with children at the subdivided quadrants
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author CBK, Spring 2016, explicit rectangle
 * @author CBK, Fall 2016, generic with Point2D interface
 * 
 */
public class PointQuadtree<E extends Point2D> {
	private E point;							// the point anchoring this node
	private int x1, y1;							// upper-left corner of the region
	private int x2, y2;							// lower-right corner of the region
	private PointQuadtree<E> c1, c2, c3, c4;	// children

	/**
	 * Initializes a leaf quadtree, holding the point in the rectangle
	 */
	public PointQuadtree(E point, int x1, int y1, int x2, int y2) {
		this.point = point;
		this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
	}

	// Getters
	
	public E getPoint() {
		return point;
	}

	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	public int getX2() {
		return x2;
	}

	public int getY2() {
		return y2;
	}

	/**
	 * Returns the child (if any) at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public PointQuadtree<E> getChild(int quadrant) {
		if (quadrant==1) return c1;
		if (quadrant==2) return c2;
		if (quadrant==3) return c3;
		if (quadrant==4) return c4;
		return null;
	}

	/**
	 * Returns whether or not there is a child at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public boolean hasChild(int quadrant) {
		return (quadrant==1 && c1!=null) || (quadrant==2 && c2!=null) || (quadrant==3 && c3!=null) || (quadrant==4 && c4!=null);
	}

	/**
	 * Inserts the point into the tree
	 */
	public void insert(E p2) {
		// TODO: YOUR CODE HERE
		if (point.getX() <= p2.getX() && p2.getX()<= x2 && y1 <= p2.getY() && p2.getY() <= point.getY()){			// point in quadrant 1
			if (hasChild(1)){ getChild(1).insert(p2);}														// if has a child, recursive call
			else {c1 = new PointQuadtree<E>(p2,(int) point.getX(), y1, x2, (int)point.getY());}						// if not, set child to be p2 with updated region
		}
		else if (x1 <= p2.getX() && p2.getX()<= point.getX() && y1 <= p2.getY() && p2.getY() <= point.getY()){		// same process for point in quadrant 2
			if (hasChild(2)){ getChild(2).insert(p2);}
			else {c2 = new PointQuadtree<E>(p2,x1, y1, (int)point.getX(), (int)point.getY());}
		}
		if (x1 <= p2.getX() && p2.getX()<= point.getX() && point.getY() <= p2.getY() && p2.getY() <= y2){			// same process for point in quadrant 3
			if (hasChild(3)){ getChild(3).insert(p2);}
			else {c3 = new PointQuadtree<E>(p2,x1, (int)point.getY(), (int)point.getX(), y2);}
		}
		else if (point.getX() <= p2.getX() && p2.getX()<= x2 && point.getY() <= p2.getY() && p2.getY() <= y2){		// same process for point in quadrant 4
			if (hasChild(4)){ getChild(4).insert(p2);}
			else {c4 = new PointQuadtree<E>(p2,(int)point.getX(), (int)point.getY(), x2, y2);}
		}
	}
	
	/**
	 * Finds the number of points in the quadtree (including its descendants)
	 */
	public int size() {
		// TODO: YOUR CODE HERE -- compute directly, using only numbers not lists (i.e., don't just call allPoints() and return its size)
		int sum = 0;
		if (hasChild(1)){								// check for child in each quadrant
			sum = sum + getChild(1).size();						// increase the sum by the sum each child returned
		}
		if (hasChild(2)){								// repeat above process for each quadrant
			sum = sum + getChild(2).size();
		}
		if (hasChild(3)){
			sum = sum + getChild(3).size();
		}
		if (hasChild(4)){
			sum = sum + getChild(4).size();
		}
		sum = sum + 1;											// count the node which the size method was called on
		return sum;
	}
	
	/**
	 * Builds a list of all the points in the quadtree (including its descendants)
	 */
	public List<E> allPoints() {
		// TODO: YOUR CODE HERE -- efficiency matters!
		List<E> allpoints = new ArrayList<E>();					// create list here and use helper function to add all points to it
		addToPoints(allpoints);									// call helper function
		return allpoints;
	}

	/**
	 * Uses the quadtree to find all points within the circle
	 * @param cx	circle center x
	 * @param cy  	circle center y
	 * @param cr  	circle radius
	 * @return    	the points in the circle (and the qt's rectangle)
	 */
	public List<E> findInCircle(double cx, double cy, double cr) {
		// TODO: YOUR CODE HERE -- efficiency matters!
		List<E> circlePoints = new ArrayList<E>();				// create list and use helper function to add to it
		addToCircle(circlePoints, cx, cy ,cr);					// call helper function
		return circlePoints;
	}

	// TODO: YOUR CODE HERE for any helper methods
	// recursively go through all nodes of the tree and add them to the points list

	private void addToPoints(List<E> points){
		if (hasChild(1)){								// call the function on any/every child
			getChild(1).addToPoints(points);
		}
		if (hasChild(2)){
			getChild(2).addToPoints(points);
		}
		if (hasChild(3)){
			getChild(3).addToPoints(points);
		}
		if (hasChild(4)){
			getChild(4).addToPoints(points);
		}
		points.add(point);										// after going through all children, add point to list
		}

	// function to add all points that intersect a circle of given x, y, and r to a list
	private void addToCircle(List<E> hits,double cx, double cy, double cr){
		if (Geometry.circleIntersectsRectangle(cx, cy, cr, x1 ,y1, x2, y2)){				// check if the circle intersects rectangle for each node
			if (Geometry.pointInCircle(point.getX(), point.getY(), cx, cy, cr)){			// if it intersects rectangle, check if it intersects the circle
				hits.add(point);															// If so, add to list of that is passed in to function
			}
			if (hasChild(1)){														// Call function on all of the children of node whose rectangle intersects circle
				getChild(1).addToCircle(hits,cx, cy, cr);
			}
			if (hasChild(2)){
				getChild(2).addToCircle(hits,cx, cy, cr);
			}
			if (hasChild(3)){
				getChild(3).addToCircle(hits,cx, cy, cr);
			}
			if (hasChild(4)){
				getChild(4).addToCircle(hits,cx, cy, cr);
			}

		}

	}
}
