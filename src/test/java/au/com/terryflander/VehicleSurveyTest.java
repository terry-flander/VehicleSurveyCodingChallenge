package au.com.terryflander;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import java.util.ArrayList;

/**
 * Unit test for simple App.
 */
public class VehicleSurveyTest {

  VehicleSurvey vs;

  @Before
  public void initialize() {
    vs = new VehicleSurvey();
    vs.init("test");
  }

  @Test
  public final void whenReadTestFileCountIsCorrect() {
    Assert.assertEquals(67296, vs.counterEventsTotal());
  }

  @Test
  public final void whenNorthCarCountIsCorrect() {
    Assert.assertEquals(11276, vs.northCarCount());
  }

  @Test
  public final void whenSouthCarCountIsCorrect() {
    Assert.assertEquals(11096, vs.southCarCount());
  }

  @Test
  public final void whenNorthCarAmCountIsCorrect() {
    Assert.assertEquals(4594, vs.countPerPeriod("B", "AM_PM")[0]);
  }

  @Test
  public final void whenSouthCarAmCountIsCorrect() {
    Assert.assertEquals(6468, vs.countPerPeriod("A", "AM_PM")[0]);
  }

  @Test
  public final void whenNorthCarTotalIsCorrect() {
    Assert.assertEquals(11276, vs.countPerPeriod("B", "AM_PM")[0] + vs.countPerPeriod("B", "AM_PM")[1]);
  }

  @Test
  public final void whenNorthCarTotalIsCorrectAllDays() {
    Assert.assertEquals(11276, vs.countPerPeriod("B", "AM_PM", "*")[0]
        + vs.countPerPeriod("B", "AM_PM", "*")[1]);
  }

  @Test
  public final void whenDateCountIsCorrect() {
    Assert.assertEquals(5, vs.dateCount());
  }

  @Test
  public final void whenSouthCarAmDay1CountIsCorrect() {
    Assert.assertEquals(1264, vs.countPerPeriod("A", "AM_PM", "1")[0]);
  }

  @Test
  public final void whenVehicleCountHoursAreCorrect() {
    Assert.assertEquals(24, vs.countPerPeriod("A", "HOUR", "1").length);
  }

  @Test
  public final void whenVehicleCountHalfHoursAreCorrect() {
    Assert.assertEquals(2*24, vs.countPerPeriod("A", "HALF_HOUR", "1").length);
  }

  @Test
  public final void whenVehicleCountTwentyMinutsAreCorrect() {
    Assert.assertEquals(3*24, vs.countPerPeriod("A", "TWENTY_MINUTES", "1").length);
  }

  @Test
  public final void whenVehicleCountFifteenMinutsAreCorrect() {
    Assert.assertEquals(4*24, vs.countPerPeriod("A", "FIFTEEN_MINUTES", "1").length);
  }

  @Test
  public final void whenNorthCarSummaryIsCorrectAllDays() {
    vs.loadSummary("AM_PM", false);
    ArrayList<VehicleSurvey.CountSummary> summary = vs.getCountSummary();
    int btotal = 0;
    for (int i=0; i< summary.size(); i++) {
      btotal += summary.get(i).getNorthCount();
    }
    Assert.assertEquals(11276, btotal);
  }

  @Test
  public final void whenNorthCarSummaryAverageIsCorrectAllDays() {
    vs.loadSummary("AM_PM", true);
    ArrayList<VehicleSurvey.CountSummary> summary = vs.getCountSummary();
    int btotal = 0;
    for (int i=0; i< summary.size(); i++) {
      btotal += summary.get(i).getNorthCount();
    }
    Assert.assertEquals(2252, btotal);
  }

  @Test
  public final void whenCreateAllOutput() {
    vs.loadSummary("AM_PM", false);
    vs.saveResults("output","test_am_pm.csv");
    vs.loadSummary("HOUR", false);
    vs.saveResults("output","test_1_hour.csv");
    vs.loadSummary("HALF_HOUR", false);
    vs.saveResults("output","test_half_hour.csv");
    vs.loadSummary("TWENTY_MINUTES", false);
    vs.saveResults("output","test_20_minutes.csv");
    vs.loadSummary("FIFTEEN_MINUTES", false);
    vs.saveResults("output","test_15_minutes.csv");
    ArrayList<VehicleSurvey.CountSummary> summary = vs.getCountSummary();
    int btotal = 0;
    for (int i=0; i< summary.size(); i++) {
      btotal += summary.get(i).getNorthCount();
    }
    Assert.assertEquals(11276, btotal);
  }

  @Test
  public final void whenAverageHourlySpeedIsCorrectAllDays() {
    vs.loadSummary("HOUR", false);
    ArrayList<VehicleSurvey.CountSummary> summary = vs.getCountSummary();
    double btotal = 0;
    for (int i=0; i< summary.size(); i++) {
      btotal += summary.get(i).getNorthSpeed();
    }
    Assert.assertEquals(62, btotal / summary.size(), 2.0);
  }

}
