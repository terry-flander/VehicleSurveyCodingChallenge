package au.com.terryflander;

import java.util.HashMap;

/**
 *  The purpose of this class is to identify valid reporting periods based on Name.
 **/

class ReportPeriods {

  private final HashMap<String, Integer> countPeriods;

  public ReportPeriods() {
    this.countPeriods = new HashMap<>();
    this.countPeriods.put("AM_PM", 720);
    this.countPeriods.put("HOUR", 60);
    this.countPeriods.put("HALF_HOUR", 30);
    this.countPeriods.put("TWENTY_MINUTES", 20);
    this.countPeriods.put("FIFTEEN_MINUTES", 15);
  }

  public boolean containsKey(String periodId) {
    return this.countPeriods.containsKey((periodId));
  }

  public int getMinutesPerPeriod(String periodId) {
    int result = 0;
    if (containsKey(periodId)) {
      result = this.countPeriods.get(periodId);
    }
    return result;
  }

}
