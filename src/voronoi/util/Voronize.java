package voronoi.util;

import java.awt.geom.Point2D;

import java.util.ArrayList;
import java.util.List;

/**
 * Maintains a voronoi diagram as a list of points and their associated polys.
 * Supports testing and adding points plus some utility operations. See alg.html
 * for algorithm specifics.
 * 
 * Main method constructs a default interface, and can initialize from a file of
 * points representing the moves of two players.
 */
// ============================================================================

public class Voronize {
  final double FUDGE = .0000001;

  public final int W;
  public final int H;
  public final Point2D.Double UL;
  public final Point2D.Double UR;
  public final Point2D.Double LL;
  public final Point2D.Double LR;
  public List<Point2D.Double> points;
  public List<PolarPoly> ppolys;

  public static int BoardSize = 400;

  public Voronize(int w, int h) {
    W = w;
    H = h;
    UL = new Point2D.Double(0, 0);
    UR = new Point2D.Double(W - 1, 0);
    LL = new Point2D.Double(0, H - 1);
    LR = new Point2D.Double(W - 1, H - 1);
    clear();
  }
  public Voronize(Voronize v){
    W = v.W;
    H = v.H;
    UL = v.UL;
    UR = v.UR;
    LL = v.LL;
    LR = v.LR;
    points = new ArrayList<Point2D.Double>(v.points);
    ppolys = new ArrayList<PolarPoly>(v.ppolys);
  }

  public static void main(String[] args) {
   // Voronize v = new Voronize(BoardSize + 2, BoardSize + 2);
  }

  public void clear() {
    points = new ArrayList<Point2D.Double>();
    ppolys = new ArrayList<PolarPoly>();
  }

  public List<Point2D.Double> getPoints() {
    return points;
  }

  public List<PolarPoly> getPPolys() {
    return ppolys;
  }

  int count;

  public void set(List<Point2D.Double> p) {
    count = 0;
    points = p;
    ppolys = new ArrayList<PolarPoly>();
    for (int k = 0; k < p.size(); k++)
      ppolys.add(new PolarPoly());
    for (int k = 0; k < p.size(); k++) {
      VLine[] bis = getBisectors(k);
      addPoint(k, bis, false);
    }
  }

  public PolarPoly testPoint(Point2D.Double p) {
    PolarPoly pp = new PolarPoly();
    VLine[] bis = getBisectors(p);
    getPoly(-1, pp, p, 0, bis, true);
    return pp;
  }

  public void addPoint(Point2D.Double p) {
    points.add(p);
    ppolys.add(new PolarPoly());
    VLine[] bis = getBisectors(points.size() - 1);
    addPoint(points.size() - 1, bis, true);
  }

  public void addPoint(int k, VLine[] bis, boolean incremental) {
    if (incremental)
      for (int i = 0; i < bis.length - 4; i++)
        prune(i, bis[i]);
    int start = incremental ? 0 : k + 1;
    getPoly(k, (PolarPoly) ppolys.get(k), (Point2D.Double) points.get(k),
        start, bis, false);
  }

  private void getPoly(int k, PolarPoly pp, Point2D.Double newpt, int start,
      VLine[] bis, boolean testing) {
    for (int i = start; i < bis.length - 1; i++) {
      for (int j = i + 1; j < bis.length; j++) {
        Point2D.Double intpt = bis[i].getIntersection(bis[j]);
        if (intpt != null) {
          VLine testint = new VLine(newpt.x, newpt.y, intpt.x, intpt.y);
          // System.out.println(" int: " + intpt.x + "," + intpt.y);
          if (isGood(k, i, j, testint, intpt, bis, newpt)) {
            pp.addPoint(intpt);
            if (!testing) {
              count++;
              if (i < bis.length - 4)
                ((PolarPoly) ppolys.get(i)).addPoint(intpt);
              if (j < bis.length - 4)
                ((PolarPoly) ppolys.get(j)).addPoint(intpt);
            }
          }
        }
      }
    }
  }

  private boolean isGood(int k, int i, int j, VLine testint,
      Point2D.Double intpt, VLine[] bis, Point2D.Double ctr) {
    boolean OK = true;
    int m = 0;
    do {
      if ((m != i && m != j && m != k) || m >= bis.length - 4) {
        Point2D.Double xp = testint.getIntersection(bis[m]);
        if (xp != null
            && Math.min(ctr.x, intpt.x) <= xp.x
            && xp.x <= Math.max(ctr.x, intpt.x)
            && Math.min(ctr.y, intpt.y) <= xp.y
            && xp.y <= Math.max(ctr.y, intpt.y)
            && (Math.abs(ctr.x - xp.x) > FUDGE || Math.abs(ctr.y - xp.y) > FUDGE)
            && (Math.abs(intpt.x - xp.x) > FUDGE || Math.abs(intpt.y - xp.y) > FUDGE)) {
          OK = false;
        }
      }
      m++;
    } while (OK && m < bis.length);
    return OK;
  }

  private void prune(int index, VLine line) {
    PolarPoly pp = (PolarPoly) ppolys.get(index);
    Point2D.Double pt = (Point2D.Double) points.get(index);
    double goodside = line.eval(pt);
    int N = pp.getSize();
    for (int i = N - 1; i >= 0; i--) {
      Point2D.Double p = pp.getPoint(i);
      double e = line.eval(p);
      if (Math.abs(e) < .01)
        e = 0;
      if (e * goodside < 0)
        pp.removePoint(i);
    }
  }

  // find perpendicular bisectors of points[index]
  // add screen edge bisectors
  private VLine[] getBisectors(int ind) {
    VLine[] lines = new VLine[points.size() + 3];
    Point2D.Double p = (Point2D.Double) points.get(ind);
    int cnt = 0;
    for (int i = 0; i < points.size(); i++)
      if (i != ind)
        lines[cnt++] = getPerpBisect(p, (Point2D.Double) points.get(i));
    lines[cnt++] = new VLine(UL, UR);
    lines[cnt++] = new VLine(UR, LR);
    lines[cnt++] = new VLine(LR, LL);
    lines[cnt++] = new VLine(LL, UL);
    return lines;
  }

  public VLine[] getBisectors(Point2D.Double p) {
    VLine[] lines = new VLine[points.size() + 4];
    int i;
    for (i = 0; i < points.size(); i++)
      lines[i] = getPerpBisect(p, (Point2D.Double) points.get(i));
    lines[i++] = new VLine(UL, UR);
    lines[i++] = new VLine(UR, LR);
    lines[i++] = new VLine(LR, LL);
    lines[i++] = new VLine(LL, UL);
    return lines;
  }

  // get the perpendicular bisector of the line between p1 and p2
  private VLine getPerpBisect(Point2D.Double p1, Point2D.Double p2) {
    Point2D.Double center = new Point2D.Double((p1.x + p2.x) / 2.0,
        (p1.y + p2.y) / 2.0);
    Point2D.Double newp1 = new Point2D.Double(-(p1.y - center.y) + center.x,
        p1.x - center.x + center.y);
    Point2D.Double newp2 = new Point2D.Double(-(p2.y - center.y) + center.x,
        p2.x - center.x + center.y);
    return new VLine(newp1, newp2);
  }

}