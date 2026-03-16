package com.smartcoffee.entity;

import java.math.BigDecimal;
import java.util.Date;

public class CartItem {
  private Long id;
  private Long userId;
  private Long productId;
  private String productName;
  private String productImage;
  private String skuSpec;
  private BigDecimal price;
  private Integer quantity;
  private Date createTime;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }
  public Long getProductId() { return productId; }
  public void setProductId(Long productId) { this.productId = productId; }
  public String getProductName() { return productName; }
  public void setProductName(String productName) { this.productName = productName; }
  public String getProductImage() { return productImage; }
  public void setProductImage(String productImage) { this.productImage = productImage; }
  public String getSkuSpec() { return skuSpec; }
  public void setSkuSpec(String skuSpec) { this.skuSpec = skuSpec; }
  public BigDecimal getPrice() { return price; }
  public void setPrice(BigDecimal price) { this.price = price; }
  public Integer getQuantity() { return quantity; }
  public void setQuantity(Integer quantity) { this.quantity = quantity; }
  public Date getCreateTime() { return createTime; }
  public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
