package au.com.terryflander;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class VehiclesTest {

  private Vehicles vehicles;

  @Before
  public void setUp() throws Exception {
    try {
      EventDataSource eds = new EventDataSource("mock");
      vehicles = new Vehicles(eds);
    } catch (Exception e) {
      System.out.println("EventDataSource MOCK create error: " + e.getMessage());
    }
  }

  @Test
  public void getVehicles() throws Exception {
    Assert.assertEquals(13, vehicles.getVehicles().size());
  }

  @Test
  public void getNumDays() throws Exception {
    Assert.assertEquals(3, vehicles.getNumDays());
  }

}