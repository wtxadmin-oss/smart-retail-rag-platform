package com.smartcoffee.service.impl;

import com.smartcoffee.entity.Order;
import com.smartcoffee.entity.OrderItem;
import com.smartcoffee.mapper.OrderMapper;
import com.smartcoffee.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Resource private OrderMapper orderMapper;

    @Override
    public List<Order> list(Long userId, Integer status, Integer pageNum, Integer pageSize) {
        Integer offset = (pageNum - 1) * pageSize;
        return orderMapper.list(userId, status, offset, pageSize);
    }

    @Override
    public int count(Long userId, Integer status) {
        return orderMapper.count(userId, status);
    }

    @Override
    public Order getById(Long id) {
        Order order = orderMapper.findById(id);
        if (order != null) {
            order.setItems(orderMapper.listItemsByOrderId(id));
        }
        return order;
    }

    @Override
    public List<OrderItem> getItemsByOrderId(Long orderId) {
        return orderMapper.listItemsByOrderId(orderId);
    }

    @Override
    @Transactional
    public void placeOrder(Order order) {
        if (order.getOrderNo() == null || order.getOrderNo().trim().isEmpty()) {
            order.setOrderNo("SC" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        }
        if (order.getStatus() == null) {
            order.setStatus(0);
        }
        if (order.getTotalAmount() == null) {
            BigDecimal total = BigDecimal.ZERO;
            if (order.getItems() != null) {
                for (OrderItem it : order.getItems()) {
                    if (it.getPrice() != null && it.getQuantity() != null) {
                        total = total.add(it.getPrice().multiply(new BigDecimal(it.getQuantity())));
                    }
                }
            }
            order.setTotalAmount(total);
        }
        orderMapper.insert(order);
        for (OrderItem item : order.getItems()) {
            item.setOrderId(order.getId());
            orderMapper.insertItem(item);
        }
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        orderMapper.updateStatus(id, status);
    }

    @Override
    public void delete(Long id) {
        orderMapper.deleteById(id);
    }
}
