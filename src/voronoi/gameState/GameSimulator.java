package voronoi.gameState;

import java.awt.geom.Point2D;
import java.util.List;


import voronoi.network.NeuralNetwork;
import voronoi.util.Pair;

public class GameSimulator {
  NeuralNetwork p1;
  NeuralNetwork p2;
  GameState gameState;
  int turnOf = 0;//0 for player, 1 for opponent
  int turns;
  final int depth;
  public GameSimulator(NeuralNetwork p1, NeuralNetwork p2){
    this.p1 = p1;
    p1.setId(0);
    this.p2 = p2;
    p1.setId(1);
    this.turns = 7;
    int players = 2;
    this.gameState = new GameState(turns, players);
    this.depth = 4;
  }
  /**
   * Simulates a game between two strategies (NN)
   * @return  from set = { -2, 0, 1} loss, draw, and win respectively
   */
  public int simulate(){
    
    while(turns>0){
      //System.out.println("turn " + turns);
      Pair<Point2D.Double, Double> result;
      NeuralNetwork player = turnOf==0? p1: p2;
      NeuralNetwork opponent = turnOf==0? p2: p1;
      result = alphaBeta(gameState, depth, player, opponent, Double.NEGATIVE_INFINITY, 
        Double.POSITIVE_INFINITY, new Point2D.Double(0,0));
      double value = player.getId();
      if(player.useEvolvableValue(turns)){
        value = player.getEvolvable();
      }
      gameState.addPoint(result.getFst(), player.getId(), value);
      turns--;
      turnOf= turnOf==0 ? 1: 0;
    }
    //System.out.println("Result (1=Win, -2=Lose) "+gameState.result());
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
      //check if we should use the X value
      double value = player.getId();
      if(player.useEvolvableValue(thisState.getMovesLeft())){
        value = player.getEvolvable();
      }
      //add this point to this state
      //System.out.println("Move is " + pt + " by Player" + player.getId());
      thisState.addPoint(pt, player.getId(), value); thisState.decrementTurn(player.getId());
      //recurse
      Pair<Point2D.Double, Double> result = 
        alphaBeta(thisState, depth-1,opponent, player, -1*beta, -1*alpha, pt);
      //take the max, set the point associated with max
      if(alpha < result.getSnd()){
        alpha = result.getSnd();
        winner = result.getFst();
      }      
      if(beta<=alpha) break;
    }
    return new Pair<Point2D.Double, Double>(winner, alpha);
  }
}
