package org.example;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main
{
  public static void main(String[] args)
  {
    Scraper scraper = new Scraper("", "");
    FrequencyTracker tracker = new FrequencyTracker();
    MetricsLogger logger = new MetricsLogger("pol");
    logger.logHeader();

    AnomalyDetector detector = new AnomalyDetector(15, 2.0, 0.5);

    // Start web dashboard on port 7070
    WebDashboard dashboard = new WebDashboard(7070, 100);


    // Initialize Discord webhook
    DiscordWebhook webhook = new DiscordWebhook("");
    detector.setAnomalyHandler(anomaly -> {
      System.err.println("🚨 ANOMALY DETECTED: " + anomaly);
      try
      {
        // Simple message
        // webhook.sendMessage("**ANOMALY DETECTED**\n" + anomaly.toString());

        int color = anomaly.type() == AnomalyType.SPIKE ? 0xFF0000 : 0xFFA500;
        webhook.sendEmbed(
                "🚨 Anomaly Detected: " + anomaly.type(),
                String.format(
                        "**Metric:** %s\n**Current:** %.2f\n**Baseline:** %.2f\n**Change:** %.1f%%",
                        anomaly.metric(),
                        anomaly.currentValue(),
                        anomaly.baselineAverage(),
                        ((anomaly.currentValue() - anomaly.baselineAverage()) / anomaly.baselineAverage()) * 100
                ),
                color
        );
      } catch (IOException e)
      {
        System.err.println("Failed to send Discord notification: " + e.getMessage());
      }
    });


    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    scheduler.scheduleAtFixedRate(() -> {
      try
      {
        List<Post> posts = scraper.fetchThreads();
        FrequencyStats stats = tracker.analyzeSnapshot(posts);

        LocalDateTime now = LocalDateTime.now();
        MetricSnapshot snapshot = new MetricSnapshot(
                now,
                stats.newThreads(),
                stats.deletedThreads(),
                stats.totalReplyIncrease(),
                stats.totalImageIncrease()
        );

        logger.log(snapshot);

        // Skip first load
        if (snapshot.newThreads() >= 200)
        {
        } else
        {
          detector.addSnapshot(snapshot);
          dashboard.addSnapshot(snapshot);
        }

        System.out.println("[" + now + "] " + stats);

      } catch (IOException e)
      {
        System.err.println("Error: " + e.getMessage());
      }
    }, 0, 60, TimeUnit.SECONDS);

    System.out.println("Monitor started. Dashboard at http://localhost:7070");
  }
}