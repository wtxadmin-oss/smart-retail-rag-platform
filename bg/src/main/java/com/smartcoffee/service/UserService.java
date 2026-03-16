package com.smartcoffee.service;

import com.smartcoffee.entity.User;

import java.util.List;

public interface UserService {
  User login(String username, String password);
  List<User> list(Integer pageNum, Integer pageSize);
  int count();
  boolean changePassword(Long userId, String oldPassword, String newPassword);
}
