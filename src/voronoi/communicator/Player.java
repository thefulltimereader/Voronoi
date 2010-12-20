package voronoi.communicator;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

import voronoi.gameState.GameState;
import voronoi.network.NeuralNetwork;
import voronoi.util.Pair;

public class Player {
  int name;
  int turns;
  final int DEPTH = 3;
  private NeuralNetwork strategy;
  private NeuralNetwork strategyOpponent;
  private GameState gameState;
  
  private final static String FILENAME = "winner";
  public Player(){
    strategy = retrieveBest();
    strategyOpponent = retrieveBest();
    strategy.setId(0);
    strategyOpponent.setId(1);
  }
  
  public static void main(String[] args){
    Player p = new Player();
    
  }
  
  private NeuralNetwork retrieveBest(){
    File f = new File(FILENAME);
    double[] w1 = new double[64];
    double[] a1 = new double[64];
    double[] w2 = new double[4];
    double[] a2 = new double[4];
    double[] w3 = new double[4];
    double[] a3 = new double[4];
    double evolvable = 0;       
    try {
      Scanner scanner = new Scanner(f);
      System.out.println("reading input..");
      while(scanner.hasNext()){
        String tok = scanner.next();
        //should.. won't check file input syntax..if(tok.equals("weight1")){
          for(int i=0; i<64;i++){
            w1[i] = scanner.nextDouble();
            System.out.println(w1[i]);
          }
          System.out.println("w2!");
          scanner.next();//"weight2"
          for(int i=0; i<4;i++){
            w2[i] = scanner.nextDouble();
            System.out.println(w2[i]);
          }
          scanner.next();//"weight3"
          for(int i=0; i<4;i++){
            w3[i] = scanner.nextDouble();
            System.out.println(w3[i]);
          }
          scanner.next();//"sigma1"
          for(int i=0; i<64;i++){
            a1[i] = scanner.nextDouble();
          }
          scanner.next();//"sigma2"
          for(int i=0; i<4;i++){
            a2[i] = scanner.nextDouble();
          }
          scanner.next();//"sigma3"
          for(int i=0; i<4;i++){
            a3[i] = scanner.nextDouble();
          }
        //}
      }
      
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    
    return new NeuralNetwork(w1,a1,w2,a2,w3,a3,evolvable);
  }
  public String play() {
    System.out.println("computing...");
    Pair<Point2D.Double, Double> result = alphaBeta(gameState, DEPTH, 
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
    return new Point2D.Double(Math.round(Math.floor(pt.x/50)), Math.round(Math.floor(pt.y/50)));
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
    try{
    gameState.addPoint(scalePtDown(pt), 1, -2);
    }catch(Exception e){
      e.printStackTrace();
    }
    
  }
  
  
  private Pair<Point2D.Double, Double> alphaBeta(GameState state, int depth, 
      NeuralNetwork player, NeuralNetwork opponent, double alpha, 
      double beta, Point2D.Double choice){
    //    if(depth%DEPTH == 0) System.out.print("...");
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
