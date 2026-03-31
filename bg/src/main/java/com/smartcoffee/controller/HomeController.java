package com.smartcoffee.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/")
public class HomeController {
    @GetMapping("/")
    @ResponseBody
    // 返回根路径说明信息，方便快速确认服务已经启动。
    public Map<String, Object> root() {
        Map<String, Object> m = new HashMap<>();
        m.put("service", "SmartCoffee API");
        m.put("status", "UP");
        m.put("hint", "Use /api/** endpoints from your frontend app");
        return m;
    }

    @GetMapping("/api/health")
    @ResponseBody
    // 返回一个简单健康检查结果，便于联调时确认后端存活。
    public Map<String, Object> health() {
        Map<String, Object> m = new HashMap<>();
        m.put("status", "UP");
        m.put("time", System.currentTimeMillis());
        return m;
    }
}

