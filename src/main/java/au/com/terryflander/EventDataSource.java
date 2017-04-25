package au.com.terryflander;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class EventDataSource {

  private final ArrayList<CounterEvent> counterEvents;
  private int numDays = 0;

  public EventDataSource(String inFile) throws IOException {

    String loadFile;
    this.counterEvents = new ArrayList<>();

    if (inFile.equals("test")) {
      loadFile = "./Vehicle Survey Coding Challenge sample data.txt";
    } else {
      loadFile = inFile;
    }

    Path path = FileSystems.getDefault().getPath(".", loadFile);

    if (Files.exists(path)) {
      ArrayList<String> records;
      try {
        records = (ArrayList<String>) Files.readAllLines(path, Charset.defaultCharset());
        int dayNumber = 0;
        long lastEventTime = 999999999;
        for (String record: records) {
          long eventTime = getEventTime(record.substring(1));
          if (eventTime < lastEventTime) {
            dayNumber++;
          }
          counterEvents.add(new CounterEvent(record.substring(0,1), eventTime, dayNumber));
          lastEventTime = eventTime;
        }
        numDays = dayNumber;
      } catch (IOException ioe) {
        System.out.println("Could not read load file: " + loadFile);
        throw ioe;
      }
    } else {
      File f = new File("./");
      System.out.println("Load file not found: " + loadFile + " looking from "
          + f.getAbsolutePath());
      IOException e = new IOException("File not found: " + loadFile);
      throw e;
    }
  }

  public int getNumDays() {
    return this.numDays;
  }

  public ArrayList<CounterEvent> getEvents() {
    return this.counterEvents;
  }

  protected long getEventTime(String value) {
    long result = 0;
    try {
      result = Long.valueOf(value);
    } catch (Exception e) {
      System.out.println("Could not convert time to number: " + value);
      throw e;
    }
    return result;
  }

  public static class CounterEvent {
    private final String direction;
    private final long eventTime;
    private final int dayNumber;

    public CounterEvent(String direction, long eventTime, int dayNumber) {
      this.direction = direction;
      this.dayNumber = dayNumber;
      this.eventTime = eventTime;
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
  }
}
