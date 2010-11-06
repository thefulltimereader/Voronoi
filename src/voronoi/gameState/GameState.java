package voronoi.gameState;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import voronoi.util.PolarPoly;
import voronoi.util.VLine;
import voronoi.util.Voronize;


public class GameState {
  List<Integer> turns;
  double scores[];
  public static int BOARDSIZE = 400;
  public static int NUMPLAYERS= 2;
  private int showPoly;
  private boolean singlePoly;
  Voronize vo;
  
  private int numPointsLeft=64;
  /**
   * board has 0 for empty spot, 1 for spot owned by this NN, -1 for spot owned
   * by others, evolvable value -X, and X modifies input array
   */
  double[][] board = new double[8][8];

  public GameState(int turn, int players) {
    turns = new ArrayList<Integer>(2); //one for player, one for opponent
    turns.add(turn); turns.add(turn);
    vo = new Voronize(BOARDSIZE + 2, BOARDSIZE + 2);
    scores = new double[players];
  }

  // copy of
  public GameState(GameState state) {
    this.turns = state.turns;
    this.scores = state.scores;
    this.board = state.board; // TODO does this make a copy of the pointer?
  }

  public boolean done() {
    return turns.get(0)==0 && turns.get(1)==0 || numPointsLeft==0;
  }
  
  public double result() {
    //first player needs to win by 10%
    if (scores[0] > scores[1]*1.1)
      return 1.0;
    else
      return -2.0;
  }


  public double[][] getBoard() {
    return board;
  }

  public List<Point2D.Double> getPossiblePoints() {
    List<Point2D.Double> poss = new ArrayList<Point2D.Double>();
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        if (board[i][j] == 0) {
          poss.add(new Point2D.Double(i, j));
        }
      }
    }
    numPointsLeft = poss.size();
    return poss;
  }
  /****
   * Game state materials below from voronoi2
   */
  
  public void addPoint(Point2D.Double pt, int id) {
    //my code add point to board
    //if id == 0, that's player 1 = 1, else set it to -2, otherwise all 0
    board[(int)pt.x][(int) pt.y] = id==0? 1 : -2;
    if (pt.x >= 0 && pt.x < vo.W && pt.y >= 0 && pt.y < vo.H) {
      List<Point2D.Double> points = vo.getPoints();
      pt.x++;
      pt.y++;
      if(!points.contains(pt)){
        vo.points.add(pt);
        vo.ppolys = new ArrayList<PolarPoly>();
        for (int k = 0; k < vo.points.size(); k++)
            vo.ppolys.add(new PolarPoly());
          for (int k = 0; k < vo.points.size(); k++) {
            VLine[] bis = vo.getBisectors(vo.points.get(k));
            vo.addPoint(k, bis, false);
          }
          
          update();
        
      }
    }
  }

  private void update() {
    List<Point2D.Double> points = vo.getPoints();
    List<PolarPoly> ppolys = vo.getPPolys();
    
    for (int i = 0; i < NUMPLAYERS; i++)
      scores[i] = 0;
    if (showPoly >= ppolys.size())
      showPoly -= ppolys.size();
    else if (showPoly < 0)
      showPoly += ppolys.size();
    if (singlePoly)
      System.out.println("displaying: " + showPoly);

    int start = singlePoly ? showPoly : 0;
    int end = singlePoly ? showPoly + 1 : ppolys.size();
    double area = 0;
    for (int i = start; i < end; i++) {
      System.out.println("Updating poly " + i);

      Point2D.Double pnt = points.get(i);

      System.out.println("Point " + pnt.x + " " + pnt.y);

      PolarPoly pp = (PolarPoly) ppolys.get(i);
      area += pp.area();
      java.awt.Polygon poly = pp.getPolygon();

      java.awt.Rectangle r = poly.getBounds();
      System.out.println("x:" + r.x + " y:" + r.y + " w:" + r.width + " h:"
          + r.height);

      scores[i % NUMPLAYERS] += pp.area();
    }
  }

  public void decrementTurn(int id) {
    Integer turn = turns.get(id);
    turn--; // TODO this is reference and so works right?
  }
}
