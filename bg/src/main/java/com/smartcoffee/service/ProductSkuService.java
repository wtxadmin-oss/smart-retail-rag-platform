package com.smartcoffee.service;

import com.smartcoffee.entity.ProductSku;
import java.util.List;

public interface ProductSkuService {
  List<ProductSku> listByProductId(Long productId);
  ProductSku findById(Long id);
  Long create(ProductSku sku);
  void update(ProductSku sku);
  void delete(Long id);
}
