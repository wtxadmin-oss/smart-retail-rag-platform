package com.smartcoffee.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;

public class User {
  private Long id;
  private String username;
  @JsonIgnore
  private String password;
  private String role;
  private String phone;
  private Integer status;
  private Date createTime;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }
  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }
  public String getRole() { return role; }
  public void setRole(String role) { this.role = role; }
  public String getPhone() { return phone; }
  public void setPhone(String phone) { this.phone = phone; }
  public Integer getStatus() { return status; }
  public void setStatus(Integer status) { this.status = status; }
  public Date getCreateTime() { return createTime; }
  public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
