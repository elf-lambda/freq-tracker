package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MetricsLogger {
  public String getSite()
  {
    return site;
  }

  public void setSite(String site)
  {
    this.site = site;
  }

  private String site;
  private final String logFile;
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  private static final DateTimeFormatter fileFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

  public MetricsLogger() {
    String timestamp = LocalDateTime.now().format(fileFormatter);
    this.logFile = "metrics_"+ site + timestamp + ".csv";
  }

  public MetricsLogger(String site) {
    this.site = site;
    String timestamp = LocalDateTime.now().format(fileFormatter);
    this.logFile = "metrics_"+ site + "_" + timestamp + ".csv";
  }

  public String getLogFile() {
    return logFile;
  }

  public void log(MetricSnapshot snapshot) {
    try (PrintWriter writer = new PrintWriter(new FileWriter(logFile, true))) {
      writer.printf("%s,%d,%d,%d,%d%n",
              snapshot.timestamp().format(formatter),
              snapshot.newThreads(),
              snapshot.deletedThreads(),
              snapshot.totalReplyIncrease(),
              snapshot.totalImageIncrease()
      );
    } catch (IOException e) {
      System.err.println("Failed to write log: " + e.getMessage());
    }
  }

  public void logHeader() {
    try (PrintWriter writer = new PrintWriter(new FileWriter(logFile, false))) {
      writer.println("timestamp,new_threads,deleted_threads,reply_increase,image_increase");
    } catch (IOException e) {
      System.err.println("Failed to write header: " + e.getMessage());
    }
  }
}