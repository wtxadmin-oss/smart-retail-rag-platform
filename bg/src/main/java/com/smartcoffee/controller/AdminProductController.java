package com.smartcoffee.controller;

import com.smartcoffee.common.Result;
import com.smartcoffee.entity.Product;
import com.smartcoffee.mapper.ProductMapper;
import com.smartcoffee.service.ProductService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {
  @Resource private ProductMapper productMapper;
  @Resource private ProductService productService;

  @GetMapping
  // 按分类、关键字和上下架状态分页查询后台商品列表。
  public Result<Result.PageData<Product>> list(
      @RequestParam(required=false) Integer categoryId,
      @RequestParam(required=false) String keyword,
      @RequestParam(required=false) Integer isActive,
      @RequestParam(defaultValue = "1") Integer pageNum,
      @RequestParam(defaultValue = "10") Integer pageSize) {
    if (pageSize > 100) pageSize = 100;
    Integer offset = (pageNum - 1) * pageSize;
    List<Product> list = productMapper.listAdmin(categoryId, keyword, isActive, offset, pageSize);
    int total = productMapper.countAdmin(categoryId, keyword, isActive);
    return Result.page(total, list);
  }

  @PostMapping
  // 创建新的咖啡商品。
  public Result<Long> create(@RequestBody Product product) {
    return Result.ok(productService.create(product));
  }

  @PutMapping("/{id}")
  // 更新后台指定商品的信息。
  public Result<Void> update(@PathVariable Long id, @RequestBody Product product) {
    product.setId(id);
    productService.update(product);
    return Result.ok();
  }

  @DeleteMapping("/{id}")
  // 删除指定商品。
  public Result<Void> delete(@PathVariable Long id) {
    productService.delete(id);
    return Result.ok();
  }
}
