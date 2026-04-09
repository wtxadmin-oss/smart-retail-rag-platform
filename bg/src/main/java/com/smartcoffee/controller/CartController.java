package com.smartcoffee.controller;

import com.smartcoffee.common.Result;
import com.smartcoffee.entity.CartItem;
import com.smartcoffee.mapper.CartMapper;
import com.smartcoffee.mapper.StoreMapper;
import com.smartcoffee.mapper.StoreProductMapper;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
  @Resource private CartMapper cartMapper;
  @Resource private StoreMapper storeMapper;
  @Resource private StoreProductMapper storeProductMapper;
  @Resource private HttpServletRequest request;

  // 从请求上下文中读取当前登录用户 ID，避免直接信任前端传参。
  private Long getCurrentUserId() {
    return (Long) request.getAttribute("currentUserId");
  }

  @GetMapping
  // 查询当前登录用户的购物车条目列表。
  public Result<List<CartItem>> list(@RequestParam(required = false) Long userId) {
    return Result.ok(cartMapper.listByUser(getCurrentUserId()));
  }

  @PostMapping("/add")
  // 向当前用户购物车中新增商品，已存在时累计数量。
  public Result<Void> add(@RequestBody CartItem item) {
    if (item.getProductId() == null) {
      return Result.fail(400, "商品 ID 不能为空");
    }
    if (item.getSkuId() == null) {
      return Result.fail(400, "规格 ID 不能为空");
    }
    if (item.getStoreId() == null) {
      return Result.fail(400, "门店 ID 不能为空");
    }
    if (item.getQuantity() == null || item.getQuantity() <= 0) {
      item.setQuantity(1);
    }
    if (storeMapper.findById(item.getStoreId()) == null) {
      return Result.fail(400, "门店不存在");
    }
    Integer available = storeProductMapper.isProductAvailable(item.getStoreId(), item.getProductId());
    if (available == null || available <= 0) {
      return Result.fail(400, "该商品当前门店不可售");
    }
    item.setUserId(getCurrentUserId());
    cartMapper.upsertAddQuantity(item);
    return Result.ok();
  }

  @PutMapping("/{id}/quantity")
  // 修改当前用户某个购物车条目的数量。
  public Result<Void> updateQuantity(@PathVariable Long id, @RequestParam Integer quantity) {
    if (quantity == null || quantity <= 0) {
      return Result.fail(400, "数量必须大于 0");
    }
    
    CartItem existing = cartMapper.findByIdAndUserId(id, getCurrentUserId());
    if (existing == null) {
      return Result.fail(403, "无权修改该购物车条目");
    }
    cartMapper.updateQuantity(id, quantity);
    return Result.ok();
  }

  @DeleteMapping("/{id}")
  // 删除当前用户购物车中的指定条目。
  public Result<Void> delete(@PathVariable Long id) {
    CartItem existing = cartMapper.findByIdAndUserId(id, getCurrentUserId());
    if (existing == null) {
      return Result.fail(403, "无权删除该购物车条目");
    }
    cartMapper.deleteById(id);
    return Result.ok();
  }

  @DeleteMapping("/clear")
  // 清空当前登录用户的整个购物车。
  public Result<Void> clear(@RequestParam(required = false) Long userId) {
    cartMapper.clearByUser(getCurrentUserId());
    return Result.ok();
  }
}
