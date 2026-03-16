package com.smartcoffee.mapper;

import com.smartcoffee.entity.CartItem;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface CartMapper {
  List<CartItem> listByUser(@Param("userId") Long userId);
  CartItem findOne(@Param("userId") Long userId, @Param("productId") Long productId, @Param("skuSpec") String skuSpec);
  int insert(CartItem item);
  int upsertAddQuantity(CartItem item);
  int updateQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);
  int deleteById(@Param("id") Long id);
  int clearByUser(@Param("userId") Long userId);
}
