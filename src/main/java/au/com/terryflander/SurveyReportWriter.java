package au.com.terryflander;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class SurveyReportWriter {

  public static void saveResults(ArrayList<ReportSummary.CountSummary> countSummary,
      String dirName, String fileName) {
    // Output Results
    PrintWriter pw;
    try {
      pw = saveData(dirName, fileName);
      pw.println("Day,Hour,Minute,North Count,South Count,North Speed (km/hr),South Speed (km/hr)"
          + ",North Separation (M),South Separation (M),North Peak,South Peak");
      for (ReportSummary.CountSummary fs : countSummary) {
        pw.println(fs.getDayNumber() + ","
            + fs.getHour() + ","
            + fs.getMinute() + ","
            + fs.getNorthCount() + ","
            + fs.getSouthCount() + ","
            + round(fs.getNorthSpeed(), 0) + ","
            + round(fs.getSouthSpeed(), 0) + ","
            + round(fs.getNorthSeparation(), 0) + ","
            + round(fs.getSouthSeparation(), 0) + ","
            + (fs.getNorthPeak() ? "Peak" : "") + ","
            + (fs.getSouthPeak() ? "Peak" : "")
        );
      }
      pw.close();
    } catch (Exception e) {
      System.out.println("saveError: " + e.getMessage());
    }

  }

  @SuppressWarnings("SameParameterValue")
  private static double round(double value, int places) {
    if (places < 0) {
      throw new IllegalArgumentException();
    }

    BigDecimal bd = new BigDecimal(value);
    bd = bd.setScale(places, RoundingMode.HALF_DOWN);
    return bd.doubleValue();
  }

  private static PrintWriter saveData(String dirName, String fileName) {
    if (fileName.equals("test")) {
      return writeTest();
    } else {
      return writeFile(dirName, fileName);
    }
  }

  private static PrintWriter writeTest() {
    return new PrintWriter(System.out);
  }

  private static PrintWriter writeFile(String dirName, String fileName) {
    File f = new File(dirName);
    if (!f.isDirectory()) {
      try {
        if (!f.mkdirs()) {
          System.out.println("writeFile: Create missing directories failed.");
        }
      } catch (Exception e) {
        System.out.println("writeFile: Could not create directories. " + e.getMessage());
      }
    }
    PrintWriter result = null;
    Path path = FileSystems.getDefault().getPath(dirName + "/" + fileName);
    try {
      result =
          new PrintWriter(Files.newBufferedWriter(path, java.nio.charset.StandardCharsets.UTF_8));
    } catch (IOException e) {
      System.out.println("writeFile: Could not " + e.getMessage());
    }
    return result;
  }

}
