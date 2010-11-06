package voronoi.util;

import java.util.Random;

public final class CalcUtil {

  public static double sigmoid(double x) {
    return (1 / (1 + Math.pow(Math.E, (-1 * x))));
  }

  /**
   * Generate pseudo-random floating point values, with an approximately
   * Gaussian (normal) distribution.
   * 
   * Many physical measurements have an approximately Gaussian distribution;
   * this provides a way of simulating such values.
   * http://www.javapractices.com/topic/TopicAction.do?Id=62
   */
  private final static Random fRandom = new Random();

  public static double randomGaussian(double aMean, double aVariance) {
    return aMean + fRandom.nextGaussian() * aVariance;
  }

}
