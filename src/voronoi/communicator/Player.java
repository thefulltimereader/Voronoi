package voronoi.communicator;

import java.awt.geom.Point2D;
import java.util.List;

import voronoi.gameState.GameState;
import voronoi.network.NeuralNetwork;
import voronoi.util.Pair;

public class Player {
  int name;
  int turns;
  final int depth = 5;
  private NeuralNetwork strategy;
  private NeuralNetwork strategyOpponent;
  private GameState gameState;
  public Player(){
    strategy = retrieveBest();
    strategyOpponent = retrieveBest();
    strategy.setId(0);
    strategyOpponent.setId(1);
  }
  
  private NeuralNetwork retrieveBest(){
    //TODO: read from file
    return new NeuralNetwork();
  }
  public String play() {
    System.out.println("computing...");
    Pair<Point2D.Double, Double> result = alphaBeta(gameState, depth, 
        strategy, strategyOpponent, Double.NEGATIVE_INFINITY, 
        Double.POSITIVE_INFINITY, new Point2D.Double(0,0));
    gameState.addPoint(result.getFst(), 0, 1);
    turns--;
    //rescale
    
    Point2D.Double scaledPt = rescalePoint(result.getFst());
    System.out.println("Result "+ scaledPt);
    StringBuffer reply = new StringBuffer();
    reply.append(Math.round(Math.round(scaledPt.x)))
    .append(" ").append((Math.round(Math.round(scaledPt.y))));
    return reply.toString();
  }
  
  
  private Point2D.Double rescalePoint(Point2D.Double pt){
    return new Point2D.Double(pt.x*50, pt.y*50);
  }
  private Point2D.Double scalePtDown(Point2D.Double pt) {
    return new Point2D.Double(Math.round(Math.round(pt.x/50)), Math.round(Math.round(pt.y/50)));
  }


  public void init(String fromServer) {
    String[] split = fromServer.split(" ");
    //int bSize = Integer.valueOf(split[0]); 
    turns = Integer.valueOf(split[1]); 
    int numPlayers = Integer.valueOf(split[2]); 
    name= Integer.valueOf(split[3]);
    this.gameState = new GameState(turns, numPlayers);
    
    
  }
  /**
   * Updates other player's input to the internals
   * @param fromServer
   */
  public void addStates(String fromServer) {
    String[] split = fromServer.split(" ");
    int x =Integer.valueOf(split[0]); 
    int y = Integer.valueOf(split[1]); 
    int playerName = Integer.valueOf(split[2]); 
    Point2D.Double pt = new Point2D.Double(x, y);
    
    gameState.addPoint(scalePtDown(pt), playerName, -2);
    
  }
  
  
  private Pair<Point2D.Double, Double> alphaBeta(GameState state, int depth, 
      NeuralNetwork player, NeuralNetwork opponent, double alpha, 
      double beta, Point2D.Double choice){
   // if(depth%5 == 0) System.out.print("...");
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
