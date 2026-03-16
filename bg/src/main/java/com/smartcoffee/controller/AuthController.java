package com.smartcoffee.controller;

import com.smartcoffee.common.Result;
import com.smartcoffee.entity.User;
import com.smartcoffee.mapper.UserMapper;
import com.smartcoffee.service.UserService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Resource private UserService userService;
  @Resource private UserMapper userMapper;

  public static class LoginReq {
    public String username;
    public String password;
  }

  public static class ChangePwdReq {
    public Long userId;
    public String oldPassword;
    public String newPassword;
  }

  public static class RegisterReq {
    public String username;
    public String password;
  }

  @PostMapping("/login")
  public Result<Map<String, Object>> login(@RequestBody LoginReq req) {
    User u = userService.login(req.username, req.password);
    if (u == null) {
      return Result.fail(401, "用户名或密码错误，或账号被禁用");
    }
    Map<String, Object> data = new HashMap<>();
    data.put("userId", u.getId());
    data.put("username", u.getUsername());
    data.put("role", u.getRole());
    data.put("token", "dummy-token"); // 先返回一个占位符，后续可换成JWT
    return Result.ok(data);
  }

  @PostMapping("/register")
  public Result<Long> register(@RequestBody RegisterReq req) {
    String username = req == null ? null : req.username;
    String password = req == null ? null : req.password;
    if (username == null || username.trim().isEmpty()) {
      return Result.fail(400, "用户名不能为空");
    }
    if (password == null || password.trim().isEmpty()) {
      return Result.fail(400, "密码不能为空");
    }
    username = username.trim();
    User existed = userMapper.findByUsername(username);
    if (existed != null) {
      return Result.fail(400, "用户名已存在");
    }
    User u = new User();
    u.setUsername(username);
    u.setPassword(password);
    u.setRole("CUSTOMER");
    u.setStatus(1);
    userMapper.insert(u);
    return Result.ok(u.getId());
  }

  @PostMapping("/change-password")
  public Result<Void> changePassword(@RequestBody ChangePwdReq req) {
    boolean ok = userService.changePassword(req.userId, req.oldPassword, req.newPassword);
    if (!ok) return Result.fail(400, "原密码不正确或用户不存在");
    return Result.ok();
  }
}
