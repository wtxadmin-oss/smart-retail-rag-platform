package com.smartcoffee.entity;

public class Store {
  private Long id;
  private String name;
  private String address;
  private String phone;
  private Double lng;
  private Double lat;
  private Integer status; // 1-营业中, 0-暂停

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getAddress() { return address; }
  public void setAddress(String address) { this.address = address; }
  public String getPhone() { return phone; }
  public void setPhone(String phone) { this.phone = phone; }
  public Double getLng() { return lng; }
  public void setLng(Double lng) { this.lng = lng; }
  public Double getLat() { return lat; }
  public void setLat(Double lat) { this.lat = lat; }
  public Integer getStatus() { return status; }
  public void setStatus(Integer status) { this.status = status; }
}
