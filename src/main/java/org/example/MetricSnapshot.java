package org.example;

import java.time.LocalDateTime;

public record MetricSnapshot(
        LocalDateTime timestamp,
        int newThreads,
        int deletedThreads,
        int totalReplyIncrease,
        int totalImageIncrease
) {
}