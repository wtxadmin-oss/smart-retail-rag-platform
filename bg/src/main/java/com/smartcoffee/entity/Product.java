package com.smartcoffee.entity;

import java.math.BigDecimal;

public class Product {
  private Long id;
  private Integer categoryId;
  private String name;
  private String description;
  private String imageUrl;
  private Integer isActive;
  private BigDecimal minPrice;
  private Integer storeAvailable;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Integer getCategoryId() { return categoryId; }
  public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public String getImageUrl() { return imageUrl; }
  public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
  public Integer getIsActive() { return isActive; }
  public void setIsActive(Integer isActive) { this.isActive = isActive; }
  public BigDecimal getMinPrice() { return minPrice; }
  public void setMinPrice(BigDecimal minPrice) { this.minPrice = minPrice; }
  public Integer getStoreAvailable() { return storeAvailable; }
  public void setStoreAvailable(Integer storeAvailable) { this.storeAvailable = storeAvailable; }
}
