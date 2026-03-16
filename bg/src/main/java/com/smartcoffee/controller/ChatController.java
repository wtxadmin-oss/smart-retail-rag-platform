package com.smartcoffee.controller;

import com.smartcoffee.service.ai.RagChatService;
import com.smartcoffee.service.ai.ZhipuChatClient;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ChatController {
  @Resource private RagChatService ragChatService;
  @Resource private StringRedisTemplate stringRedisTemplate;
  @Resource private ZhipuChatClient zhipuChatClient;

  public static class ChatReq {
    public String message;
  }

  @PostMapping("/chat")
  public Map<String, Object> chat(@RequestBody ChatReq req) {
    String msg = req == null ? null : req.message;
    if (msg == null || msg.trim().isEmpty()) {
      Map<String, Object> r = new HashMap<>();
      r.put("reply", "请先输入问题。");
      return r;
    }
    String reply = ragChatService.chat(msg);
    Map<String, Object> r = new HashMap<>();
    r.put("reply", reply);
    return r;
  }

  @PostMapping("/admin/rag/rebuild")
  public Map<String, Object> rebuild() {
    int count = ragChatService.rebuildRagIndex();
    Map<String, Object> r = new HashMap<>();
    r.put("count", count);
    r.put("message", "OK");
    return r;
  }

  @GetMapping("/admin/rag/status")
  public Map<String, Object> status() {
    Map<String, Object> r = new HashMap<>();
    r.put("ragKey", ragChatService.getRagKey());
    r.put("hasZhipuKey", zhipuChatClient.hasApiKey());
    try {
      Long size = stringRedisTemplate.opsForHash().size(ragChatService.getRagKey());
      r.put("redisOk", true);
      r.put("ragSize", size == null ? 0 : size);
    } catch (DataAccessException e) {
      r.put("redisOk", false);
      r.put("ragSize", 0);
    }
    return r;
  }
}
