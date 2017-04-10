package au.com.terryflander;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

public class VehicleSurvey {

  private static String loadFile;
  private static ArrayList<CounterEvent> counterEvents;
  private static ArrayList<Vehicle> vehicles;
  private static int numDays;
  private static HashMap<String,CountPeriod> countPeriods;
  private static ArrayList<CountSummary> countSummary;

  public static void main( String[] args ) {
    if (args.length != 2) {
      System.err.println("VehicleSummary usage: <input-file> <output-directory>");
      System.exit(1);
    } else {
      VehicleSurvey vs = new VehicleSurvey();
      System.out.println("arg0='"+args[0]+"' arg1='"+args[1]+"'");
      String loadFile = args[0];
      String saveDir = args[1];
      vs.init(loadFile);
      if (counterEvents!=null && counterEvents.size() > 0) {
        vs.loadSummary("AM_PM", false);
        vs.saveResults(saveDir,"stats_am_pm.csv");
        vs.loadSummary("HOUR", false);
        vs.saveResults(saveDir, "stats_1_hour.csv");
        vs.loadSummary("HALF_HOUR", false);
        vs.saveResults(saveDir,"stats_half_hour.csv");
        vs.loadSummary("TWENTY_MINUTES", false);
        vs.saveResults(saveDir, "stats_20_minutes.csv");
        vs.loadSummary("FIFTEEN_MINUTES", false);
        vs.saveResults(saveDir, "stats_15_minutes.csv");
      }
    }

  }

  public void init(String inFile) {
    initCountPeriods();
    loadEventData(inFile);
    createVehiclesFromEvents();
  }

  public void loadEventData(String inFile) {

    if (inFile.equals("test")) {
      loadFile = "./Vehicle Survey Coding Challenge sample data.txt";
    } else {
      loadFile = inFile;
    }

    Path path = FileSystems.getDefault().getPath(".", loadFile);

    if (Files.exists(path)) {
      ArrayList<String> records = new ArrayList<String>();
      try {
        records = (ArrayList<String>) Files.readAllLines(path, Charset.defaultCharset() );
        counterEvents = new ArrayList<CounterEvent>();
        int dayNumber = 0;
        long lastEventTime = 999999999;
        for (String record: records) {
          long eventTime = getEventTime(record.substring(1));
          if (eventTime < lastEventTime) {
            dayNumber++;
          }
          counterEvents.add(new CounterEvent(record.substring(0,1), eventTime, dayNumber));
          lastEventTime = eventTime;
        }
        numDays = dayNumber;
      } catch (IOException e) {
        System.out.println("Could not read load file: " + loadFile);
      }
    } else {
      File f = new File("./");
      System.out.println("Load file not found: " + loadFile + " looking from " + f.getAbsolutePath());
    }
  }

  public int counterEventsTotal() {
    return counterEvents.size();
  }

  public int vehiclesTotal() {
    return vehicles!=null ? vehicles.size() : 0;
  }

  public int northCarCount() {
    int result = 0;
    for (Vehicle vehicle: vehicles) {
      if (vehicle.direction.equals("B")) {
        result++;
      }
    }
    return result;
  }

  public int southCarCount() {
    return vehiclesTotal() - northCarCount();
  }

  public int[] countPerPeriod(String direction, String period) {
    return countPerPeriod(direction, period, "*");
  }

  public int[] countPerPeriod(String direction, String period, String selectDays) {
    ArrayList<PeriodData> periodData = getPeriodData(direction, period, selectDays);
    int result[] = new int[periodData.size()];
    for (int i =  0; i<periodData.size(); i++) {
      result[i] = periodData.get(i).getCount();
    }
    return result;
  }

