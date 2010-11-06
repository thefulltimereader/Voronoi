package voronoi.gameState;

import org.junit.Before;
import org.junit.Test;

import voronoi.network.NeuralNetwork;

public class GameSimulatorTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testSimulate() {
    NeuralNetwork p1 = new NeuralNetwork();
    NeuralNetwork p2 = new NeuralNetwork();
    GameSimulator game = new GameSimulator(p1, p2);
    game.simulate();
  }

}
