package com.smartcoffee.controller;

import com.smartcoffee.common.Result;
import com.smartcoffee.entity.Product;
import com.smartcoffee.entity.ProductSku;
import com.smartcoffee.mapper.ProductSkuMapper;
import com.smartcoffee.service.ProductService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
  @Resource private ProductService productService;
  @Resource private ProductSkuMapper productSkuMapper;

  @GetMapping
  public Result<Result.PageData<Product>> list(
      @RequestParam(required=false) Integer categoryId,
      @RequestParam(required=false) String keyword,
      @RequestParam(defaultValue = "1") Integer pageNum,
      @RequestParam(defaultValue = "10") Integer pageSize) {
    List<Product> list = productService.list(categoryId, keyword, pageNum, pageSize);
    int total = productService.count(categoryId, keyword);
    return Result.page(total, list);
  }

  @GetMapping("/{id}")
  public Result<Product> detail(@PathVariable Long id) {
    return Result.ok(productService.getById(id));
  }

  @GetMapping("/{id}/skus")
  public Result<List<ProductSku>> skus(@PathVariable Long id) {
    return Result.ok(productSkuMapper.listByProductId(id));
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