  public ArrayList<PeriodData> getPeriodData(String direction, String period, String selectDays) {
    ArrayList<PeriodData> result = new ArrayList<PeriodData>();
    if (countPeriods.containsKey(period)) {
      CountPeriod thisPeriod = countPeriods.get(period);
      for (Vehicle vehicle : vehicles) {
        if (vehicle.direction.equals(direction)) {
          if (includeInCount(selectDays, vehicle.dayNumber)) {
            int bucket = getCountBucket(thisPeriod.getMinutesPerPeriod(), vehicle.hour, vehicle.minute);
            while (result.size() < bucket + 1) {
              result.add(result.size(), new PeriodData());
            }
            result.get(bucket).addCount();
            result.get(bucket).addSpeed(vehicle.speed);
            result.get(bucket).addSeparation(vehicle.separation);
          }
        }
      }
      double sd = getStandardDeviation(result);
      for (PeriodData pd: result) {
        // > 2 SD == Peak
        if (pd.getCount() > sd * 2) {
          pd.setPeak();
        }
      }
    } else {
      System.out.println("Invalid period: " + period);
    }
    return result;
  }

  public void loadSummary(String period, boolean average) {
    if (countPeriods.containsKey(period)) {
      CountPeriod thisPeriod = countPeriods.get(period);
      countSummary = new ArrayList<CountSummary>(thisPeriod.getArraySize() * (average?1:numDays));
      for (int day = 0;day < numDays; day++) {
        ArrayList<PeriodData> periodData = getPeriodData("A", period, String.valueOf(day + 1));
        int offset = (day * periodData.size());
        for (int i = 0; i<periodData.size(); i++) {
          getSummary(offset, i, day, thisPeriod).setSouthCount(periodData.get(i).getCount());
          getSummary(offset, i, day, thisPeriod).addSouthSpeed(periodData.get(i).getSpeed());
          getSummary(offset, i, day, thisPeriod).addSouthSeparation(periodData.get(i).getSeparation());
          getSummary(offset, i, day, thisPeriod).setSouthPeak(periodData.get(i).getPeak());
        }

        periodData = getPeriodData("B", period, String.valueOf(day + 1));
        offset = (day * periodData.size());
        for (int i = 0; i<periodData.size(); i++) {
          getSummary(offset, i, day, thisPeriod).setNorthCount(periodData.get(i).getCount());
          getSummary(offset, i, day, thisPeriod).addNorthSpeed(periodData.get(i).getSpeed());
          getSummary(offset, i, day, thisPeriod).addNorthSeparation(periodData.get(i).getSeparation());
          getSummary(offset, i, day, thisPeriod).setNorthPeak(periodData.get(i).getPeak());
        }
      }

      // If averaging, only one total but must be divided by numDays
      if (average) {
        for (int i = 0; i<countSummary.size(); i++) {
          CountSummary cs = countSummary.get(i);
          cs.setNorthCount(cs.getNorthCount() / numDays);
          cs.setSouthCount(cs.getSouthCount() / numDays);
          cs.setNorthSpeed(cs.getNorthSpeed() / numDays);
          cs.setSouthSpeed(cs.getSouthSpeed() / numDays);
        }
      }
    } else {
      System.out.println("Invalid period: " + period);
    }
  }

  private CountSummary getSummary(int offset, int i, int day, CountPeriod thisPeriod) {
    if (countSummary.size() < offset + i + 1) {
      countSummary.add(offset + i, new CountSummary(day, calculateHour(thisPeriod.minutesPerPeriod, i), calculateMinute(thisPeriod.minutesPerPeriod, i)));
    }
    return countSummary.get(offset + i);
  }

  private boolean includeInCount(String selectDays, int dayNumber) {
    return ((selectDays.equals("*") || selectDays.indexOf(String.valueOf(dayNumber)) != -1));
  }

  private int getCountBucket(int minutesPerPeriod, int hour, int minute) {
    return ((hour * 60) + minute) / minutesPerPeriod;
  }

  private int calculateHour(int minutesPerPeriod, int offset) {
    return minutesPerPeriod * offset / 60;
  }

  private int calculateMinute(int minutesPerPeriod, int offset) {
    return minutesPerPeriod * offset % 60;
  }

