package au.com.terryflander;

import java.util.ArrayList;

class VehicleSurvey {

  private EventDataSource counterEvents;
  private final ReportPeriods countPeriods = new ReportPeriods();
  private Vehicles vehicles;
  private ArrayList<ReportSummary.CountSummary> countSummary;

  public void init(String inFile) {
    try {
      this.counterEvents = new EventDataSource(inFile);
      this.vehicles = new Vehicles(this.counterEvents);
    } catch (Exception e) {
      System.out.println("Unable to initialize classes: " + e.getMessage());
    }
  }

  public int counterEventsTotal() {
    return this.counterEvents.getEvents().size();
  }

  private int vehiclesTotal() {
    return this.vehicles.getVehicles().size();
  }

  public int northCarCount() {
    int result = 0;
    for (Vehicles.Vehicle vehicle: this.vehicles.getVehicles()) {
      if (vehicle.getDirection().equals("B")) {
        result++;
      }
    }
    return result;
  }

  public int southCarCount() {
    return vehiclesTotal() - northCarCount();
  }

  public int dateCount() {
    return this.vehicles.getNumDays();
  }

  public int[] countPerPeriod(String direction, @SuppressWarnings("SameParameterValue") String period) {
    return countPerPeriod(direction, period, "1");
  }

  public int[] countPerPeriod(String direction, String period, String selectDays) {
    loadSummary(period, false);
    int bucketsPerDay =
        countSummary.size() / (selectDays.equals("*") ? 1 : this.vehicles.getNumDays());
    int[] result = new int[bucketsPerDay];
    for (int i = 0; i < countSummary.size(); i++) {
      int j = i % bucketsPerDay;
      result[j] +=
          direction.equals("B") ? countSummary.get(i).getNorthCount()
              : countSummary.get(i).getSouthCount();
    }
    return result;
  }

  public void loadSummary(String period, boolean average) {
    if (countPeriods.containsKey(period)) {
      ReportSummary summary = new ReportSummary(countPeriods.getMinutesPerPeriod(period), average,
          this.vehicles);
      countSummary = summary.getCountSummary();
    }
  }

  public ArrayList<ReportSummary.CountSummary> getCountSummary() {
    return countSummary;
  }

  public void saveResults(String dirName, String fileName) {
    SurveyReportWriter.saveResults(this.countSummary, dirName, fileName);
  }

}

