package com.smartcoffee.controller;

import com.smartcoffee.common.Result;
import com.smartcoffee.entity.Order;
import com.smartcoffee.entity.OrderItem;
import com.smartcoffee.service.OrderService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {
    @Resource private OrderService orderService;

    // 用户订单列表
    @GetMapping("/user/orders")
    public Result<Result.PageData<Order>> listUserOrders(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        List<Order> list = orderService.list(userId, status, pageNum, pageSize);
        int total = orderService.count(userId, status);
        return Result.page(total, list);
    }

    // 管理员订单列表
    @GetMapping("/admin/orders")
    public Result<Result.PageData<Order>> listAllOrders(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        List<Order> list = orderService.list(null, status, pageNum, pageSize);
        int total = orderService.count(null, status);
        return Result.page(total, list);
    }

    // 订单详情
    @GetMapping("/orders/{id}")
    public Result<Order> detail(@PathVariable Long id) {
        Order order = orderService.getById(id);
        if (order == null) return Result.fail(404, "订单不存在");
        return Result.ok(order);
    }

    // 订单明细列表
    @GetMapping("/orders/{id}/items")
    public Result<List<OrderItem>> items(@PathVariable Long id) {
        List<OrderItem> items = orderService.getItemsByOrderId(id);
        return Result.ok(items);
    }

    // 下单
    @PostMapping("/orders")
    public Result<Void> placeOrder(@RequestBody Order order) {
        orderService.placeOrder(order);
        return Result.ok();
    }

    // 更新状态
    @PutMapping("/orders/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        orderService.updateStatus(id, status);
        return Result.ok();
    }

    // 删除订单
    @DeleteMapping("/orders/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return Result.ok();
    }
}
