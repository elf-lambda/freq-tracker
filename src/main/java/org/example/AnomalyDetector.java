package org.example;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class AnomalyDetector {
  private final LinkedList<MetricSnapshot> history = new LinkedList<>();
  private final int windowSize;
  private final double spikeThreshold; // Multiplier for what counts as a spike (2.0 = 200% of average)
  private final double dropThreshold; // Multiplier for what counts as a drop (0.5 = 50% of average)

  private Consumer<AnomalyEvent> onAnomaly;

  public AnomalyDetector(int windowSize, double spikeThreshold, double dropThreshold) {
    this.windowSize = windowSize;
    this.spikeThreshold = spikeThreshold;
    this.dropThreshold = dropThreshold;
  }

  public void setAnomalyHandler(Consumer<AnomalyEvent> handler) {
    this.onAnomaly = handler;
  }

  public LinkedList<MetricSnapshot> getHistory()
  {
    return history;
  }

  public void addSnapshot(MetricSnapshot snapshot) {
    history.add(snapshot);

    if (history.size() > windowSize) {
      history.removeFirst();
    }

    if (history.size() >= 3) {
      detectAnomalies(snapshot);
    }
  }

  private void detectAnomalies(MetricSnapshot current) {
    List<MetricSnapshot> baseline = history.subList(0, history.size() - 1);

    double avgNewThreads = baseline.stream().mapToInt(MetricSnapshot::newThreads).average().orElse(0);
    double avgReplies = baseline.stream().mapToInt(MetricSnapshot::totalReplyIncrease).average().orElse(0);
    double avgImages = baseline.stream().mapToInt(MetricSnapshot::totalImageIncrease).average().orElse(0);

    // Detect spikes
    if (avgNewThreads > 0 && current.newThreads() > avgNewThreads * spikeThreshold && current.newThreads() >= 5) {
      triggerAnomaly(new AnomalyEvent(
              AnomalyType.SPIKE,
              "new_threads",
              current.newThreads(),
              avgNewThreads,
              current.timestamp()
      ));
    }

    if (avgReplies > 0 && current.totalReplyIncrease() > avgReplies * spikeThreshold) {
      triggerAnomaly(new AnomalyEvent(
              AnomalyType.SPIKE,
              "replies",
              current.totalReplyIncrease(),
              avgReplies,
              current.timestamp()
      ));
    }

    // Detect drops
//    if (avgNewThreads > 0 && current.newThreads() < avgNewThreads * dropThreshold) {
//      triggerAnomaly(new AnomalyEvent(
//              AnomalyType.DROP,
//              "new_threads",
//              current.newThreads(),
//              avgNewThreads,
//              current.timestamp()
//      ));
//    }

//    if (avgReplies > 0 && current.totalReplyIncrease() < avgReplies * dropThreshold) {
//      triggerAnomaly(new AnomalyEvent(
//              AnomalyType.DROP,
//              "replies",
//              current.totalReplyIncrease(),
//              avgReplies,
//              current.timestamp()
//      ));
//    }
  }

  private void triggerAnomaly(AnomalyEvent event) {
    if (onAnomaly != null) {
      onAnomaly.accept(event);
    }
  }
}