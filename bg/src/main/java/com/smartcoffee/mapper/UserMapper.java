package com.smartcoffee.mapper;

import com.smartcoffee.entity.User;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface UserMapper {
  User findById(@Param("id") Long id);
  User findByUsername(@Param("username") String username);
  List<User> list(@Param("offset") Integer offset, @Param("limit") Integer limit);
  int count();
  int insert(User user);
  int update(User user);
  int deleteById(@Param("id") Long id);
  int updatePassword(@Param("id") Long id, @Param("oldPassword") String oldPassword, @Param("newPassword") String newPassword);
}
