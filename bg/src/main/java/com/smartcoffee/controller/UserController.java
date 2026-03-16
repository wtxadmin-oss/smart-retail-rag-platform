package com.smartcoffee.controller;

import com.smartcoffee.common.Result;
import com.smartcoffee.entity.User;
import com.smartcoffee.mapper.UserMapper;
import com.smartcoffee.service.UserService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class UserController {
    @Resource private UserService userService;
    @Resource private UserMapper userMapper;

    @GetMapping
    public Result<Result.PageData<User>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        List<User> list = userService.list(pageNum, pageSize);
        int total = userService.count();
        return Result.page(total, list);
    }

    @PostMapping
    public Result<Long> create(@RequestBody User user) {
        if (user.getRole() != null) user.setRole(user.getRole().toUpperCase());
        if (user.getStatus() == null) user.setStatus(1);
        userMapper.insert(user);
        return Result.ok(user.getId());
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        if (user.getRole() != null) user.setRole(user.getRole().toUpperCase());
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            User old = userMapper.findById(id);
            if (old != null) user.setPassword(old.getPassword());
        }
        userMapper.update(user);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userMapper.deleteById(id);
        return Result.ok();
    }
}
