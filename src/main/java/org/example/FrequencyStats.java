package org.example;

public record FrequencyStats(
        int newThreads,
        int deletedThreads,
        int totalReplyIncrease,
        int totalImageIncrease
) {
  @Override
  public String toString() {
    return String.format(
            "New threads: %d | Deleted: %d | New replies: %d | New images: %d",
            newThreads, deletedThreads, totalReplyIncrease, totalImageIncrease
    );
  }
}