  private void createVehiclesFromEvents() {
    if (counterEvents!=null) {
      int i = counterEvents.size() - 1;
      vehicles = new ArrayList<Vehicle>();
      long lastTime[] = new long[2];
      while (0 <= i) {
        String direction = counterEvents.get(i).direction;
        Vehicle v = new Vehicle(direction);
        int offset = direction.equals("B") ? 3 : 1;
        for (int j = i - offset; j <= i; j++) {
          v.addEvent(counterEvents.get(j));
        }
        i = i - (offset + 1);
        v.setSpeed();
        v.setDistance(lastTime[direction.equals("B") ? 0 : 1]);
        lastTime[direction.equals("B") ? 0 : 1] = v.eventTime;
        vehicles.add(v);
      }
    }
  }

  private void initCountPeriods() {
    countPeriods = new HashMap<String,CountPeriod>();
    countPeriods.put("AM_PM",new CountPeriod(2, 720));
    countPeriods.put("HOUR",new CountPeriod(24, 60));
    countPeriods.put("HALF_HOUR",new CountPeriod(48, 30));
    countPeriods.put("TWENTY_MINUTES",new CountPeriod(72, 20));
    countPeriods.put("FIFTEEN_MINUTES",new CountPeriod(96, 15));
  }

  public int dateCount() {
    return numDays;
  }

  public ArrayList<CountSummary> getCountSummary() {
    return countSummary;
  }

  private long getEventTime(String value) {
    long result = 0;
    try {
      result = Long.valueOf(value);
    } catch (Exception e) {
      System.out.println("Could not convert time to number: " + value);
    }
    return result;
  }

  public void saveResults(String dirName, String fileName) {
    // Output Results
    PrintWriter pw = null;
    try {
      pw = saveData(dirName, fileName);
      pw.println("Day,Hour,Minute,North Count,South Count,North Speed (km/hr),South Speed (km/hr),North Separation (M),South Separation (M),North Peak,South Peak");
      for (int i=0;i<countSummary.size();i++) {
        CountSummary fs = countSummary.get(i);
        pw.println(fs.getDayNumber() + ","
            + fs.getHour() + ","
            + fs.getMinute() + ","
            + fs.getNorthCount() + ","
            + fs.getSouthCount() + ","
            + round(fs.getNorthSpeed(),0) + ","
            + round(fs.getSouthSpeed(), 0) + ","
            + round(fs.getNorthSeparation(), 0) + ","
            + round(fs.getSouthSeparation(), 0) + ","
            + (fs.getNorthPeak()?"Peak":"") + ","
            + (fs.getSouthPeak()?"Peak":"")
        );
      }
      pw.close();
    } catch (Exception e) {
      System.out.println("saveError: "+e.getMessage());
    }

  }

  private static double round(double value, int places) {
    if (places < 0) throw new IllegalArgumentException();

    BigDecimal bd = new BigDecimal(value);
    bd = bd.setScale(places, RoundingMode.HALF_DOWN);
    return bd.doubleValue();
  }

  private static PrintWriter saveData (String dirName, String fileName) {
    if (fileName.equals("test")) {
      return writeTest();
    } else {
      return writeFile(dirName, fileName);
    }
  }

  private static PrintWriter writeTest() {
    PrintWriter result = new PrintWriter(System.out);
    return result;
  }

  private static PrintWriter writeFile(String dirName, String fileName) {
    try {
      File  f = new File(dirName);
      f.mkdirs();
    } catch (Exception e) {
      System.out.println("writeFile: Could not create directories. " + e.getMessage());
    }
    PrintWriter result = null;
    Path path = FileSystems.getDefault().getPath(dirName + "/" + fileName);
    try {
      result = new PrintWriter(Files.newBufferedWriter(path, java.nio.charset.StandardCharsets.UTF_8));
    } catch (IOException e) {
      System.out.println("writeFile: Could not " + e.getMessage());
    }
    return result;
  }

