package org.example;

import org.jsoup.Jsoup;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Scraper
{
  private final String board;
  private final String site;

  public Scraper(String board, String site) {
    this.board = board;
    this.site = site;
  }

  public List<Post> fetchThreads() throws IOException {
    String json = Jsoup.connect(site + board + "/catalog.json")
            .ignoreContentType(true)
            .execute()
            .body();

    List<Post> posts = new ArrayList<>();
    JSONArray pages = new JSONArray(json);

    for (int i = 0; i < pages.length(); i++) {
      JSONObject page = pages.getJSONObject(i);
      JSONArray threads = page.getJSONArray("threads");

      for (int j = 0; j < threads.length(); j++) {
        JSONObject thread = threads.getJSONObject(j);

        String title = stripHtml(thread.optString("sub", ""));
        String content = stripHtml(thread.optString("com", ""));
        int replies = thread.optInt("replies", 0);
        int images = thread.optInt("images", 0);
        String hash = thread.optString("md5", String.valueOf(thread.optLong("no")));

        posts.add(new Post(title, content, replies, images, hash));
      }
    }

    return posts;
  }

  private String stripHtml(String html) {
    return Jsoup.parse(html).text();
  }
}