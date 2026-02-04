package org.example;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class WebDashboard {
  private final CopyOnWriteArrayList<MetricSnapshot> recentSnapshots = new CopyOnWriteArrayList<>();
  private final int maxDataPoints;
  private final Javalin app;

  public WebDashboard(int port, int maxDataPoints) {
    this.maxDataPoints = maxDataPoints;

    this.app = Javalin.create(config -> {
      config.staticFiles.add("/public", Location.CLASSPATH);
    });

    // API endpoint for live data
    app.get("/api/metrics", ctx -> {
      List<Map<String, Object>> data = new ArrayList<>();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

      for (MetricSnapshot snapshot : recentSnapshots) {
        Map<String, Object> point = new HashMap<>();
        point.put("timestamp", snapshot.timestamp().format(formatter));
        point.put("newThreads", snapshot.newThreads());
        point.put("deletedThreads", snapshot.deletedThreads());
        point.put("replies", snapshot.totalReplyIncrease());
        point.put("images", snapshot.totalImageIncrease());
        data.add(point);
      }

      ctx.json(data);
    });

    // Stats endpoint
    app.get("/api/stats", ctx -> {
      Map<String, Object> stats = new HashMap<>();

      if (!recentSnapshots.isEmpty()) {
        MetricSnapshot latest = recentSnapshots.get(recentSnapshots.size() - 1);
        stats.put("currentThreads", latest.newThreads());
        stats.put("currentReplies", latest.totalReplyIncrease());
        stats.put("currentImages", latest.totalImageIncrease());
        stats.put("totalDataPoints", recentSnapshots.size());

        // Calculate averages
        double avgReplies = recentSnapshots.stream()
                .mapToInt(MetricSnapshot::totalReplyIncrease)
                .average()
                .orElse(0);
        stats.put("avgReplies", Math.round(avgReplies * 100.0) / 100.0);
      } else {
        stats.put("currentThreads", 0);
        stats.put("currentReplies", 0);
        stats.put("currentImages", 0);
        stats.put("totalDataPoints", 0);
        stats.put("avgReplies", 0);
      }

      ctx.json(stats);
    });

    app.start(port);
  }

  public void addSnapshot(MetricSnapshot snapshot) {
    recentSnapshots.add(snapshot);

    while (recentSnapshots.size() > maxDataPoints) {
      recentSnapshots.remove(0);
    }
  }

  public void stop() {
    app.stop();
  }
}