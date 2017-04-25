package au.com.terryflander;

import au.com.terryflander.EventDataSource.CounterEvent;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

public class EventDataSourceTest {

  EventDataSource eds;

  @Before
  public void initialize() {
    try {
      eds = new EventDataSource("test");
    } catch (Exception e) {
      System.out.println("EventDataSource create error: " + e.getMessage());
    }
  }

  @Test(expected=IOException.class)
  public final void whenInputFileCannotBeRead() throws IOException {
    try {
      eds = new EventDataSource("");
    } catch (Exception e) {
      throw e;
    }
  }

  @Test(expected=IOException.class)
  public final void whenInputFileDoesNotExist() throws IOException {
    try {
      eds = new EventDataSource("aBogusName");
    } catch (Exception e) {
      throw e;
    }
  }

  @Test
  public final void whenReadTestFileCountIsCorrect() {
    Assert.assertEquals(67296, eds.getEvents().size());
  }

  @Test
  public final void whenDateCountIsCorrect() {
    Assert.assertEquals(5, eds.getNumDays());
  }

  @Test
  public final void whenGetEventTimeIsCorrect() {
    Assert.assertEquals(100, eds.getEventTime("100"));
  }

  @Test(expected=NumberFormatException.class)
  public final void whenGetEventTimeIsNotCorrect() {
    eds.getEventTime("");
  }

  @Test
  public final void whenDirectionIsCorrect() {
    Assert.assertEquals("A", eds.getEvents().get(0).getDirection());
  }

  @Test
  public final void whenEventTimeIsCorrect() {
    Assert.assertEquals(98186, eds.getEvents().get(0).getEventTime());
  }

  @Test
  public final void whenDayNumberIsCorrect() {
    Assert.assertEquals(1, eds.getEvents().get(0).getDayNumber());
  }

  @Test
  public final void whenCreateEventIsCorrect() {
    CounterEvent ev = new EventDataSource.CounterEvent("A",1000L,1);
    Assert.assertEquals(1, ev.getDayNumber());
  }
}
