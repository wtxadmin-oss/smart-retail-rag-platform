package com.smartcoffee.mapper;

import com.smartcoffee.entity.ProductSku;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ProductSkuMapper {
  List<ProductSku> listByProductId(@Param("productId") Long productId);
  ProductSku findById(@Param("id") Long id);
  int insert(ProductSku sku);
  int update(ProductSku sku);
  int deleteById(@Param("id") Long id);
}
