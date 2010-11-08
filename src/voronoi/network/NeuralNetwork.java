package voronoi.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import voronoi.util.CalcUtil;

public class NeuralNetwork {
  private final int SIZE = 64;
  private final int WIDTH = 8;
  // first input neurons
  private double[] input;
  private int id;
  private int name;
  
  private boolean DEBUG = false;
  /**
   * to keep track of indecies of 16 neurons 2D array 
   */
  private List<List<Integer>> firstLayer;
  private List<List<Integer>> secondLayer;
  private List<List<Integer>> thirdLayer;
  
  /**
   * 1st hidden spacial layer of 4 neurons has 16 neurons per neuron
   */
  private double[] firstCoordWeights;
  /**
   * 2nd hidden spacial layer has 4 neurons per neuron
   */
  private double[] secondCoordWeights;
  private double[] thirdCoordWeights;
  /**
   * Self-adaptive parameter per NN (strategy)
   * initially set to 0.05
   */
  private double[] selfAdaptiveParamOne;
  private double[] selfAdaptiveParamTwo;
  private double[] selfAdaptiveParamThree;
  /**
   * Evolveable parameter for mutation
   * Once in a while NN will return this value to the board instead of the usu
   * {-1, 0 1} so there will be {-1, 0, 1, -X, X}
   */
  private double evolvable = 2;
    
  private final Random randGen = new Random();
  private final int numWeights = SIZE*4*4;
  private final double tau = 1/Math.sqrt(2*Math.sqrt(numWeights));  

  public NeuralNetwork() {
    input = new double[SIZE];
    // 4 becuase of the 4 coordinate
    firstCoordWeights = new double[SIZE];
    selfAdaptiveParamOne = new double[SIZE];
    firstLayer = new ArrayList<List<Integer>>();
    secondCoordWeights = new double[4];
    selfAdaptiveParamTwo = new double[4];
    secondLayer = new ArrayList<List<Integer>>();
    thirdCoordWeights = new double[4];
    selfAdaptiveParamThree = new double[4];
    thirdLayer = new ArrayList<List<Integer>>();
    initialize();
  }
  /**
   * Weights are generated sampling from a uniform distribution over [-0.2, 0.2]
   * with X initially set to 2.0
   */
  void initialize() {
    for (int i = 0, n = firstCoordWeights.length; i < n; i++) {
      firstCoordWeights[i] = (randGen.nextDouble()*.4)-0.2;
    }
    for(int i=0; i<4;i++){
      firstLayer.add(new ArrayList<Integer>());
    }
    for (int i = 0, n = secondCoordWeights.length; i < n; i++) {
      secondCoordWeights[i] = (randGen.nextDouble()*.4)-0.2;
    }
    for(int i=0; i<4;i++){
      secondLayer.add(new ArrayList<Integer>());
    }
    for (int i = 0, n = thirdCoordWeights.length; i < n; i++) {
      thirdCoordWeights[i] = (randGen.nextDouble()*.4)-0.2;
    }
    for(int i=0; i<4;i++){
      thirdLayer.add(new ArrayList<Integer>());
    }
    //initially set for 0.05
    for(int i=0, n=selfAdaptiveParamOne.length; i<n;i++){
      selfAdaptiveParamOne[i] = 0.05;
    }
    for(int i=0, n=selfAdaptiveParamTwo.length; i<n;i++){
      selfAdaptiveParamTwo[i] = 0.05;
      selfAdaptiveParamThree[i] = 0.05;
    }
    

  }

