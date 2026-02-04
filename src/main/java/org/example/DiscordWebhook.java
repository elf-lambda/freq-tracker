package org.example;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DiscordWebhook {
  private final String webhookUrl;

  public DiscordWebhook(String webhookUrl) {
    this.webhookUrl = webhookUrl;
  }

  public void sendMessage(String content) throws IOException {
    JSONObject json = new JSONObject();
    json.put("content", content);

    sendJsonPayload(json.toString());
  }

  public void sendEmbed(String title, String description, int color) throws IOException {
    JSONObject embed = new JSONObject();
    embed.put("title", title);
    embed.put("description", description);
    embed.put("color", color);

    JSONObject json = new JSONObject();
    json.put("embeds", new JSONObject[]{embed});

    sendJsonPayload(json.toString());
  }

  private void sendJsonPayload(String jsonPayload) throws IOException {
    URL url = new URL(webhookUrl);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    connection.setRequestMethod("POST");
    connection.setRequestProperty("Content-Type", "application/json");
    connection.setDoOutput(true);

    try (OutputStream os = connection.getOutputStream()) {
      byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
      os.write(input, 0, input.length);
    }

    int responseCode = connection.getResponseCode();
    if (responseCode != 204) {
      throw new IOException("Discord webhook failed with response code: " + responseCode);
    }
  }
}