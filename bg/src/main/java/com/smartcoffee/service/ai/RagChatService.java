package com.smartcoffee.service.ai;

import com.smartcoffee.entity.Product;
import com.smartcoffee.mapper.ProductMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class RagChatService {
  private static final String RAG_PRODUCTS_HASH = "rag:products";
  private static final int MAX_REBUILD_PRODUCTS = 100000;
  private static final int MAX_CONTEXT_CHARS = 800;
  private static final Pattern TOKEN_SPLIT = Pattern.compile("[\\s,，。.!?;；:：、】【()（）]+");

  @Resource private StringRedisTemplate stringRedisTemplate;
  @Resource private ProductMapper productMapper;
  @Resource private ZhipuChatClient zhipuChatClient;

  public String chat(String userMessage) {
    List<String> contexts = retrieveTopKContexts(userMessage, 6);
    String systemPrompt =
      "你是 SmartCoffee 的智能客服。你必须用中文回答。"
        + "你只能根据给定的【商品知识库】回答商品相关问题；"
        + "如果知识库里没有相关信息，就明确说明“知识库中未找到相关商品信息”，并给出一般性建议，但不要编造商品信息。";

    StringBuilder userPrompt = new StringBuilder();
    userPrompt.append("【商品知识库】\n");
    if (contexts.isEmpty()) {
      userPrompt.append("(空)\n");
    } else {
      for (int i = 0; i < contexts.size(); i++) {
        userPrompt.append(i + 1).append(". ").append(contexts.get(i)).append("\n");
      }
    }
    userPrompt.append("\n【用户问题】\n").append(userMessage);

    try {
      String reply = zhipuChatClient.chat(systemPrompt, userPrompt.toString());
      if (reply == null || reply.trim().isEmpty()) {
        return "抱歉，我暂时无法生成回复，请稍后再试。";
      }
      return reply.trim();
    } catch (IllegalStateException e) {
      return "智谱API Key 未配置或进程未读取到环境变量，请检查 Zhipu-Key 是否对后端进程生效。";
    } catch (Exception e) {
      return "抱歉，智能客服暂不可用，请稍后再试。";
    }
  }

  public String getRagKey() {
    return RAG_PRODUCTS_HASH;
  }

  public int rebuildRagIndex() {
    List<Product> products = productMapper.list(null, null, 0, MAX_REBUILD_PRODUCTS);
    Map<String, String> entries = new HashMap<>();
    for (Product p : products) {
      String content = buildProductDoc(p);
      if (content == null || content.trim().isEmpty()) continue;
      entries.put(String.valueOf(p.getId()), content);
    }
    stringRedisTemplate.delete(RAG_PRODUCTS_HASH);
    if (!entries.isEmpty()) {
      stringRedisTemplate.opsForHash().putAll(RAG_PRODUCTS_HASH, entries);
    }
    return entries.size();
  }

  private List<String> retrieveTopKContexts(String query, int k) {
    if (query == null) query = "";
    String q = query.trim();
    if (q.isEmpty()) return new ArrayList<>();

    Map<String, String> docs = new HashMap<>();
    try {
      ensureIndexExists();
      Map<Object, Object> raw = stringRedisTemplate.opsForHash().entries(RAG_PRODUCTS_HASH);
      for (Map.Entry<Object, Object> e : raw.entrySet()) {
        if (e.getKey() == null || e.getValue() == null) continue;
        docs.put(String.valueOf(e.getKey()), String.valueOf(e.getValue()));
      }
    } catch (DataAccessException ex) {
      List<Product> products = productMapper.list(null, null, 0, MAX_REBUILD_PRODUCTS);
      for (Product p : products) {
        docs.put(String.valueOf(p.getId()), buildProductDoc(p));
      }
    }

    List<String> tokens = tokenize(q);
    String qLower = q.toLowerCase(Locale.ROOT);

    return docs.values().stream()
      .map(doc -> new ScoredDoc(doc, score(doc, qLower, tokens)))
      .filter(sd -> sd.score > 0)
      .sorted(Comparator.comparingInt((ScoredDoc d) -> d.score).reversed())
      .limit(k)
      .map(sd -> trimDoc(sd.doc))
      .collect(Collectors.toList());
  }

  private void ensureIndexExists() {
    Boolean exists = stringRedisTemplate.hasKey(RAG_PRODUCTS_HASH);
    if (exists != null && exists) {
      Long size = stringRedisTemplate.opsForHash().size(RAG_PRODUCTS_HASH);
      if (size != null && size > 0) return;
    }
    rebuildRagIndex();
  }

  private static String buildProductDoc(Product p) {
    if (p == null) return "";
    String name = safe(p.getName());
    String desc = safe(p.getDescription());
    StringBuilder sb = new StringBuilder();
    sb.append("商品ID: ").append(p.getId()).append("；");
    sb.append("商品名: ").append(name);
    if (!desc.isEmpty()) {
      sb.append("；商品详情: ").append(desc);
    }
    return sb.toString();
  }

  private static String safe(String s) {
    return s == null ? "" : s.trim();
  }

  private static List<String> tokenize(String q) {
    String qq = q == null ? "" : q.trim().toLowerCase(Locale.ROOT);
    Set<String> tokens = new LinkedHashSet<>();

    String[] parts = TOKEN_SPLIT.split(qq);
    for (String p : parts) {
      if (p == null) continue;
      String t = p.trim();
      if (t.length() < 2) continue;
      tokens.add(t);
    }

    for (String seg : splitCjkSegments(qq)) {
      int len = seg.length();
      if (len < 2) continue;
      int maxN = Math.min(4, len);
      for (int n = 2; n <= maxN; n++) {
        for (int i = 0; i + n <= len; i++) {
          tokens.add(seg.substring(i, i + n));
          if (tokens.size() >= 80) break;
        }
        if (tokens.size() >= 80) break;
      }
      if (tokens.size() >= 80) break;
    }

    if (tokens.isEmpty() && !qq.isEmpty()) tokens.add(qq);
    return new ArrayList<>(tokens);
  }

  private static List<String> splitCjkSegments(String s) {
    List<String> out = new ArrayList<>();
    if (s == null || s.isEmpty()) return out;
    StringBuilder cur = new StringBuilder();
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (isCjk(c)) {
        cur.append(c);
      } else {
        if (cur.length() > 0) {
          out.add(cur.toString());
          cur.setLength(0);
        }
      }
    }
    if (cur.length() > 0) out.add(cur.toString());
    return out;
  }

  private static boolean isCjk(char c) {
    Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
    return block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
      || block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
      || block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
      || block == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
      || block == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT;
  }

  private static int score(String doc, String qLower, List<String> tokens) {
    if (doc == null) return 0;
    String d = doc.toLowerCase(Locale.ROOT);
    int s = 0;
    if (d.contains(qLower)) s += 8;
    String productName = extractProductName(d);
    if (!productName.isEmpty()) {
      if (qLower.contains(productName) || productName.contains(qLower)) s += 12;
    }
    for (String t : tokens) {
      if (t == null || t.isEmpty()) continue;
      if (!d.contains(t)) continue;
      if (t.length() >= 4) s += 6;
      else if (t.length() == 3) s += 4;
      else s += 2;
    }
    return s;
  }

  private static String extractProductName(String docLower) {
    if (docLower == null) return "";
    int i = docLower.indexOf("商品名:");
    if (i < 0) return "";
    int start = i + "商品名:".length();
    int end = docLower.indexOf("；", start);
    if (end < 0) end = docLower.length();
    String name = docLower.substring(start, end).trim();
    return name;
  }

  private static String trimDoc(String doc) {
    if (doc == null) return "";
    String d = doc.trim();
    if (d.length() <= MAX_CONTEXT_CHARS) return d;
    return d.substring(0, MAX_CONTEXT_CHARS);
  }

  private static class ScoredDoc {
    final String doc;
    final int score;
    ScoredDoc(String doc, int score) {
      this.doc = doc;
      this.score = score;
    }
  }
}
