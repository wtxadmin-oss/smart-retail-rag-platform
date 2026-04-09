package com.smartcoffee.mapper;

import com.smartcoffee.entity.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StoreProductMapper {
  List<Product> listStoreProducts(@Param("storeId") Long storeId,
                                  @Param("categoryId") Integer categoryId,
                                  @Param("keyword") String keyword,
                                  @Param("offset") Integer offset,
                                  @Param("limit") Integer limit);

  int countStoreProducts(@Param("storeId") Long storeId,
                         @Param("categoryId") Integer categoryId,
                         @Param("keyword") String keyword);

  List<Product> listStoreProductConfigs(@Param("storeId") Long storeId,
                                        @Param("categoryId") Integer categoryId,
                                        @Param("keyword") String keyword,
                                        @Param("offset") Integer offset,
                                        @Param("limit") Integer limit);

  int countStoreProductConfigs(@Param("storeId") Long storeId,
                               @Param("categoryId") Integer categoryId,
                               @Param("keyword") String keyword);

  int initializeMappingsForStore(@Param("storeId") Long storeId);

  int initializeMappingsForProduct(@Param("productId") Long productId,
                                   @Param("isAvailable") Integer isAvailable);

  int upsertAvailability(@Param("storeId") Long storeId,
                         @Param("productId") Long productId,
                         @Param("isAvailable") Integer isAvailable);

  Integer isProductAvailable(@Param("storeId") Long storeId,
                             @Param("productId") Long productId);
}
