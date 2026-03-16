package com.smartcoffee.mapper;

import com.smartcoffee.entity.Product;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ProductMapper {
  List<Product> list(@Param("categoryId") Integer categoryId, 
                  @Param("keyword") String keyword,
                  @Param("offset") Integer offset,
                  @Param("limit") Integer limit);
  int count(@Param("categoryId") Integer categoryId, @Param("keyword") String keyword);
  List<Product> listAdmin(@Param("categoryId") Integer categoryId,
                          @Param("keyword") String keyword,
                          @Param("isActive") Integer isActive,
                          @Param("offset") Integer offset,
                          @Param("limit") Integer limit);
  int countAdmin(@Param("categoryId") Integer categoryId,
                 @Param("keyword") String keyword,
                 @Param("isActive") Integer isActive);
  Product findById(@Param("id") Long id);
  int insert(Product product);
  int update(Product product);
  int deleteById(@Param("id") Long id);
}
