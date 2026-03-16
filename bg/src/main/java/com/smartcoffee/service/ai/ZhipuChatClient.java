package com.smartcoffee.service.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ZhipuChatClient {
  private static final String DEFAULT_ENDPOINT = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
  private static final String DEFAULT_MODEL = "glm-4-flash";

  public boolean hasApiKey() {
    String apiKey = resolveApiKey();
    return apiKey != null && !apiKey.trim().isEmpty();
  }

  public String chat(String systemPrompt, String userPrompt) {
    String apiKey = resolveApiKey();
    if (apiKey == null || apiKey.trim().isEmpty()) {
      throw new IllegalStateException("ZHIPU_API_KEY is missing");
    }

    String endpoint = resolveEndpoint();
    String model = resolveModel();

    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(8000);
    factory.setReadTimeout(30000);
    RestTemplate restTemplate = new RestTemplate(factory);

    Map<String, Object> body = new HashMap<>();
    body.put("model", model);
    body.put("temperature", 0.2);

    List<Map<String, String>> messages = new ArrayList<>();
    messages.add(message("system", systemPrompt));
    messages.add(message("user", userPrompt));
    body.put("messages", messages);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "Bearer " + apiKey);

    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
    ResponseEntity<String> resp = restTemplate.postForEntity(endpoint, entity, String.class);
    String json = resp.getBody();
    if (json == null || json.trim().isEmpty()) return "";

    try {
      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode root = objectMapper.readTree(json);
      JsonNode content = root.path("choices").path(0).path("message").path("content");
      if (content.isMissingNode()) {
        content = root.path("data").path("choices").path(0).path("message").path("content");
      }
      return content.isMissingNode() ? "" : content.asText("");
    } catch (Exception e) {
      return "";
    }
  }

  private static Map<String, String> message(String role, String content) {
    Map<String, String> m = new HashMap<>();
    m.put("role", role);
    m.put("content", content);
    return m;
  }

  private static String resolveApiKey() {
    String underscore = System.getenv("Zhipu_Key");
    if (underscore != null && !underscore.trim().isEmpty()) return underscore;
    String legacy = System.getenv("Zhipu-Key");
    if (legacy != null && !legacy.trim().isEmpty()) return legacy;
    String env = System.getenv("ZHIPU_API_KEY");
    if (env != null && !env.trim().isEmpty()) return env;
    String sys = System.getProperty("zhipu.apiKey");
    if (sys != null && !sys.trim().isEmpty()) return sys;
    return null;
  }

  private static String resolveEndpoint() {
    String env = System.getenv("ZHIPU_API_ENDPOINT");
    if (env != null && !env.trim().isEmpty()) return env;
    String sys = System.getProperty("zhipu.endpoint");
    if (sys != null && !sys.trim().isEmpty()) return sys;
    return DEFAULT_ENDPOINT;
  }

  private static String resolveModel() {
    String env = System.getenv("ZHIPU_MODEL");
    if (env != null && !env.trim().isEmpty()) return env;
    String sys = System.getProperty("zhipu.model");
    if (sys != null && !sys.trim().isEmpty()) return sys;
    return DEFAULT_MODEL;
  }
}
