package voronoi.network;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class EvolutionSimulatorTest {
  EvolutionSimulator world;
  @Before
  public void setUp() throws Exception {
    world = new EvolutionSimulator();
  }

  @Test
  public void testEvolutionSimulator() {
    world.start();
  }
  @Test
  public void testSurvivalOfTheFittest(){
    world.produce();
    world.mutate();
    world.testPlay();
    world.logIndividualScore();
    world.survivalOftheFittest();
  }
  
  @Test
  public void testMoreThan5(){
    world.start();
    
  }


}
