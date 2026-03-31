package com.smartcoffee.controller;

import com.smartcoffee.common.Result;
import com.smartcoffee.entity.Order;
import com.smartcoffee.service.OrderService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class OrderController {
  @Resource private OrderService orderService;
  @Resource private HttpServletRequest request;

  // 从鉴权拦截器注入的请求属性中获取当前登录用户 ID。
  private Long getCurrentUserId() {
    return (Long) request.getAttribute("currentUserId");
  }

  // 从鉴权拦截器注入的请求属性中获取当前登录用户角色。
  private String getCurrentUserRole() {
    return (String) request.getAttribute("currentUserRole");
  }

  @GetMapping("/api/user/orders")
  // 查询当前登录用户的订单列表，支持按状态筛选和分页。
  public Result<Result.PageData<Order>> listUserOrders(
      @RequestParam(required = false) Integer status,
      @RequestParam(defaultValue = "1") Integer pageNum,
      @RequestParam(defaultValue = "10") Integer pageSize,
      @RequestParam(required = false) Long userId) {
    Long currentUserId = getCurrentUserId();
    if (currentUserId == null) {
      return Result.fail(401, "未登录或登录状态已失效");
    }
    if (pageSize > 100) pageSize = 100;
    List<Order> list = orderService.list(currentUserId, status, pageNum, pageSize);
    int total = orderService.count(currentUserId, status);
    return Result.page(total, list);
  }

  @GetMapping("/api/admin/orders")
  // 查询后台订单列表，支持按用户、状态和分页条件筛选。
  public Result<Result.PageData<Order>> listAdminOrders(
      @RequestParam(required = false) Long userId,
      @RequestParam(required = false) Integer status,
      @RequestParam(defaultValue = "1") Integer pageNum,
      @RequestParam(defaultValue = "10") Integer pageSize) {
    if (pageSize > 100) pageSize = 100;
    List<Order> list = orderService.list(userId, status, pageNum, pageSize);
    int total = orderService.count(userId, status);
    return Result.page(total, list);
  }

  @GetMapping("/api/orders/{id}")
  // 查询订单详情，普通用户只能查看自己的订单，管理员可查看全部。
  public Result<Order> detail(@PathVariable Long id) {
    Order order = orderService.getById(id);
    if (order == null) {
      return Result.fail(404, "订单不存在");
    }
    Long currentUserId = getCurrentUserId();
    String role = getCurrentUserRole();
    boolean isAdmin = "ADMIN".equals(role);
    if (!isAdmin && (currentUserId == null || !currentUserId.equals(order.getUserId()))) {
      return Result.fail(403, "无权查看该订单");
    }
    return Result.ok(order);
  }

  @PostMapping("/api/orders")
  // 创建新订单，并强制使用当前登录用户身份作为下单人。
  public Result<Void> create(@RequestBody Order order) {
    Long currentUserId = getCurrentUserId();
    if (currentUserId == null) {
      return Result.fail(401, "未登录或登录状态已失效");
    }
    order.setUserId(currentUserId);
    try {
      orderService.placeOrder(order);
      return Result.ok();
    } catch (IllegalArgumentException e) {
      return Result.fail(400, e.getMessage());
    }
  }

  @PutMapping("/api/admin/orders/{id}/status")
  // 更新指定订单状态，供后台管理流程使用。
  public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
    if (status == null || status < 0 || status > 3) {
      return Result.fail(400, "订单状态不合法");
    }
    orderService.updateStatus(id, status);
    return Result.ok();
  }

  @DeleteMapping("/api/admin/orders/{id}")
  // 删除指定订单记录，供后台管理端操作。
  public Result<Void> delete(@PathVariable Long id) {
    orderService.delete(id);
    return Result.ok();
  }
}
