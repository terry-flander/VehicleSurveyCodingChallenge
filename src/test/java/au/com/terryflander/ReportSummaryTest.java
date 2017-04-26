package au.com.terryflander;

import au.com.terryflander.ReportSummary.CountSummary;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ReportSummaryTest {

  private Vehicles vehicles;

  @Before
  public void setUp() throws Exception {
    EventDataSource eds = new EventDataSource("mock");
    vehicles = new Vehicles(eds);
  }

  @Test
  public void getCountSummary() {
    ReportSummary summary = new ReportSummary(15, true, vehicles);
    Assert.assertEquals(42, summary.getCountSummary().size());
  }

  @Test
  public final void whenNortyCarSummaryIsCorrectAllDays()  {
    ReportSummary summary = new ReportSummary(720, false, vehicles);
    ArrayList<ReportSummary.CountSummary> periods = summary.getCountSummary();
    int total = 0;
    for (CountSummary period : periods) {
      total += period.getNorthCount();
    }
    Assert.assertEquals(8, total);
  }

  @Test
  public final void whenSouthCarSummaryIsCorrectAllDays()  {
    ReportSummary summary = new ReportSummary(720, false, vehicles);
    ArrayList<ReportSummary.CountSummary> periods = summary.getCountSummary();
    int total = 0;
    for (CountSummary period : periods) {
      total += period.getSouthCount();
    }
    Assert.assertEquals(5, total);
  }


}