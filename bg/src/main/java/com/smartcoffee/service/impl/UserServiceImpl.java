package com.smartcoffee.service.impl;

import com.smartcoffee.entity.User;
import com.smartcoffee.mapper.UserMapper;
import com.smartcoffee.service.UserService;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
  @Resource private UserMapper userMapper;

  @Override
  public User login(String username, String password) {
    User u = userMapper.findByUsername(username);
    if (u == null) return null;
    if (!password.equals(u.getPassword())) return null;
    if (u.getStatus() != null && u.getStatus() == 0) return null;
    return u;
  }

  @Override
  public List<User> list(Integer pageNum, Integer pageSize) {
    Integer offset = (pageNum - 1) * pageSize;
    return userMapper.list(offset, pageSize);
  }

  @Override
  public int count() {
    return userMapper.count();
  }

  @Override
  public boolean changePassword(Long userId, String oldPassword, String newPassword) {
    int rows = userMapper.updatePassword(userId, oldPassword, newPassword);
    return rows > 0;
  }
}
