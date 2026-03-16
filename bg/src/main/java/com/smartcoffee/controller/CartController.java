package com.smartcoffee.controller;

import com.smartcoffee.common.Result;
import com.smartcoffee.entity.CartItem;
import com.smartcoffee.mapper.CartMapper;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
  @Resource private CartMapper cartMapper;

  @GetMapping
  public Result<List<CartItem>> list(@RequestParam Long userId) {
    return Result.ok(cartMapper.listByUser(userId));
  }

  @PostMapping("/add")
  public Result<Void> add(@RequestBody CartItem item) {
    cartMapper.upsertAddQuantity(item);
    return Result.ok();
  }

  @PutMapping("/{id}/quantity")
  public Result<Void> updateQuantity(@PathVariable Long id, @RequestParam Integer quantity) {
    cartMapper.updateQuantity(id, quantity);
    return Result.ok();
  }

  @DeleteMapping("/{id}")
  public Result<Void> delete(@PathVariable Long id) {
    cartMapper.deleteById(id);
    return Result.ok();
  }

  @DeleteMapping("/clear")
  public Result<Void> clear(@RequestParam Long userId) {
    cartMapper.clearByUser(userId);
    return Result.ok();
  }
}
