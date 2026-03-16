package com.smartcoffee.entity;

import java.math.BigDecimal;

public class ProductSku {
  private Long id;
  private Long productId;
  private String specName;
  private BigDecimal price;
  private Integer stock;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getProductId() { return productId; }
  public void setProductId(Long productId) { this.productId = productId; }
  public String getSpecName() { return specName; }
  public void setSpecName(String specName) { this.specName = specName; }
  public BigDecimal getPrice() { return price; }
  public void setPrice(BigDecimal price) { this.price = price; }
  public Integer getStock() { return stock; }
  public void setStock(Integer stock) { this.stock = stock; }
}
