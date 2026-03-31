package com.smartcoffee.controller;

import com.smartcoffee.common.Result;
import com.smartcoffee.entity.User;
import com.smartcoffee.mapper.UserMapper;
import com.smartcoffee.service.UserService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class UserController {
    @Resource private UserService userService;
    @Resource private UserMapper userMapper;

    @GetMapping
    // 分页查询后台用户列表。
    public Result<Result.PageData<User>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        if (pageSize > 100) pageSize = 100;
        List<User> list = userService.list(pageNum, pageSize);
        int total = userService.count();
        return Result.page(total, list);
    }

    @PostMapping
    // 新建后台用户，未提供密码时使用系统默认密码。
    public Result<Long> create(@RequestBody User user) {
        if (user.getRole() != null) user.setRole(user.getRole().toUpperCase());
        if (user.getStatus() == null) user.setStatus(1);
        
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        } else {
            user.setPassword(BCrypt.hashpw("123456", BCrypt.gensalt()));
        }
        
        userMapper.insert(user);
        return Result.ok(user.getId());
    }

    @PutMapping("/{id}")
    // 更新指定用户资料，未填写新密码时沿用旧密码。
    public Result<Void> update(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        if (user.getRole() != null) user.setRole(user.getRole().toUpperCase());
        
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            User old = userMapper.findById(id);
            if (old != null) user.setPassword(old.getPassword());
        } else {
            user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        }
        
        userMapper.update(user);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    // 删除后台中的指定用户。
    public Result<Void> delete(@PathVariable Long id) {
        userMapper.deleteById(id);
        return Result.ok();
    }
}
