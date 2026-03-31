package com.smartcoffee.service.impl;

import com.smartcoffee.entity.User;
import com.smartcoffee.mapper.UserMapper;
import com.smartcoffee.service.UserService;
import org.mindrot.jbcrypt.BCrypt;
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
    if (!checkPassword(password, u.getPassword())) return null;
    if (u.getStatus() != null && u.getStatus() == 0) return null;
    return u;
  }

  private boolean checkPassword(String rawPassword, String encodedPassword) {
    if (encodedPassword == null) return false;
    if (encodedPassword.startsWith("$2a$")) {
      try {
        return BCrypt.checkpw(rawPassword, encodedPassword);
      } catch (Exception e) {
        return false;
      }
    }
    return rawPassword.equals(encodedPassword);
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
    User u = userMapper.findById(userId);
    if (u == null) return false;
    if (!checkPassword(oldPassword, u.getPassword())) return false;
    
    String hashedNew = BCrypt.hashpw(newPassword, BCrypt.gensalt());
    u.setPassword(hashedNew);
    int rows = userMapper.update(u);
    return rows > 0;
  }
}
