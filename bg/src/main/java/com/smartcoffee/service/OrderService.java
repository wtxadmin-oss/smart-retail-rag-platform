package com.smartcoffee.service;

import com.smartcoffee.entity.Order;
import com.smartcoffee.entity.OrderItem;
import java.util.List;

public interface OrderService {
    List<Order> list(Long userId, Integer status, Integer pageNum, Integer pageSize);
    int count(Long userId, Integer status);
    Order getById(Long id);
    List<OrderItem> getItemsByOrderId(Long orderId);
    void placeOrder(Order order);
    void updateStatus(Long id, Integer status);
    void delete(Long id);
}
