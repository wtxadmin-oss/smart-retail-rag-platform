package com.smartcoffee.controller;

import com.smartcoffee.common.Result;
import com.smartcoffee.entity.ProductSku;
import com.smartcoffee.service.ProductSkuService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/skus")
public class ProductSkuController {
  @Resource private ProductSkuService productSkuService;

  @GetMapping("/by-product/{productId}")
  public Result<List<ProductSku>> listByProduct(@PathVariable Long productId) {
    return Result.ok(productSkuService.listByProductId(productId));
  }

  @GetMapping("/{id}")
  public Result<ProductSku> detail(@PathVariable Long id) {
    return Result.ok(productSkuService.findById(id));
  }

  @PostMapping
  public Result<Long> create(@RequestBody ProductSku sku) {
    return Result.ok(productSkuService.create(sku));
  }

  @PutMapping("/{id}")
  public Result<Void> update(@PathVariable Long id, @RequestBody ProductSku sku) {
    sku.setId(id);
    productSkuService.update(sku);
    return Result.ok();
  }

  @DeleteMapping("/{id}")
  public Result<Void> delete(@PathVariable Long id) {
    productSkuService.delete(id);
    return Result.ok();
  }
}
