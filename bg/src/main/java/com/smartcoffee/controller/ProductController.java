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
  // 面向前台用户分页查询商品列表，可按分类和关键字筛选。
  public Result<Result.PageData<Product>> list(
      @RequestParam(required=false) Integer categoryId,
      @RequestParam(required=false) String keyword,
      @RequestParam(defaultValue = "1") Integer pageNum,
      @RequestParam(defaultValue = "10") Integer pageSize) {
    if (pageSize > 100) pageSize = 100;
    List<Product> list = productService.list(categoryId, keyword, pageNum, pageSize);
    int total = productService.count(categoryId, keyword);
    return Result.page(total, list);
  }

  @GetMapping("/{id}")
  // 查询单个商品的详情信息。
  public Result<Product> detail(@PathVariable Long id) {
    return Result.ok(productService.getById(id));
  }

  @GetMapping("/{id}/skus")
  // 查询某个商品对应的所有规格选项。
  public Result<List<ProductSku>> skus(@PathVariable Long id) {
    return Result.ok(productSkuMapper.listByProductId(id));
  }
}
