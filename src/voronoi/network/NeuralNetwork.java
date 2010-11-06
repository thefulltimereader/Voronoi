package voronoi.network;

import java.util.ArrayList;
import java.util.List;

public class NeuralNetwork {
  private final int SIZE = 64;
  private final int WIDTH = 8;
  // first input neurons
  private double[] input;

  /**
   * 1st hidden spacial layer of 4 neurons has 16 neurons per neuron
   */
  private double[] firstCoordWeights;
  /**
   * to keep track of indecies of 16 neurons 2D array 
   */
  private List<List<Integer>> firstLayer;
  private List<List<Integer>> secondLayer;
  private List<List<Integer>> thirdLayer;
  /**
   * 2nd hidden spacial layer has 4 neurons per neuron
   */
  private double[] secondCoordWeights;
  private double[] thirdCoordWeights;

  public NeuralNetwork() {
    input = new double[SIZE];
    // 4 becuase of the 4 coordinate
    firstCoordWeights = new double[4];
    firstLayer = new ArrayList<List<Integer>>();
    secondCoordWeights = new double[4];
    secondLayer = new ArrayList<List<Integer>>();
    thirdCoordWeights = new double[4];
    thirdLayer = new ArrayList<List<Integer>>();
    initialize();
  }

  void initialize() {
    for (int i = 0, n = firstCoordWeights.length; i < n; i++) {
      firstCoordWeights[i] = 0.5;
    }
    for(int i=0; i<4;i++){
      firstLayer.add(new ArrayList<Integer>());
    }
    for (int i = 0, n = secondCoordWeights.length; i < n; i++) {
      secondCoordWeights[i] = 0.8;
    }
    for(int i=0; i<4;i++){
      secondLayer.add(new ArrayList<Integer>());
    }
    for(int i=0; i<4;i++){
      thirdLayer.add(new ArrayList<Integer>());
    }
    

  }

  /**
   * applies feed forward nerural network to input
   * 
   * @param board
   * @return
   */
  public double evaluateState(double[][] board) {
    buildInput(board); //8x8->64*1
    firstLayer();//64->16
    secondLayer();//16-4
    thirdLayer();//4-2
    double result = 0;
    for (int i = 0; i < SIZE; i++) {
      result += input[i];
    }
    return result;
  }

  void thirdLayer() {
    //for each clustered coordinate 0=QUAD1, 1=QUAD2, ..
    for(int i=0; i< SIZE/16; i++){
      List<Integer> sixteens = secondLayer.get(i);
      for(Integer ind : sixteens){
        int position = getCoordinate(ind%4, 2);
        input[ind] = input[ind]*thirdCoordWeights[position-1];
        thirdLayer.get(position-1).add(ind);
      }
    }
    System.out.println();
    for(int i=0; i<thirdLayer.size(); i++){
      List<Integer> l = thirdLayer.get(i);
      System.out.print("\nFor last Quad/neuron " +(i+1) + " indicies are:");
      for(Integer s: l){
        System.out.print("\t"+s);
      }
    }
  }

  void secondLayer() {
    //for each clustered coordinate 0=QUAD1, 1=QUAD2, ..
    for(int i=0; i< SIZE/16; i++){
      List<Integer> sixteens = firstLayer.get(i);
      for(Integer ind : sixteens){
        int position = getCoordinate(ind%16, 4);
        input[ind] = input[ind]*secondCoordWeights[position-1];
        secondLayer.get(position-1).add(ind);
      }
    }
    System.out.println();
    for(int i=0; i<secondLayer.size(); i++){
      List<Integer> l = secondLayer.get(i);
      System.out.print("\nFor 2nd Quad/neuron " +(i+1) + " indicies are:");
      for(Integer s: l){
        System.out.print("\t"+s);
      }
    }
  }

  void firstLayer() {
    // get the coordinate
    for (int i = 0; i < SIZE; i++) {
      int position = getCoordinate(i, WIDTH);
      //apply appropriate weight
      input[i] = input[i]*firstCoordWeights[position-1];
      //update index
      firstLayer.get(position-1).add(i);        
    }
    for(int i=0; i<firstLayer.size(); i++){
      List<Integer> l = firstLayer.get(i);
      System.out.print("\nFor Quad/neuron " +(i+1) + " indicies are:");
      for(Integer s: l){
        System.out.print("\t"+s);
      }
    }
  }

  int getCoordinate(int ind, int width) {
    int halfWay = width / 2;
    // System.out.println("\nindex is:" + ind);
    // 1st or second
    if (ind < halfWay * width) {
      if (ind % width < halfWay) {
        return 2;
      }
      else {
        return 3;
      }
    }
    // 3rd or 4th
    else {
      if (ind % width < halfWay) {/* System.out.print("\t3rd"); */
        return 1;
      }
      else {/* System.out.print("\t4th"); */
        return 4;
      }
    }
  }

  /**
   * Linear Indexing Encode board 8*8 borad into 64*1 vector for convenience.
   * Count from left to right, bump down and go W->E again = how Java indexes 2D
   * arrays So we have: 0-7 8-15 16-23 ... 56-63 (x,y)-> if(x==0) 7-y if(y!=0)
   * x*y+7 else x+7.
   * 
   * @param board
   *          has 0 for empty spot, 1 for spot owned by this NN, -1 for spot
   *          owned by others, evolvable value -X, and X modifies input array
   *          i.e. double[8][8] ->double[64] 0,1,2,3,4,5,6,7,
   *          8,9,10,11,12,13,14,15, 16,17,18,19,20,21,22,23,
   *          24,25,26,27,28,29,30,31, 32,33,34,35,36,37,38,39,
   *          40,41,42,43,44,45,46,47, 48,49,50,51,52,53,54,55,
   *          56,57,58,59,60,61,62,63,
   */
  void buildInput(double[][] board) {

    for (int i = 0; i < board[0].length; i++) {
      int total = 7 * i;
      for (int j = 0; j < board[1].length; j++) {
        System.out.print((i + j + total) + ",");
        input[i + j + total] = (double) board[i][j];
      }
      System.out.println();
    }

  }
}
