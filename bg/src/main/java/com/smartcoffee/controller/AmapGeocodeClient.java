package com.smartcoffee.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Component
public class AmapGeocodeClient {
  private final ObjectMapper objectMapper = new ObjectMapper();

  public static class LngLat {
    public final double lng;
    public final double lat;
    public LngLat(double lng, double lat) {
      this.lng = lng;
      this.lat = lat;
    }
  }

  public LngLat geocode(String address) {
    String key = System.getProperty("amap.key");
    if (key == null || key.trim().isEmpty()) {
      key = System.getenv("AMAP_KEY");
    }
    if (key == null || key.trim().isEmpty()) {
      return null;
    }
    if (address == null || address.trim().isEmpty()) {
      return null;
    }
    try {
      String encoded = URLEncoder.encode(address.trim(), "UTF-8");
      String urlStr = "https://restapi.amap.com/v3/geocode/geo?address=" + encoded + "&key=" + URLEncoder.encode(key, "UTF-8");
      HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
      conn.setRequestMethod("GET");
      conn.setConnectTimeout(5000);
      conn.setReadTimeout(5000);

      int code = conn.getResponseCode();
      if (code != 200) return null;

      BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = br.readLine()) != null) sb.append(line);
      br.close();

      JsonNode root = objectMapper.readTree(sb.toString());
      JsonNode geocodes = root.get("geocodes");
      if (geocodes == null || !geocodes.isArray() || geocodes.size() == 0) return null;
      JsonNode location = geocodes.get(0).get("location");
      if (location == null) return null;
      String loc = location.asText();
      String[] parts = loc.split(",");
      if (parts.length != 2) return null;
      double lng = Double.parseDouble(parts[0]);
      double lat = Double.parseDouble(parts[1]);
      return new LngLat(lng, lat);
    } catch (Exception e) {
      return null;
    }
  }
}
