package voronoi.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import voronoi.gameState.GameSimulator;
import voronoi.util.EvolutionLogger;

/**
 * 1. Randomly create 15 (or some constant) artificial neural networks, P_i
 * i=0,...,14 each with random weights w_i(j) j=0,...,number of weights Nw in NN
 * and random evolution factor X it also has a self-adaptive paarm vector
 * sigma_i i=1,...,15 in Reals that controls weight for mutation
 * 
 * 2. Each parent produces offspring, P'_i using: sigma'_i =
 * sigma_i(j)*exp(tau*normpdf(j, 0,1)) w'_i(j) = w_i(j) + sigma'_i(j)*normpd(j,
 * 0,1) --> see NeuralNetwork.java
 * 
 * 3. All parents and offspring compete against randomly picked opponents. each
 * individual players 1 game agains 5 random opponents, always playing the side
 * of the first player. scores are from the set = { -2, 0, 1} loss, draw, and
 * win respectively
 * 
 * 4. Individuals are ranked based on how well they did in all games, the bes5
 * 15 individuals remain, write to file, repeat from step 2
 * 
 * 
 * @author ajk377
 * 
 */
public class EvolutionSimulator {
  private final static Logger LOGGER = 
    Logger.getLogger(EvolutionSimulator.class.getName());
  final static int startingIndividuals = 15;
  private static final int NUMOFGAMES = 1;
  private final Random randGen = new Random();
  int totalNumOfIndividuals = 0;
  int generations = 0;
  /**
   * Data structure to save each nn associating them with their score
   */
  Map<NeuralNetwork, Integer> individuals;

  public static void main(String[] args) {
    EvolutionSimulator world = new EvolutionSimulator();
    try{
      world.start();
    }catch(Exception e){
      LOGGER.log(Level.SEVERE, "Error", e);
    }
  }

  public EvolutionSimulator() {
    individuals = new HashMap<NeuralNetwork, Integer>();
    LOGGER.setLevel(Level.FINEST);
    try {
      EvolutionLogger.setup();
    } catch (IOException e) {
      System.err.println("bad setup");
      e.printStackTrace();
    }
  }

  public void start() {
    produce();
    while(generations < 200){
      mutate();
      playGame();
      survivalOftheFittest();
      LOGGER.fine("****************generation " + generations + " done****************");
      generations++;
    }
  }

  void produce() {
    // 1. randomly produce 15 individuals
    for (int i = 0; i < startingIndividuals; i++) {
      NeuralNetwork n = new NeuralNetwork();
      n.setName(totalNumOfIndividuals);
      totalNumOfIndividuals++;
      individuals.put(n, 0);
    }
  }

  void mutate() {
    //System.out.println("Mutate");
    Map<NeuralNetwork, Integer> copy = new HashMap<NeuralNetwork, Integer>(
        individuals);
    Set<NeuralNetwork> parents = copy.keySet();
    for (NeuralNetwork parent : parents) {
      NeuralNetwork baby = parent.generateOffspring();
      baby.setName(totalNumOfIndividuals);
      totalNumOfIndividuals++;
      individuals.put(baby, 0);
    }
  }
  
  void testPlay(){
    List<NeuralNetwork> ev = new ArrayList<NeuralNetwork>(individuals.keySet());
    int count = 0;
    for(NeuralNetwork play: ev){
      individuals.put(play, count);
      count++;
    }
  }

  void playGame() {
    int numOfGamesPlayed = 0;
    List<NeuralNetwork> everyone = new ArrayList<NeuralNetwork>(
        individuals.keySet());
    for (NeuralNetwork player : everyone) {
      // play 5 games
      if(numOfGamesPlayed%10==0){
        LOGGER.fine("# of Games played so far " + numOfGamesPlayed);
      }

      for (int i = 0; i < NUMOFGAMES; i++) {
        int rand = randGen.nextInt(everyone.size());
        if (!everyone.get(rand).equals(player)) {
          NeuralNetwork opponent = everyone.get(rand);
          GameSimulator simulator = new GameSimulator(player, opponent);
          int result = simulator.simulate();
          int resultForOpponent = result==1? -2: result==-2? 1: 0;
          int cumscore = individuals.get(player);
          int cumOpponentScore = individuals.get(opponent);
          // update
          individuals.put(player, cumscore + result);
          individuals.put(opponent, cumOpponentScore + resultForOpponent);
          numOfGamesPlayed++;
        }
      }
    }
    System.out.println("generation " + generations + " done");
    logIndividualScore();
    
  }

  void logIndividualScore() {
    Set<NeuralNetwork> ind = individuals.keySet();
    for(NeuralNetwork nn : ind){
      LOGGER.info("Nn #"+nn.getName() + "'s score:" + individuals.get(nn));
    }
    
  }

  void survivalOftheFittest() {
    //sort
    List<NeuralNetwork> sorted = EvolutionSimulator.getKeysSortedByValue(individuals);
    //cut out weaklings
    List<NeuralNetwork> cutDown = sorted.subList(0, 15);
    Map<NeuralNetwork, Integer> newIndividuals = 
      new HashMap<NeuralNetwork,Integer>();
    for(NeuralNetwork nn: cutDown){
      newIndividuals.put(nn, individuals.get(nn));
    }
    LOGGER.severe("Best: NN #" + cutDown.get(0).getName()+
        " with weights:"+cutDown.get(0).recordDetails());
    LOGGER.severe("2nd Best: NN #" + cutDown.get(1).getName()+
        " with weights:"+cutDown.get(1).recordDetails());
    individuals = newIndividuals;
  }

  /**
   * Referenced from http://stackoverflow.com/questions/109383/
   * how-to-sort-a-mapkey-value-on-the-values-in-java/3420912#3420912
   * 
   * @param <K>
   * @param <V>
   * @param map
   * @return
   */
  public static <K, V extends Comparable<? super V>> List<K> getKeysSortedByValue(
      Map<K, V> map) {
    final int size = map.size();
    final List<Map.Entry<K, V>> list = new ArrayList<Map.Entry<K, V>>(size);
    list.addAll(map.entrySet());
    final ValueComparator<V> cmp = new ValueComparator<V>();
    Collections.sort(list, cmp);
    List<K> keys = new ArrayList<K>(size); 
    for (int i = 0; i < size; i++) {
      keys.add(i, list.get(i).getKey());
    }
    return keys;
  }

  private static final class ValueComparator<V extends Comparable<? super V>>
      implements Comparator<Map.Entry<?, V>> {
    public int compare(Map.Entry<?, V> o1, Map.Entry<?, V> o2) {
      //flipped o1 and o2!!
      return o2.getValue().compareTo(o1.getValue());
    }
  }

}
