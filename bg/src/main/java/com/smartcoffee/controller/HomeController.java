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
    public Map<String, Object> root() {
        Map<String, Object> m = new HashMap<>();
        m.put("service", "SmartCoffee API");
        m.put("status", "UP");
        m.put("hint", "Use /api/** endpoints from your frontend app");
        return m;
    }

    @GetMapping("/api/health")
    @ResponseBody
    public Map<String, Object> health() {
        Map<String, Object> m = new HashMap<>();
        m.put("status", "UP");
        m.put("time", System.currentTimeMillis());
        return m;
    }
}

