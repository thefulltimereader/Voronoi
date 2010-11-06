package voronoi.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import voronoi.gameState.GameSimulator;

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
  final static int startingIndividuals = 15;
  private final Random randGen = new Random();
  int generations = 0;
  /**
   * Data structure to save each nn associating them with their score
   */
  Map<NeuralNetwork, Integer> individuals;

  public static void main(String[] args) {
    EvolutionSimulator world = new EvolutionSimulator();
    world.start();
  }

  public EvolutionSimulator() {
    individuals = new HashMap<NeuralNetwork, Integer>();
  }

  public void start() {
    produce();
    while(generations < 5){
      mutate();
      playGame();
      survivalOftheFittest();
      generations++;
    }
  }

  void produce() {
    // 1. randomly produce 15 individuals
    for (int i = 0; i < startingIndividuals; i++) {
      NeuralNetwork n = new NeuralNetwork();
      individuals.put(n, 0);
    }
  }

  void mutate() {
    Map<NeuralNetwork, Integer> copy = new HashMap<NeuralNetwork, Integer>(
        individuals);
    Set<NeuralNetwork> parents = copy.keySet();
    for (NeuralNetwork parent : parents) {
      NeuralNetwork baby = parent.generateOffspring();
      individuals.put(baby, 0);
    }
  }

  void playGame() {
    List<NeuralNetwork> everyone = new ArrayList<NeuralNetwork>(
        individuals.keySet());
    for (NeuralNetwork player : everyone) {
      // play 5 games
      for (int i = 0; i < 5; i++) {
        int rand = randGen.nextInt(everyone.size());
        if (!everyone.get(rand).equals(player)) {
          NeuralNetwork opponent = everyone.get(rand);
          GameSimulator simulator = new GameSimulator(player, opponent);
          int result = simulator.simulate();
          int score = individuals.get(player);
          // update
          individuals.put(player, score + result);
        }
      }
    }
    System.out.println("generation " + generations + " done");
  }

  private void survivalOftheFittest() {
    //sort
    List<NeuralNetwork> sorted = EvolutionSimulator.getKeysSortedByValue(individuals);
    //cut out weaklings
    sorted.subList(0, 15);
    Map<NeuralNetwork, Integer> newIndividuals = 
      new HashMap<NeuralNetwork,Integer>();
    for(NeuralNetwork nn: sorted){
      newIndividuals.put(nn, individuals.get(nn));
    }
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
    final List<K> keys = new ArrayList<K>(size);
    for (int i = 0; i < size; i++) {
      keys.set(i, list.get(i).getKey());
    }
    return keys;
  }

  private static final class ValueComparator<V extends Comparable<? super V>>
      implements Comparator<Map.Entry<?, V>> {
    public int compare(Map.Entry<?, V> o1, Map.Entry<?, V> o2) {
      return o1.getValue().compareTo(o2.getValue());
    }
  }

}
