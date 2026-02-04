package org.example;

import java.time.LocalDateTime;

public record AnomalyEvent(
        AnomalyType type,
        String metric,
        double currentValue,
        double baselineAverage,
        LocalDateTime timestamp
) {
  @Override
  public String toString() {
    return String.format("[%s] %s in %s: %.2f (baseline: %.2f, %.1f%% change)",
            type,
            type == AnomalyType.SPIKE ? "SPIKE" : "DROP",
            metric,
            currentValue,
            baselineAverage,
            ((currentValue - baselineAverage) / baselineAverage) * 100
    );
  }
}