package com.smartcoffee.entity;

public class StoreProduct {
  private Long storeId;
  private Long productId;
  private Integer isAvailable;

  public Long getStoreId() { return storeId; }
  public void setStoreId(Long storeId) { this.storeId = storeId; }
  public Long getProductId() { return productId; }
  public void setProductId(Long productId) { this.productId = productId; }
  public Integer getIsAvailable() { return isAvailable; }
  public void setIsAvailable(Integer isAvailable) { this.isAvailable = isAvailable; }
}