  /**
   * applies feed forward nerural network to input
   * 
   * @param board
   * @return
   */
  public double evaluateState(double[][] board) {
    clearLayers();
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
  //index layer array must be cleared
  private void clearLayers() {
    for(int i=0; i<4;i++){
      firstLayer.get(i).clear();
    }
    for(int i=0; i<4;i++){
      secondLayer.get(i).clear();
    }
    for(int i=0; i<4;i++){
      thirdLayer.get(i).clear();
    }
  }
  void thirdLayer() {
    //for each clustered coordinate 0=QUAD1, 1=QUAD2, ..
    for(int i=0; i< SIZE/16; i++){
      List<Integer> sixteens = secondLayer.get(i);
      for(Integer ind : sixteens){
        int position = getCoordinate(ind%4, 2);
        input[ind] = CalcUtil.sigmoid(input[ind]*thirdCoordWeights[position-1]);
        thirdLayer.get(position-1).add(ind);
      }
    }
    if(DEBUG){
    System.out.println();
    for(int i=0; i<thirdLayer.size(); i++){
      List<Integer> l = thirdLayer.get(i);
      System.out.print("\nFor last Quad/neuron " +(i+1) + " indicies are:");
      for(Integer s: l){
        System.out.print("\t"+s);
      }
    }
    }
  }

  void secondLayer() {
    //for each clustered coordinate 0=QUAD1, 1=QUAD2, ..
    for(int i=0; i< SIZE/16; i++){
      List<Integer> sixteens = firstLayer.get(i);
      for(Integer ind : sixteens){
        int position = getCoordinate(ind%16, 4);
        input[ind] = CalcUtil.sigmoid(input[ind]*secondCoordWeights[position-1]);
        secondLayer.get(position-1).add(ind);
      }
    }
    if(DEBUG){
    System.out.println();
    for(int i=0; i<secondLayer.size(); i++){
      List<Integer> l = secondLayer.get(i);
      System.out.print("\nFor 2nd Quad/neuron " +(i+1) + " indicies are:");
      for(Integer s: l){
        System.out.print("\t"+s);
      }
    }
    }
  }

  void firstLayer() {
    // get the coordinate
    for (int i = 0; i < SIZE; i++) {
      int position = getCoordinate(i, WIDTH);
      //apply appropriate weight
      input[i] = CalcUtil.sigmoid(input[i]*firstCoordWeights[i]);
      //update index
      firstLayer.get(position-1).add(i);        
    }
    if(DEBUG){
    for(int i=0; i<firstLayer.size(); i++){
      List<Integer> l = firstLayer.get(i);
      System.out.print("\nFor Quad/neuron " +(i+1) + " indicies are:");
      for(Integer s: l){
        System.out.print("\t"+s);
      }
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
        if(DEBUG) System.out.print((i + j + total) + ",");
        input[i + j + total] = (double) board[i][j];
      }
      //System.out.println();
    }

  }

  public int getId() {
    return id;
  }
  
  public void setId(int id){
    this.id = id;
  }

  /**
   * Each parent produces offspring, P'_i using: sigma'_i =
   * sigma_i(j)*exp(tau*normpdf(j, 0,1)) w'_i(j) = w_i(j) +
   * sigma'_i(j)*normpd(j, 0,1) j = 1,..., N_w Where: N_w = number of weights in
   * X' = X + delta
   * this NN tau = 1/sqrt(2*sqrt(N_w)) - computed in constructor N_j(0,1) is a
   * standard Gaussian random variable resampled for every NN=j
   * Delta = uniformly at random from {-.1, 0, .1}
   * 
   * Here P_i is just this NN so forget about the indicies
   * 
   * @return Offspring of this NN
   */
  public NeuralNetwork generateOffspring() {
    double term1, term2, term3;
    double[] childAdaptiveParamOne = new double[SIZE];
    double[] childAdaptiveParamTwo = new double[4];
    double[] childAdaptiveParamThree = new double[4];
    for(int i=0, n=selfAdaptiveParamOne.length; i<n; i++){
      term1 = tau*CalcUtil.randomGaussian(0, 1);
      childAdaptiveParamOne[i] = selfAdaptiveParamOne[i]*Math.pow(Math.E, term1);
    }
    for(int i=0, n=selfAdaptiveParamTwo.length; i<n; i++){
      term2=tau*CalcUtil.randomGaussian(0, 1);
      childAdaptiveParamTwo[i] = selfAdaptiveParamTwo[i]*Math.pow(Math.E, term2);
    }
    for(int i=0, n=selfAdaptiveParamThree.length; i<n; i++){
      term3=tau*CalcUtil.randomGaussian(0, 1);
      childAdaptiveParamThree[i] = selfAdaptiveParamThree[i]*Math.pow(Math.E, term3);
    }
    double[] firstWeights = new double[SIZE];
    for(int i=0, n=firstCoordWeights.length; i<n; i++){
      firstWeights[i] = firstCoordWeights[i] +
      childAdaptiveParamOne[i]*CalcUtil.randomGaussian(0, 1);
    }
    double[] secondWeights = new double[4];
    for(int i=0, n=secondCoordWeights.length; i<n; i++){
      secondWeights[i] = secondCoordWeights[i] +
      childAdaptiveParamTwo[i]*CalcUtil.randomGaussian(0, 1);
    }
    double[] thirdWeights = new double[4];
    for(int i=0, n=thirdCoordWeights.length; i<n; i++){
      thirdWeights[i] = thirdCoordWeights[i] +
      childAdaptiveParamThree[i]*CalcUtil.randomGaussian(0, 1);
    }
    
    int r = randGen.nextInt(3);
    int delta = r == 0? 0: r==1? 1 : -1; 
    double newEvolvable = evolvable + delta;
    //keep it in range [1,3]
    newEvolvable = newEvolvable > 3? 3 : newEvolvable < 1 ? 1: newEvolvable;
    //produce
    return new NeuralNetwork(firstWeights, childAdaptiveParamOne, 
        secondWeights, childAdaptiveParamTwo, thirdWeights, 
        childAdaptiveParamThree, evolvable);
  }
  /**
   * Constructor to produce a neural network with given parameters
   */
   public NeuralNetwork(double[] w1, double[] a1, double[] w2, 
       double[] a2, double[] w3, double[] a3, double evolvable){
    input = new double[SIZE];
    // 4 becuase of the 4 coordinate
    this.firstCoordWeights = w1;
    this.selfAdaptiveParamOne = a1;
    firstLayer = new ArrayList<List<Integer>>();
    this.secondCoordWeights = w2;
    this.selfAdaptiveParamTwo = a2;
    secondLayer = new ArrayList<List<Integer>>();
    this.thirdCoordWeights = w3;
    this.selfAdaptiveParamThree = a3;
    thirdLayer = new ArrayList<List<Integer>>();
    this.evolvable = evolvable;
    for(int i=0; i<4;i++) firstLayer.add(new ArrayList<Integer>());
    
    for(int i=0; i<4;i++) secondLayer.add(new ArrayList<Integer>());
    
    for(int i=0; i<4;i++) thirdLayer.add(new ArrayList<Integer>());
    
  }
  
  public double getEvolvable(){
    return evolvable;
  }
  /**
   * Higher probability to use X if moves left is low
   * @param movesLeft
   * @return
   */
  public boolean useEvolvableValue(int movesLeft) {
    return CalcUtil.randomGaussian(0, Math.pow(1/movesLeft, 2)) < -0.05; 
  }
  public String recordDetails() {
    StringBuffer sb = new StringBuffer();
    sb.append("weight1 ");
    for (int i = 0, n = firstCoordWeights.length; i < n; i++) {
      sb.append(firstCoordWeights[i]).append(" ");
    }
    sb.append("\nweight2 ");
    for (int i = 0, n = secondCoordWeights.length; i < n; i++) {
      sb.append(secondCoordWeights[i]).append(" ");
    }
    sb.append("\nweight3 ");
    for (int i = 0, n = thirdCoordWeights.length; i < n; i++) {
      sb.append(thirdCoordWeights[i]).append(" ");
    }
    sb.append("\nsigma1 ");
    for(int i=0, n=selfAdaptiveParamOne.length; i<n;i++){
      sb.append(selfAdaptiveParamOne[i]).append(" ");
    }
    sb.append("\nsigma2 ");
    for(int i=0, n=selfAdaptiveParamTwo.length; i<n;i++){
      sb.append(selfAdaptiveParamTwo[i]).append(" ");
    }
    sb.append("\nsigma3 ");
    for(int i=0, n=selfAdaptiveParamThree.length; i<n;i++){
      sb.append(selfAdaptiveParamThree[i]).append(" ");
    }
    System.out.println(sb.toString());
    return sb.toString();
  }
  /**
   * For logging purposes
   * @return
   */
  public int getName() {
    return name;
  }
  public void setName(int name){
    this.name = name ;
  }


}
