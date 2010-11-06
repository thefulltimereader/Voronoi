package voronoi.gameState;

import java.awt.geom.Point2D;
import java.util.List;


import voronoi.network.NeuralNetwork;
import voronoi.util.Pair;

public class Simulator {
  NeuralNetwork p1;
  NeuralNetwork p2;
  GameState gameState;
  int turnOf = 0;
  int turns;
  public Simulator(NeuralNetwork p1, NeuralNetwork p2){
    this.p1 = p1;
    this.p2 = p2;
    this.turns = 7;
    int players = 2;
    this.gameState = new GameState(turns, players);
    
        
  }
  
  public int simulate(){
    
    while(turns>0){
      Pair<Point2D.Double, Double> result;
      if(turnOf == 0){
        result = alphaBeta(gameState, turns, p1, p2, Double.NEGATIVE_INFINITY, 
        Double.POSITIVE_INFINITY, new Point2D.Double(0,0));
      }
      else{ //p2's pov!!
        result = alphaBeta(gameState, turns, p2, p1, Double.NEGATIVE_INFINITY, 
        Double.POSITIVE_INFINITY, new Point2D.Double(0,0));
      }      
      gameState.addPoint(result.getFst());
      turns--;
    }
    
    return (int) gameState.result();

    
  }
  
  private Pair<Point2D.Double, Double> alphaBeta(GameState state, int depth, 
      NeuralNetwork player, NeuralNetwork opponent, double alpha, 
      double beta, Point2D.Double choice){
    if (depth == 0 || state.done()){
      if(state.done()) return new Pair<Point2D.Double, Double>(choice, state.result());
      return new Pair<Point2D.Double, Double>(choice, player.evaluateState(state.getBoard()));
    }
    List<Point2D.Double> points = state.getPossiblePoints();
    Point2D.Double winner = choice;
    for(Point2D.Double pt: points){
      GameState thisState = new GameState(state);
      thisState.addPoint(pt);
      Pair<Point2D.Double, Double> result = alphaBeta(thisState, depth-1, player, opponent, -1*beta, -1*alpha, pt);
      //take the max, set the point associated with max
      if(alpha < result.getSnd()){
        alpha = result.getSnd();
        winner = result.getFst();
      }
      //TODO make sure that beta != -beta here bc of line 49
      if(beta<=alpha) break;
    }
    return new Pair<Point2D.Double, Double>(winner, alpha);
  }
}
