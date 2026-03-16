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
  public Result<Result.PageData<Product>> list(
      @RequestParam(required=false) Integer categoryId,
      @RequestParam(required=false) String keyword,
      @RequestParam(required=false) Integer isActive,
      @RequestParam(defaultValue = "1") Integer pageNum,
      @RequestParam(defaultValue = "10") Integer pageSize) {
    Integer offset = (pageNum - 1) * pageSize;
    List<Product> list = productMapper.listAdmin(categoryId, keyword, isActive, offset, pageSize);
    int total = productMapper.countAdmin(categoryId, keyword, isActive);
    return Result.page(total, list);
  }

  @PostMapping
  public Result<Long> create(@RequestBody Product product) {
    return Result.ok(productService.create(product));
  }

  @PutMapping("/{id}")
  public Result<Void> update(@PathVariable Long id, @RequestBody Product product) {
    product.setId(id);
    productService.update(product);
    return Result.ok();
  }

  @DeleteMapping("/{id}")
  public Result<Void> delete(@PathVariable Long id) {
    productService.delete(id);
    return Result.ok();
  }
}
