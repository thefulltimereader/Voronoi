package voronoi.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class CalcUtilTest {

  @Test
  public void testRandomGaussian() {
    double tau =  1/Math.sqrt(2*Math.sqrt(64*4*4));
    System.out.println(tau);
    for(int i=0; i<30; i++){
      System.out.println(Math.pow(Math.E,tau*CalcUtil.randomGaussian(0, 1)));
    }
  }

}
