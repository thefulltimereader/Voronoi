package voronoi.gameState;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.List;

public class GameState {
  int turns;
  double SCORES[];
  int[][] board = new int[8][8];
  public GameState(int turns, int players){
    this.turns = turns;
    SCORES = new double[players];
  }
  //copy of
  public GameState(GameState state) {
    this.turns = state.turns;
    this.SCORES = state.SCORES;
    this.board = state.board; //TODO does this copy? 
  }
  public boolean done() {
    return turns==0;
  }
  public void scoreFor(int id) {
    // TODO Auto-generated method stub
    
  }
  public double result() {
    // TODO make this for to accomodate multiple but prolly dont need it.
    if(SCORES[0]> SCORES[1]) return 1.0;
    else return -2.0;
  }
  public void addPoint(Point2D.Double move) {
    // TODO Auto-generated method stub
    
  }
  public int[][] getBoard() {
    return board;
  }
  public List<Point2D.Double> getPossiblePoints() {
    List<Point2D.Double> poss = new ArrayList<Point2D.Double>();
    for(int i=0; i<8; i++){
      for(int j=0; j<8; j++){
        if(board[i][j] == -1){
          poss.add(new Point2D.Double(i,j));
        }
      }
    }
    return poss;
  }
  
}
