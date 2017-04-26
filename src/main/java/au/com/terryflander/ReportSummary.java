package au.com.terryflander;

import java.util.ArrayList;

/**
 *  The purpose of this class is to process the Vehicles into the ArrayList CountSummary for the
 *  required report period minutes. Either returns all days data or if average=true combines
 *  into single day with values averaged over the total number of days.
 **/

class ReportSummary {
  private final ArrayList<CountSummary> countSummary;

  public ReportSummary(int minutesPerPeriod, boolean average, Vehicles vehicles) {
    int numDays = vehicles.getNumDays();
    this.countSummary =
        new ArrayList<>(getArraySize(minutesPerPeriod) * (average ? 1 : numDays));
    for (int day = 0;day < numDays; day++) {
      ArrayList<PeriodData> periodData =
          getPeriodData("A", minutesPerPeriod, String.valueOf(day + 1), vehicles);
      int offset = (day * periodData.size());
      for (int i = 0; i < periodData.size(); i++) {
        getSummary(offset, i, day, average,
            minutesPerPeriod).addSouthCount(periodData.get(i).getCount());
        getSummary(offset, i, day, average,
            minutesPerPeriod).addSouthSpeed(periodData.get(i).getSpeed());
        getSummary(offset, i, day, average,
            minutesPerPeriod).addSouthSeparation(periodData.get(i).getSeparation());
        getSummary(offset, i, day, average,
            minutesPerPeriod).setSouthPeak(periodData.get(i).getPeak());
      }

      periodData = getPeriodData("B", minutesPerPeriod, String.valueOf(day + 1), vehicles);
      offset = (day * periodData.size());
      for (int i = 0; i < periodData.size(); i++) {
        getSummary(offset, i, day, average,
            minutesPerPeriod).addNorthCount(periodData.get(i).getCount());
        getSummary(offset, i, day, average,
            minutesPerPeriod).addNorthSpeed(periodData.get(i).getSpeed());
        getSummary(offset, i, day, average,
            minutesPerPeriod).addNorthSeparation(periodData.get(i).getSeparation());
        getSummary(offset, i, day, average,
            minutesPerPeriod).setNorthPeak(periodData.get(i).getPeak());
      }
    }

    // If averaging, only one total but must be divided by numDays
    if (average) {
      for (CountSummary cs: countSummary) {
        cs.setNorthCount(cs.getNorthCount() / numDays);
        cs.setSouthCount(cs.getSouthCount() / numDays);
        cs.setNorthSpeed(cs.getNorthSpeedRaw() / numDays);
        cs.setSouthSpeed(cs.getSouthSpeedRaw() / numDays);
        cs.setNorthSeparation(cs.getNorthSeparationRaw() / numDays);
        cs.setSouthSeparation(cs.getSouthSeparationRaw() / numDays);
      }
    }
  }

  public ArrayList<CountSummary> getCountSummary() {
    return this.countSummary;
  }

  private int getArraySize(int minutesPerPeriod) {
    return (24 * 60 * 60) / minutesPerPeriod;
  }

  private ArrayList<PeriodData> getPeriodData(
      String direction,
      int minutesPerPeriod, String selectDays, Vehicles vehicles) {
    ArrayList<PeriodData> result = new ArrayList<>();
    for (Vehicles.Vehicle vehicle : vehicles.getVehicles()) {
      if (vehicle.getDirection().equals(direction)) {
        if (includeInCount(selectDays, vehicle.getDayNumber())) {
          int bucket = getCountBucket(minutesPerPeriod, vehicle.getHour(), vehicle.getMinute());
          while (result.size() < bucket + 1) {
            result.add(result.size(), new PeriodData());
          }
          result.get(bucket).addCount();
          result.get(bucket).addSpeed(vehicle.getSpeed());
          result.get(bucket).addSeparation(vehicle.getSeparation());
        }
      }
      double sd = getStandardDeviation(result);
      for (PeriodData pd : result) {
        // > 2 SD == Peak
        if (pd.getCount() > sd * 2) {
          pd.setPeak();
        }
      }
    }
    return result;
  }

  private CountSummary getSummary(
      int offset,
      int i,
      int day,
      boolean average, int minutesPerPeriod) {
    if (this.countSummary.size() < (average ? 0 : offset) + i + 1) {
      this.countSummary.add(offset + i,
          new CountSummary(
              average ? 0 : day,
              calculateHour(minutesPerPeriod, i),
              calculateMinute(minutesPerPeriod, i)));
    }
    return this.countSummary.get((average ? 0 : offset) + i);
  }

