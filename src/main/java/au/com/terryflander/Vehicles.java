package au.com.terryflander;

import au.com.terryflander.EventDataSource.CounterEvent;
import java.util.ArrayList;
import java.util.Calendar;

class Vehicles {

  private final ArrayList<Vehicle> vehicles;
  private int numDays = 0;

  public Vehicles(EventDataSource counterEvents) {
    this.vehicles = new ArrayList<>();
    this.numDays = counterEvents.getNumDays();

    int i = counterEvents.getEvents().size() - 1;
    long[] lastTime = new long[2];
    while (0 <= i) {
      String direction = counterEvents.getEvents().get(i).getDirection();
      Vehicle v = new Vehicle(direction);
      int offset = direction.equals("B") ? 3 : 1;
      for (int j = i - offset; j <= i; j++) {
        v.addEvent(counterEvents.getEvents().get(j));
      }
      i = i - (offset + 1);
      v.setSpeed();
      v.setDistance(lastTime[direction.equals("B") ? 0 : 1]);
      lastTime[direction.equals("B") ? 0 : 1] = v.getEventTime();
      this.vehicles.add(v);
    }
  }

  public ArrayList<Vehicle> getVehicles() {
    return this.vehicles;
  }

  public int getNumDays() {
    return this.numDays;
  }

  public class Vehicle {

    private final String direction;
    private final ArrayList<CounterEvent> events;
    private long eventTime;
    private int dayNumber;
    private int hour;
    private int minute;
    private double speed;
    private double separation;

    public Vehicle(String direction) {
      this.direction = direction;
      this.events = new ArrayList<>();
    }

    public void addEvent(EventDataSource.CounterEvent event) {
      this.events.add(event);
      if (this.eventTime == 0) {
        this.eventTime = event.getEventTime();
        this.dayNumber = event.getDayNumber();
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
      for (EventDataSource.CounterEvent event : events) {
        if (event.getDirection().equals("A")) {
          if (elapsedTime == 0) {
            elapsedTime = event.getEventTime();
          } else {
            elapsedTime = event.getEventTime() - elapsedTime;
          }
        }
      }
      this.speed = (2.5 * 3600) / elapsedTime;
    }

    public void setDistance(long nextTime) {
      long elapsed;
      if (nextTime < this.eventTime) {
        elapsed = nextTime - (this.eventTime - (24 * 60 * 60 * 1000)); // Subtract a day
      } else {
        elapsed = nextTime - this.eventTime;
      }
      this.separation = ((this.speed * 1000) / 3600) * (elapsed / 3600);
    }

    public String getDirection() {
      return this.direction;
    }

    public long getEventTime() {
      return this.eventTime;
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

    public double getSpeed() {
      return this.speed;
    }

    public double getSeparation() {
      return this.separation;
    }

  }

}