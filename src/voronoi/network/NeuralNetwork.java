package voronoi.network;

import java.util.ArrayList;
import java.util.List;

public class NeuralNetwork {
  private final int SIZE = 64; 
  private double[] input;
  //first weight
  private double[] inputWeights;
  //hidden spacial layer
  private double[] firstCoordWeights;
  //2nd hidden spacial layer
  private double[] secondCoordWeights;
  
  
  public NeuralNetwork(){
    input = new double[SIZE];
    inputWeights = new double[SIZE];
    firstCoordWeights= new double[SIZE/4];
    secondCoordWeights= new double[SIZE/16];
  }
  
  public void initialize(){
    for(int i=0, n=inputWeights.length; i<n;i++){
      inputWeights[i] = 0.3;
    }
    for(int i=0, n=firstCoordWeights.length; i<n;i++){
     firstCoordWeights[i] = 0.5;
    }
    for(int i=0, n=secondCoordWeights.length; i<n;i++){
      secondCoordWeights[i] = 0.8;
    }
    
  }
  
  /**
   * applies feed forward nerural network to input
   * @param board
   * @return
   */
  public double evaluateState(int[][] board) {
    buildInput(board);
    //first layer
    for(int i=0, n=inputWeights.length; i<n;i++){
      input[i] = input[i]*inputWeights[i];
    }
    for(int i=0, n=firstCoordWeights.length; i<n;i++){
      input[i] = input[i]*firstCoordWeights[i];
    }
    for(int i=0, n=secondCoordWeights.length; i<n;i++){
      input[i] = input[i]*secondCoordWeights[i];
    }
    double result = 0;
    for(int i=0; i< SIZE; i++){
      result+= input[i];
    }
    return result;
  }
  /**
   * Encode board 8*8 borad into 64*1 vector for convenience.
   * Count from left to right, bump down and go W->E again
   *  = how Java indexes 2D arrays
   * @param board has 0 for empty spot, 1 for spot owned by this NN, 
   * -1 for spot owned by others, evolvable value -X, and X
   */
  private void buildInput(int[][] board){
    for(int i=0;i<board[0].length;i++){
      for(int j=0;j<board[1].length;i++){
        input[i+j]=(double) board[i][j];
      }
    }
  }
}
