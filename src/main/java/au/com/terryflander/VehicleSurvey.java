package au.com.terryflander;

import java.util.ArrayList;

/**
 The purpose of this class is to manage all processes and support TDD
 **/
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

  public void loadSummary(String period, boolean average) {
    if (countPeriods.containsKey(period)) {
      ReportSummary summary = new ReportSummary(countPeriods.getMinutesPerPeriod(period), average,
          this.vehicles);
      this.countSummary = summary.getCountSummary();
    }
  }

  public void saveResults(String dirName, String fileName) {
    SurveyReportWriter.saveResults(this.countSummary, dirName, fileName);
  }

  /*
   The following methods are provided only for the purpose of supporting the TDD methodology
   followed for this assignment. In order they provide access to the incremental components
   created. All TDD tests reside in VehicleSurveyTest.java.
   */

  // Input file counter events loaded without error
  public int counterEventsTotal() {
    return this.counterEvents.getEvents().size();
  }

  // Counter events could be parsed into vehicles
  private int vehiclesTotal() {
    return this.vehicles.getVehicles().size();
  }

  // The total number of vehicles travelling 'North' (B) is correct
  public int northCarCount() {
    int result = 0;
    for (Vehicles.Vehicle vehicle: this.vehicles.getVehicles()) {
      if (vehicle.getDirection().equals("B")) {
        result++;
      }
    }
    return result;
  }

  // The total number of vehicles travelling 'South' (A) is correct
  public int southCarCount() {
    return vehiclesTotal() - northCarCount();
  }

  // The correct number of days read is correct
  public int dateCount() {
    return this.vehicles.getNumDays();
  }

  // The reporting periods are correctly set up to receive counting by period
  public int[] countPerPeriod(String direction, @SuppressWarnings("SameParameterValue") String period) {
    return countPerPeriod(direction, period, "1");
  }

  // The counts for specific periods and direction are correct
  public int[] countPerPeriod(String direction, String period, String selectDays) {
    loadSummary(period, false);
    int bucketsPerDay =
        this.countSummary.size() / (selectDays.equals("*") ? 1 : this.vehicles.getNumDays());
    int[] result = new int[bucketsPerDay];
    for (int i = 0; i < this.countSummary.size(); i++) {
      int j = i % bucketsPerDay;
      result[j] +=
          direction.equals("B") ? this.countSummary.get(i).getNorthCount()
              : this.countSummary.get(i).getSouthCount();
    }
    return result;
  }

  // Provide direct access to count summary for further tests.
  public ArrayList<ReportSummary.CountSummary> getCountSummary() {
    return this.countSummary;
  }

}