  private int calculateHour(int minutesPerPeriod, int offset) {
    return minutesPerPeriod * offset / 60;
  }

  private int calculateMinute(int minutesPerPeriod, int offset) {
    return minutesPerPeriod * offset % 60;
  }

  private boolean includeInCount(String selectDays, int dayNumber) {
    return ((selectDays.equals("*") || selectDays.contains(String.valueOf(dayNumber))));
  }

  private int getCountBucket(int minutesPerPeriod, int hour, int minute) {
    return ((hour * 60) + minute) / minutesPerPeriod;
  }

  private double getStandardDeviation(ArrayList<PeriodData> data) {
    double[] count = new double[data.size()];
    for (int i = 0; i < data.size();i++) {
      count[i] = data.get(i).count;
    }
    return new Statistics(count).getStdDev();
  }

  private class PeriodData {
    private int count;
    private double speed;
    private double separation;
    private boolean peak;

    public PeriodData() {
    }

    public void addCount() {
      this.count++;
    }

    public void addSpeed(double speed) {
      this.speed += speed;
    }

    public void addSeparation(double separation) {
      this.separation += separation;
    }

    public int getCount() {
      return this.count;
    }

    public double getSpeed() {
      return this.speed;
    }

    public double getSeparation() {
      return this.separation;
    }

    public void setPeak() {
      this.peak = true;
    }

    public boolean getPeak() {
      return this.peak;
    }

  }

  public class CountSummary {

    private final int dayNumber;
    private final int hour;
    private final int minute;
    private int northCount;
    private int southCount;
    private double northSpeed;
    private double southSpeed;
    private double northSeparation;
    private double southSeparation;
    private boolean northPeak;
    private boolean southPeak;

    public CountSummary(int dayNumber, int hour, int minute) {
      this.dayNumber = dayNumber;
      this.hour = hour;
      this.minute = minute;
    }

    public int getDayNumber() {
      return this.dayNumber;
    }

    public int getHour() {
      return this.hour;
    }

    public int getMinute() {
      return this.minute;
    }

    public int getNorthCount() {
      return this.northCount;
    }

    public int getSouthCount() {
      return this.southCount;
    }

    public void setNorthCount(int northCount) {
      this.northCount = northCount;
    }

    public void addNorthCount(int northCount) {
      this.northCount += northCount;
    }

    public void setSouthCount(int southCount) {
      this.southCount = southCount;
    }

    public void addSouthCount(int southCount) {
      this.southCount += southCount;
    }

    public double getNorthSpeed() {
      return this.northSpeed / (this.northCount != 0 ? this.northCount : 1);
    }

    public double getSouthSpeed() {
      return this.southSpeed / (this.southCount != 0 ? this.southCount : 1);
    }

    public double getNorthSpeedRaw() {
      return this.northSpeed;
    }

    public double getSouthSpeedRaw() {
      return this.southSpeed;
    }

    public void addNorthSpeed(double northSpeed) {
      this.northSpeed += northSpeed;
    }

    public void addSouthSpeed(double southSpeed) {
      this.southSpeed += southSpeed;
    }

    public void setNorthSpeed(double northSpeed) {
      this.northSpeed = northSpeed;
    }

    public void setSouthSpeed(double southSpeed) {
      this.southSpeed = southSpeed;
    }

    public double getNorthSeparation() {
      return this.northSeparation / (this.northCount != 0 ? this.northCount : 1);
    }

    public double getSouthSeparation() {
      return this.southSeparation / (this.southCount != 0 ? this.southCount : 1);
    }

    public double getNorthSeparationRaw() {
      return this.northSeparation;
    }

    public double getSouthSeparationRaw() {
      return this.southSeparation;
    }

    public void addNorthSeparation(double northSeparation) {
      this.northSeparation += northSeparation;
    }

    public void addSouthSeparation(double southSeparation) {
      this.southSeparation += southSeparation;
    }

    public void setNorthSeparation(double northSeparation) {
      this.northSeparation = northSeparation;
    }

    public void setSouthSeparation(double southSeparation) {
      this.southSeparation = southSeparation;
    }

    public void setNorthPeak(boolean peak) {
      this.northPeak = peak;
    }

    public boolean getNorthPeak() {
      return this.northPeak;
    }

    public void setSouthPeak(boolean peak) {
      this.southPeak = peak;
    }

    public boolean getSouthPeak() {
      return this.southPeak;
    }

  }

}
