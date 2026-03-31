package com.smartcoffee.service.impl;

import com.smartcoffee.entity.Order;
import com.smartcoffee.entity.OrderItem;
import com.smartcoffee.entity.ProductSku;
import com.smartcoffee.entity.Product;
import com.smartcoffee.mapper.OrderMapper;
import com.smartcoffee.mapper.ProductSkuMapper;
import com.smartcoffee.mapper.ProductMapper;
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
    @Resource private ProductSkuMapper productSkuMapper;
    @Resource private ProductMapper productMapper;

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
            order.setStatus(1); // 待支付
        }
        
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new IllegalArgumentException("订单商品不能为空");
        }

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem it : order.getItems()) {
            if (it.getQuantity() == null || it.getQuantity() <= 0) {
                throw new IllegalArgumentException("商品数量必须大于0");
            }
            
            BigDecimal realPrice = BigDecimal.ZERO;
            if (it.getProductId() != null) {
                List<ProductSku> skus = productSkuMapper.listByProductId(it.getProductId());
                boolean found = false;
                if (skus != null && it.getSkuSpec() != null) {
                    for (ProductSku sku : skus) {
                        if (it.getSkuSpec().equals(sku.getSpecName())) {
                            realPrice = sku.getPrice();
                            found = true;
                            break;
                        }
                    }
                }
                
                Product p = productMapper.findById(it.getProductId());
                if (p == null) {
                    throw new IllegalArgumentException("无效的商品ID");
                }
                
                if (!found) {
                    if (p.getMinPrice() != null) {
                        realPrice = p.getMinPrice();
                    } else {
                        throw new IllegalArgumentException("无法确定商品价格");
                    }
                }
                
                // 补全商品名称和图片，以数据库为准
                it.setProductName(p.getName());
                it.setProductImage(p.getImageUrl());
                
            } else {
                throw new IllegalArgumentException("商品ID不能为空");
            }
            
            it.setPrice(realPrice);
            total = total.add(realPrice.multiply(new BigDecimal(it.getQuantity())));
        }
        order.setTotalAmount(total);

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
