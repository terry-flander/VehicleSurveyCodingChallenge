package au.com.terryflander;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ReportPeriodsTest {

  private ReportPeriods rp;

  @Before
  public void initialize() {
    rp = new ReportPeriods();
  }

  @Test
  public final void whenContainsKeyIsCorrect() {
    Assert.assertTrue(rp.containsKey("AM_PM"));
  }

  @Test
  public final void whenContainsKeyIsIncorect() {
    Assert.assertFalse(rp.containsKey("ABC"));
  }

  @Test
  public final void whenGetMinutesPerPeriodIsIncorect() {
    Assert.assertEquals(720, rp.getMinutesPerPeriod("AM_PM"));
  }

}
