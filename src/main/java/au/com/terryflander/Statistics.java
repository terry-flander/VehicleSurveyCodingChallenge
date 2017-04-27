package au.com.terryflander;

import java.util.Arrays;

/**
 The purpose of this class is to provide the calculation of Standard Deviation for use
 in the 'Peak Period' requirement.
 **/

class Statistics {
  private final double[] data;
  private final int size;

  public Statistics(double[] data) {
    this.data = data;
    size = data.length;
  }

  private double getMean() {
    double sum = 0.0;
    for (double a : data) {
      sum += a;
    }
    return sum / size;
  }

  private double getVariance() {
    double mean = getMean();
    double temp = 0;
    for (double a : data) {
      temp += (a - mean) * (a - mean);
    }
    return temp / size;
  }

  public double getStdDev() {
    return Math.sqrt(getVariance());
  }

  @SuppressWarnings("unused")
  private double median() {
    Arrays.sort(data);

    if (data.length % 2 == 0) {
      return (data[(data.length / 2) - 1] + data[data.length / 2]) / 2.0;
    }
    return data[data.length / 2];
  }
}