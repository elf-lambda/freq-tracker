package org.example;

import java.util.*;

public class FrequencyTracker {
  private Map<String, Post> previousSnapshot = new HashMap<>();

  public FrequencyStats analyzeSnapshot(List<Post> currentPosts) {
    Map<String, Post> currentSnapshot = new HashMap<>();
    for (Post post : currentPosts) {
      currentSnapshot.put(post.hash(), post);
    }

    int newThreads = 0;
    int totalReplyIncrease = 0;
    int totalImageIncrease = 0;
    int deletedThreads = previousSnapshot.size() -
            (int) previousSnapshot.keySet().stream()
                    .filter(currentSnapshot::containsKey)
                    .count();

    // Check for new threads
    for (String hash : currentSnapshot.keySet()) {
      if (!previousSnapshot.containsKey(hash)) {
        newThreads++;
      }
    }

    // Check for reply/image increases
    for (Map.Entry<String, Post> entry : currentSnapshot.entrySet()) {
      String hash = entry.getKey();
      Post currentPost = entry.getValue();

      if (previousSnapshot.containsKey(hash)) {
        Post previousPost = previousSnapshot.get(hash);
        totalReplyIncrease += currentPost.replies() - previousPost.replies();
        totalImageIncrease += currentPost.images() - previousPost.images();
      }
    }

    previousSnapshot = currentSnapshot;

    return new FrequencyStats(newThreads, deletedThreads, totalReplyIncrease, totalImageIncrease);
  }
}