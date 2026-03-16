package com.smartcoffee.mapper;

import com.smartcoffee.entity.Order;
import com.smartcoffee.entity.OrderItem;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface OrderMapper {
    List<Order> list(@Param("userId") Long userId, @Param("status") Integer status, @Param("offset") Integer offset, @Param("limit") Integer limit);
    int count(@Param("userId") Long userId, @Param("status") Integer status);
    Order findById(@Param("id") Long id);
    List<OrderItem> listItemsByOrderId(@Param("orderId") Long orderId);
    int insert(Order order);
    int insertItem(OrderItem item);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    int deleteById(@Param("id") Long id);
}
