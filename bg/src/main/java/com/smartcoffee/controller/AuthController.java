package com.smartcoffee.controller;

import com.smartcoffee.common.Result;
import com.smartcoffee.entity.User;
import com.smartcoffee.mapper.UserMapper;
import com.smartcoffee.service.UserService;
import com.smartcoffee.utils.JwtUtils;
import org.mindrot.jbcrypt.BCrypt;
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
    public String oldPassword;
    public String newPassword;
  }

  public static class RegisterReq {
    public String username;
    public String password;
  }

  @PostMapping("/login")
  // 校验用户名和密码，登录成功后签发 JWT 并返回基础用户信息。
  public Result<Map<String, Object>> login(@RequestBody LoginReq req) {
    User u = userService.login(req.username, req.password);
    if (u == null) {
      return Result.fail(401, "用户名或密码错误");
    }
    Map<String, Object> data = new HashMap<>();
    data.put("userId", u.getId());
    data.put("username", u.getUsername());
    data.put("role", u.getRole());

    String token = JwtUtils.generateToken(u.getId(), u.getRole());
    data.put("token", token);
    return Result.ok(data);
  }

  @PostMapping("/register")
  // 注册新账号，校验输入并对密码加密后写入数据库。
  public Result<Long> register(@RequestBody RegisterReq req) {
    String username = req == null ? null : req.username;
    String password = req == null ? null : req.password;
    if (username == null || username.trim().isEmpty()) {
      return Result.fail(400, "用户名不能为空");
    }
    if (password == null || password.trim().isEmpty()) {
      return Result.fail(400, "密码不能为空");
    }
    if (password.length() < 6) {
      return Result.fail(400, "密码长度不能小于6位");
    }
    username = username.trim();
    User existed = userMapper.findByUsername(username);
    if (existed != null) {
      return Result.fail(400, "用户名已存在");
    }
    User u = new User();
    u.setUsername(username);

    String hashedPwd = BCrypt.hashpw(password, BCrypt.gensalt());
    u.setPassword(hashedPwd);
    u.setRole("CUSTOMER");
    u.setStatus(1);
    userMapper.insert(u);
    return Result.ok(u.getId());
  }

  @PostMapping("/change-password")
  // 根据当前登录用户身份校验旧密码，再更新为新密码。
  public Result<Void> changePassword(@RequestBody ChangePwdReq req, javax.servlet.http.HttpServletRequest request) {
    if (req.newPassword == null || req.newPassword.length() < 6) {
      return Result.fail(400, "新密码长度不能小于6位");
    }

    Long currentUserId = (Long) request.getAttribute("currentUserId");
    if (currentUserId == null) {
      return Result.fail(401, "未登录");
    }

    boolean ok = userService.changePassword(currentUserId, req.oldPassword, req.newPassword);
    if (!ok) return Result.fail(400, "旧密码错误或用户不存在");
    return Result.ok();
  }
}
