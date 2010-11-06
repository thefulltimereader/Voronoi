package voronoi.util;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.Vector;

/**
 * Wrapper for java.awt.Polygon Assumes that poly is convex, and keeps all
 * points in CCW order
 */

public class PolarPoly {

	private Vector<Point2D.Double> points;

	public PolarPoly() {
		points = new Vector<Point2D.Double>();
	}

	public void addPoint(Point2D.Double p) {
		addPoint(p.x, p.y);
	}

	public void addPoint(double x, double y) {
		points.addElement(new Point2D.Double(x, y));
		sort(points);
	}

	public int getSize() {
		return points.size();
	}

	public Point2D.Double getPoint(int i) {
		return (Point2D.Double) points.elementAt(i);
	}

	public void removePoint(int i) {
		points.removeElementAt(i);
	}

	public Polygon getPolygon() {
		Polygon p = new Polygon();
		for (int i = 0; i < points.size(); i++) {
			Point2D.Double pd = (Point2D.Double) points.elementAt(i);
			p.addPoint((int) Math.round(pd.x), (int) Math.round(pd.y));
		}
		return p;
	}

	public double area() {
		double a = 0;
		for (int i = 2; i < points.size(); i++)
			a += triarea((Point2D.Double) points.elementAt(0),
					(Point2D.Double) points.elementAt(i - 1),
					(Point2D.Double) points.elementAt(i));
		return a;
	}

	public String toString() {
		String s = "(" + points.size() + "/" + area() + "):";
		for (int i = 0; i < points.size(); i++)
			s += " " + ((Point2D.Double) points.elementAt(i));
		return s;
	}

	private double triarea(Point2D.Double A, Point2D.Double B, Point2D.Double C) {
		double ax = B.x - A.x;
		double ay = B.y - A.y;
		double bx = C.x - A.x;
		double by = C.y - A.y;
		return ((Math.abs(ax * by - ay * bx)) / 2);
	}

	private void sort(Vector<Point2D.Double> v) {
		int N = v.size();
		Vector<Double> ths = new Vector<Double>();
		Point2D.Double[] pts = new Point2D.Double[N];
		Point2D.Double center = getCenter();
		for (int i = 0; i < N; i++)
			ths.addElement(new Double(getTheta((Point2D.Double) v.elementAt(i),
					center)));
		for (int j = 0; j < N; j++) {
			double lt = 2 * Math.PI;
			int ind = 0;
			for (int i = 0; i < v.size(); i++) {
				double t = ((Double) ths.elementAt(i)).doubleValue();
				if (t < lt) {
					lt = t;
					ind = i;
				}
			}
			pts[j] = (Point2D.Double) v.elementAt(ind);
			v.removeElementAt(ind);
			ths.removeElementAt(ind);
		}
		for (int i = 0; i < N; i++)
			v.addElement(pts[i]);
	}

	private Point2D.Double getCenter() {
		double xsum = 0.0, ysum = 0.0;
		for (int i = 0; i < points.size(); i++) {
			xsum += ((Point2D.Double) points.elementAt(i)).x;
			ysum += ((Point2D.Double) points.elementAt(i)).y;
		}
		return new Point2D.Double(xsum / points.size(), ysum / points.size());
	}

	private double getTheta(Point2D.Double p, Point2D.Double center) {
		double x = p.x - center.x;
		double y = p.y - center.y;
		double r = Math.sqrt(x * x + y * y);
		double t = Math.acos(x / r);
		return y < 0 ? 2 * Math.PI - t : t;
	}
}