package au.com.terryflander;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *  The purpose of this class is to create an ArrayList of CounterEvent object for processing.
 **/

class EventDataSource {

  private final ArrayList<CounterEvent> counterEvents;
  private int numDays = 0;

  /**
   * The purpose of this method is to construct Class with the desired data
   *
   *  @param inFile -- the type of data to load.
    *  "mock"   -- Will load counterEvents with internal Mock data.
    *  "test"   -- Will load counterEvents from supplied test data file.
    *  fileName -- Will attempt to load counterEvents from a file with the specified name which must
    *              be accessible from the run directory. Will throw IOException if not available or
   *              able to be read.
  **/
  public EventDataSource(String inFile) throws IOException {

    this.counterEvents = new ArrayList<>();

    if (inFile.equals("mock")) {
      createEventsFromArray(getMockData());
    } else {
      createEventsFromFile(inFile);
    }

  }

  private void createEventsFromFile(String inFile) throws IOException {
    String loadFile;
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
      } catch (IOException ioe) {
        System.out.println("Could not read load file: " + loadFile);
        throw ioe;
      }
      createEventsFromArray(records);
    } else {
      File f = new File("./");
      System.out.println("Load file not found: " + loadFile + " looking from "
          + f.getAbsolutePath());
      throw new IOException("File not found: " + loadFile);
    }
  }

  private void createEventsFromArray(ArrayList<String> records) {
    int dayNumber = 0;
    long lastEventTime = 999999999;
    for (String record: records) {
      long eventTime = getEventTime(record.substring(1));
      if (eventTime < lastEventTime) {
        dayNumber++;
      }
      this.counterEvents.add(new CounterEvent(record.substring(0,1), eventTime, dayNumber));
      lastEventTime = eventTime;
    }
    this.numDays = dayNumber;
  }

  public int getNumDays() {
    return this.numDays;
  }

  public ArrayList<CounterEvent> getEvents() {
    return this.counterEvents;
  }

  @SuppressWarnings("WeakerAccess")
  protected long getEventTime(String value) {
    long result;
    try {
      result = Long.valueOf(value);
    } catch (Exception e) {
      System.out.println("Could not convert time to number: " + value);
      throw e;
    }
    return result;
  }

  private ArrayList<String> getMockData() {
    String mockData =
        // Start of day 1 sample
        "A98186;A98333;A499718;A499886;A638379;B638382;A638520;B638523;A1016488;A1016648;"
        + "A1058535;B1058538;A1058659;B1058662;A1201386;B1201389;A1201539;B1201542;"
        // End of day 1 sample start of day 2
        + "A86335139;A86335248;A86351522;B86351525;A86351669;B86351672;A156007;B156011;A156220;"
        + "B156224;A457868;A457996;"
        // End of day 2 sample start of day 3
        + "A86381692;B86381695;A86381834;B86381837;A88663;B88666;A88800;B88803;A210423;B210426;"
        + "A210586;B210589";
    return new ArrayList<>(Arrays.asList(mockData.split(";")));
  }

  public static class CounterEvent {
    private final String direction;
    private final long eventTime;
    private final int dayNumber;

    /**
     * The purpose of this method is to create a CounterEvent.
     * @param direction -- the direction of travel A = South, B = North
     * @param dayNumber -- the day corresponding to this event -- 1 indexed
     * @param eventTime -- the value representing the number of milliseconds since midnight
     */
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
