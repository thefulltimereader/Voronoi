package voronoi.network;

import org.junit.Before;
import org.junit.Test;


public class NeuralNetworkTest {
  final int SIZE = 64; 
  NeuralNetwork nn;
  @Before
  public void setUp(){
    nn = new NeuralNetwork();
    
  }
  @Test
  public void testBuildInput(){
    double[][] b = new double[8][8];    
    nn.buildInput(b);
  }
  @Test
  public void testGetCoordinate(){
    for (int i = 0; i < SIZE; i++) {
      double position = nn.getCoordinate(i, SIZE);
      // if(position)
    }
  }
  @Test
  public void testLayers(){
    testBuildInput();
    nn.firstLayer();
    nn.secondLayer();
    nn.thirdLayer();
  }
}