  private double getStandardDeviation(ArrayList<PeriodData> data) {
    double[] count = new double[data.size()];
    for (int i=0; i<data.size();i++) {
      count[i] = data.get(i).count;
    }
    return new Statistics(count).getStdDev();
  }

  public class Statistics {
    double[] data;
    int size;

    public Statistics(double[] data) {
      this.data = data;
      size = data.length;
    }

    double getMean() {
      double sum = 0.0;
      for(double a : data)
        sum += a;
      return sum/size;
    }

    double getVariance() {
      double mean = getMean();
      double temp = 0;
      for(double a :data)
        temp += (a-mean)*(a-mean);
      return temp/size;
    }

    double getStdDev() {
      return Math.sqrt(getVariance());
    }

    public double median() {
      Arrays.sort(data);

      if (data.length % 2 == 0) {
        return (data[(data.length / 2) - 1] + data[data.length / 2]) / 2.0;
      }
      return data[data.length / 2];
    }
  }

  private class CounterEvent {
    private String direction;
    private long eventTime;
    private int dayNumber;
    private int hour_of_day;
    private int minute;

    public CounterEvent(String direction, long eventTime, int dayNumber) {
      this.direction = direction;
      this.dayNumber = dayNumber;
      this.eventTime = eventTime;
    }
  }

  private class Vehicle {
    private String direction;
    private ArrayList<CounterEvent> events;
    private long eventTime;
    private int dayNumber;
    private int hour;
    private int minute;
    private double speed;
    private double separation;

    public Vehicle(String direction) {
      this.direction = direction;
      this.events = new ArrayList<CounterEvent>();
    }

    public void addEvent(CounterEvent event) {
      this.events.add(event);
      if (this.eventTime == 0) {
        this.eventTime = event.eventTime;
        this.dayNumber = event.dayNumber;
        try {
          Calendar c = Calendar.getInstance();
          c.setTimeInMillis(this.eventTime);
          this.hour = c.get(Calendar.HOUR_OF_DAY);
          this.minute = c.get(Calendar.MINUTE);
        } catch (Exception e) {
          System.out.println("Could not convert time: " + this.eventTime);
        }
      }
    }

    public void setSpeed() {
      long elapsedTime = 0;
      for (CounterEvent event: events) {
        if (event.direction.equals("A")) {
          if (elapsedTime == 0) {
            elapsedTime = event.eventTime;
          } else {
            elapsedTime = event.eventTime - elapsedTime;
          }
        }
      }
      this.speed = (2.5 * 3600) / elapsedTime;
    }

    public void setDistance(long nextTime) {
      long elapsed = 0;
      if (nextTime < this.eventTime) {
        elapsed = nextTime - (this.eventTime - (24 * 60 * 60 * 1000)); // Subtract a day
      } else {
        elapsed = nextTime - this.eventTime;
      }
      this.separation = this.speed * elapsed / 3600;
    }
  }

  public class PeriodData {
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

    private int dayNumber;
    private int hour;
    private int minute;
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

    public void setSouthCount(int southCount) {
      this.southCount = southCount;
    }

    public double getNorthSpeed() {
      return this.northSpeed / (this.northCount!=0?this.northCount:1);
    }

    public double getSouthSpeed() {
      return this.southSpeed / (this.southCount!=0?this.southCount:1);
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
      return this.northSeparation / (this.northCount!=0?this.northCount:1);
    }

    public double getSouthSeparation() {
      return this.southSeparation / (this.southCount!=0?this.southCount:1);
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

  private class CountPeriod {

    private int arraySize;
    private int minutesPerPeriod;

    private CountPeriod(int arraySize, int minutesPerPeriod) {
      this.arraySize = arraySize;
      this.minutesPerPeriod = minutesPerPeriod;
    }

    public int getArraySize() {
      return this.arraySize;
    }

    public int getMinutesPerPeriod() {
      return this.minutesPerPeriod;
    }
  }
};

