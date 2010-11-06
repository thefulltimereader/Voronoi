package voronoi.gameState;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import voronoi2.VPanel;
import voronoi2.Voronize;

public class GameState {
  int turns;
  double SCORES[];
  public static int BOARDSIZE = 400;
  Voronize vo;
  /**
   * board has 0 for empty spot, 1 for spot owned by this NN, -1 for spot owned
   * by others, evolvable value -X, and X modifies input array
   */
  double[][] board = new double[8][8];

  public GameState(int turns, int players) {
    this.turns = turns;
    vo = new Voronize(BOARDSIZE + 2, BOARDSIZE + 2);
    SCORES = new double[players];
  }

  // copy of
  public GameState(GameState state) {
    this.turns = state.turns;
    this.SCORES = state.SCORES;
    this.board = state.board; // TODO does this copy?
  }

  public boolean done() {
    return turns == 0;
  }

  public void scoreFor(int id) {
    // TODO Auto-generated method stub

  }

  public double result() {
    // TODO make this for to accomodate multiple but prolly dont need it.
    if (SCORES[0] > SCORES[1])
      return 1.0;
    else
      return -2.0;
  }

  public void addPoint(Point2D.Double move) {
    

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
    return poss;
  }

}
