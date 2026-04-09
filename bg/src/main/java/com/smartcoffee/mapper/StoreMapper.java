package com.smartcoffee.mapper;

import com.smartcoffee.entity.Store;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface StoreMapper {
  int initFromOrders();
  List<Store> list(@Param("offset") Integer offset, @Param("limit") Integer limit);
  int count();
  List<Store> listNeedGeo(@Param("limit") Integer limit);
  int updateGeo(@Param("id") Long id, @Param("lng") Double lng, @Param("lat") Double lat);
  List<Store> listActive();
  Store findById(@Param("id") Long id);
  int insert(Store store);
  int update(Store store);
  int deleteById(@Param("id") Long id);
}
