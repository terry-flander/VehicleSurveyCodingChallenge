package au.com.terryflander;

import java.util.ArrayList;

public class VehicleSurveyMain {

  public static void main(String[] args) {
    if (args.length < 2) {
      System.err.println("VehicleSummary usage: <load-file> <save-directory> [average]");
      System.exit(1);
    } else {
      String loadFile = args[0];
      String saveDirectory = args[1];
      boolean average = args.length == 3;
      VehicleSurvey vs = new VehicleSurvey();
      vs.init(loadFile);
      if (vs.counterEventsTotal() > 0) {
        vs.loadSummary("AM_PM", average);
        vs.saveResults(saveDirectory, "stats_am_pm.csv");
        vs.loadSummary("HOUR", average);
        vs.saveResults(saveDirectory, "stats_1_hour.csv");
        vs.loadSummary("HALF_HOUR", average);
        vs.saveResults(saveDirectory, "stats_half_hour.csv");
        vs.loadSummary("TWENTY_MINUTES", average);
        vs.saveResults(saveDirectory, "stats_20_minutes.csv");
        vs.loadSummary("FIFTEEN_MINUTES", average);
        vs.saveResults(saveDirectory, "stats_15_minutes.csv");
      }
    }
  }

}

